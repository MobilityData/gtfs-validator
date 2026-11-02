package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getValueMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.setterMethodName;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.table.ColumnDictionary;
import org.mobilitydata.gtfsvalidator.table.ColumnarEntityList;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Generates the columns of a table declared with {@code storage = TableStorage.COLUMNAR}.
 *
 * <p>E.g., GtfsStopTimeColumns class is generated for "stop_times.txt". Each field is kept in an
 * array: indexed fields and times as codes into their distinct values, enums as the integer type of
 * the entity, other fields as their own type. Columns are split into chunks of {@link
 * ColumnarEntityList#CHUNK_SIZE} rows. Indexed fields and the sequence are allocated for every
 * chunk, other fields when a row of the chunk first has a value.
 */
public class TableColumnsGenerator {

  private enum Kind {
    INDEX,
    SEQUENCE,
    TIME,
    ENUM,
    VALUE
  }

  private static final String CSV_ROW_NUMBER = "csvRowNumber_";
  private static final String PRESENT = "present_";
  private static final String CHUNKS = "chunks_";
  private static final String TIMES = "times_";

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;
  private final ImmutableMap<String, TypeName> enumIntegerFieldTypes;

  public TableColumnsGenerator(
      GtfsFileDescriptor fileDescriptor, List<GtfsEnumDescriptor> enumDescriptors) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
    this.enumIntegerFieldTypes =
        EntityImplementationGenerator.createEnumIntegerFieldTypesMap(enumDescriptors);
  }

  public JavaFile generateGtfsColumnsJavaFile() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateGtfsColumnsClass()).build();
  }

  public TypeSpec generateGtfsColumnsClass() {
    ClassName entityType = classNames.entityImplementationTypeName();
    ClassName builderType = entityType.nestedClass("Builder");
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.tableColumnsSimpleName())
            .addSuperinterface(
                ParameterizedTypeName.get(
                    ClassName.get(ColumnarEntityList.Columns.class), entityType))
            .addAnnotation(Generated.class)
            .addModifiers(Modifier.FINAL);

    typeSpec.addField(
        FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(ThreadLocal.class), builderType),
                "BUILDER",
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.FINAL)
            .initializer("$T.withInitial($T::new)", ThreadLocal.class, builderType)
            .build());
    typeSpec.addField(int.class, CHUNKS, Modifier.PRIVATE);
    typeSpec.addField(chunkedColumnField(CSV_ROW_NUMBER, TypeName.INT));
    typeSpec.addField(chunkedColumnField(PRESENT, presentType()));
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addField(chunkedColumnField(field.name(), columnType(field)));
    }
    for (GtfsFieldDescriptor field : fileDescriptor.indices()) {
      typeSpec.addField(
          dictionaryField(dictionaryName(field), TypeName.get(field.javaType()).box()));
    }
    if (hasTimes()) {
      // Time columns share their distinct values, so that rows share their GtfsTime objects.
      typeSpec.addField(dictionaryField(TIMES, ClassName.get(GtfsTime.class)));
    }

    typeSpec.addMethod(generateAddChunkMethod());
    typeSpec.addMethod(generateSetMethod());
    typeSpec.addMethod(generateGetMethod(builderType));
    typeSpec.addMethod(generateCompactMethod());
    typeSpec.addMethod(generateIndexCountMethod());
    typeSpec.addMethod(generateDictionaryMethod());
    typeSpec.addMethod(generateCodeAtMethod());
    typeSpec.addMethod(generateCodeMethod());
    typeSpec.addMethod(generateSequenceIndexMethod());
    typeSpec.addMethod(generateSequenceAtMethod());
    typeSpec.addMethod(generateSequenceMethod());
    typeSpec.addMethod(generateDefinedKeysMethod());
    typeSpec.addMethod(generateDefinedValuesMethod());
    return typeSpec.build();
  }

  /** A column, as an array of chunks. */
  private static FieldSpec chunkedColumnField(String name, TypeName type) {
    return FieldSpec.builder(ArrayTypeName.of(ArrayTypeName.of(type)), name, Modifier.PRIVATE)
        .initializer("new $T[16][]", type)
        .build();
  }

  private static FieldSpec dictionaryField(String name, TypeName valueType) {
    return FieldSpec.builder(
            ParameterizedTypeName.get(ClassName.get(ColumnDictionary.class), valueType),
            name,
            Modifier.PRIVATE,
            Modifier.FINAL)
        .initializer("new $T<>()", ColumnDictionary.class)
        .build();
  }

  private Kind kind(GtfsFieldDescriptor field) {
    if (fileDescriptor.indices().contains(field)) {
      return Kind.INDEX;
    }
    if (fileDescriptor.sequenceIndex() >= 0 && field.equals(fileDescriptor.sequenceField().get())) {
      return Kind.SEQUENCE;
    }
    if (field.type() == FieldTypeEnum.TIME) {
      return Kind.TIME;
    }
    if (field.type() == FieldTypeEnum.ENUM) {
      return Kind.ENUM;
    }
    return Kind.VALUE;
  }

  /** Columns allocated for every row, since the indices need a value for every row. */
  private boolean isAlwaysAllocated(GtfsFieldDescriptor field) {
    return kind(field) == Kind.INDEX || kind(field) == Kind.SEQUENCE;
  }

  private boolean hasTimes() {
    return fileDescriptor.fields().stream().anyMatch((f) -> kind(f) == Kind.TIME);
  }

  private TypeName columnType(GtfsFieldDescriptor field) {
    switch (kind(field)) {
      case INDEX:
      case TIME:
        return TypeName.INT;
      case ENUM:
        return enumIntegerFieldTypes.getOrDefault(
            ((DeclaredType) field.javaType()).asElement().getSimpleName().toString(), TypeName.INT);
      default:
        return TypeName.get(field.javaType());
    }
  }

  private static String dictionaryName(GtfsFieldDescriptor field) {
    return field.name() + "Values_";
  }

  /** Type of the presence bits of a row: one bit per field. */
  private TypeName presentType() {
    int fieldCount = fileDescriptor.fields().size();
    return fieldCount <= 8
        ? TypeName.BYTE
        : fieldCount <= 16 ? TypeName.SHORT : fieldCount <= 32 ? TypeName.INT : TypeName.LONG;
  }

  private TypeName presentLocalType() {
    return presentType().equals(TypeName.LONG) ? TypeName.LONG : TypeName.INT;
  }

  private String mask(int fieldNumber) {
    return presentType().equals(TypeName.LONG)
        ? "0x" + Long.toHexString(1L << fieldNumber) + "L"
        : "0x" + Integer.toHexString(1 << fieldNumber);
  }

  /** Expression that turns the value of the entity into the value of its column. */
  private CodeBlock toColumn(GtfsFieldDescriptor field) {
    switch (kind(field)) {
      case INDEX:
        return CodeBlock.of("$L.code(entity.$L())", dictionaryName(field), field.name());
      case TIME:
        return CodeBlock.of("$L.code(entity.$L())", TIMES, field.name());
      case ENUM:
        return CodeBlock.of(
            "($T) entity.$L()", columnType(field), getValueMethodName(field.name()));
      default:
        return CodeBlock.of("entity.$L()", field.name());
    }
  }

  /** Expression that turns the value of a column into the value of the entity builder setter. */
  private CodeBlock fromColumn(GtfsFieldDescriptor field) {
    switch (kind(field)) {
      case INDEX:
        return CodeBlock.of("$L.value($L[c][i])", dictionaryName(field), field.name());
      case TIME:
        return CodeBlock.of("$L.value($L[c][i])", TIMES, field.name());
      case ENUM:
        return CodeBlock.of("(int) $L[c][i]", field.name());
      default:
        return CodeBlock.of("$L[c][i]", field.name());
    }
  }

  /**
   * Statements that set {@code c} and {@code i}, the chunk of {@code row} and its position in it.
   */
  private static CodeBlock chunkOfRow() {
    return CodeBlock.builder()
        .addStatement("int c = row >>> $T.CHUNK_SHIFT", ColumnarEntityList.class)
        .addStatement("int i = row & $T.CHUNK_MASK", ColumnarEntityList.class)
        .build();
  }

  private MethodSpec generateAddChunkMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("addChunk")
            .addModifiers(Modifier.PRIVATE)
            .beginControlFlow("if ($L == $L.length)", CHUNKS, CSV_ROW_NUMBER)
            .addStatement("int length = $L * 2", CHUNKS);
    List<String> columns = new ArrayList<>(List.of(CSV_ROW_NUMBER, PRESENT));
    fileDescriptor.fields().forEach((f) -> columns.add(f.name()));
    for (String column : columns) {
      method.addStatement("$L = $T.copyOf($L, length)", column, Arrays.class, column);
    }
    method.endControlFlow();
    addChunkAllocation(method, CSV_ROW_NUMBER, TypeName.INT);
    addChunkAllocation(method, PRESENT, presentType());
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      if (isAlwaysAllocated(field)) {
        addChunkAllocation(method, field.name(), columnType(field));
      }
    }
    return method.addStatement("$L++", CHUNKS).build();
  }

  private static void addChunkAllocation(MethodSpec.Builder method, String column, TypeName type) {
    method.addStatement(
        "$L[$L] = new $T[$T.CHUNK_SIZE]", column, CHUNKS, type, ColumnarEntityList.class);
  }

  private MethodSpec generateSetMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("set")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "row")
            .addParameter(classNames.entityImplementationTypeName(), "entity")
            .addCode(chunkOfRow())
            .beginControlFlow("if (c == $L)", CHUNKS)
            .addStatement("addChunk()")
            .endControlFlow()
            .addStatement("$T p = 0", presentLocalType())
            .addStatement("$L[c][i] = entity.csvRowNumber()", CSV_ROW_NUMBER);
    int fieldNumber = 0;
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      if (isAlwaysAllocated(field)) {
        method
            .addStatement("$L[c][i] = $L", field.name(), toColumn(field))
            .addStatement(
                "if (entity.$L()) p |= $L", hasMethodName(field.name()), mask(fieldNumber));
      } else {
        method
            .beginControlFlow("if (entity.$L())", hasMethodName(field.name()))
            .addStatement("p |= $L", mask(fieldNumber))
            .beginControlFlow("if ($L[c] == null)", field.name())
            .addStatement(
                "$L[c] = new $T[$T.CHUNK_SIZE]",
                field.name(),
                columnType(field),
                ColumnarEntityList.class)
            .endControlFlow()
            .addStatement("$L[c][i] = $L", field.name(), toColumn(field))
            .endControlFlow();
      }
      ++fieldNumber;
    }
    if (presentType().equals(presentLocalType())) {
      method.addStatement("$L[c][i] = p", PRESENT);
    } else {
      method.addStatement("$L[c][i] = ($T) p", PRESENT, presentType());
    }
    return method.build();
  }

  private MethodSpec generateGetMethod(ClassName builderType) {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("get")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "row")
            .returns(classNames.entityImplementationTypeName())
            .addCode(chunkOfRow())
            .addStatement("$T p = $L[c][i]", presentLocalType(), PRESENT)
            .addStatement("$T b = BUILDER.get()", builderType)
            .addStatement("b.clear()")
            .addStatement("b.setCsvRowNumber($L[c][i])", CSV_ROW_NUMBER);
    int fieldNumber = 0;
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      method.addStatement(
          "if ((p & $L) != 0) b.$L($L)",
          mask(fieldNumber),
          setterMethodName(field.name()),
          fromColumn(field));
      ++fieldNumber;
    }
    return method.addStatement("return b.build()").build();
  }

  private MethodSpec generateCompactMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("compact")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC);
    if (hasTimes()) {
      method.addStatement("$L.compact()", TIMES);
    }
    return method.build();
  }

  private MethodSpec generateIndexCountMethod() {
    return MethodSpec.methodBuilder("indexCount")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("return $L", fileDescriptor.indices().size())
        .build();
  }

  /** Generates a method that returns, for each index, the given expression of its field. */
  private MethodSpec generateByIndexMethod(
      MethodSpec.Builder method, Function<GtfsFieldDescriptor, CodeBlock> value) {
    List<GtfsFieldDescriptor> indices = fileDescriptor.indices();
    for (int i = 0; i < indices.size(); i++) {
      method.addStatement("if (index == $L) return $L", i, value.apply(indices.get(i)));
    }
    return method.addStatement("throw new $T(index)", IndexOutOfBoundsException.class).build();
  }

  private MethodSpec generateDictionaryMethod() {
    return generateByIndexMethod(
        MethodSpec.methodBuilder("dictionary")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "index")
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(ColumnDictionary.class),
                    WildcardTypeName.subtypeOf(Object.class))),
        (field) -> CodeBlock.of("$L", dictionaryName(field)));
  }

  private MethodSpec generateCodeAtMethod() {
    return generateByIndexMethod(
        MethodSpec.methodBuilder("codeAt")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "index")
            .addParameter(int.class, "row")
            .returns(int.class),
        (field) -> rowOfColumn(field.name()));
  }

  private static CodeBlock rowOfColumn(String column) {
    return CodeBlock.of(
        "$L[row >>> $T.CHUNK_SHIFT][row & $T.CHUNK_MASK]",
        column,
        ColumnarEntityList.class,
        ColumnarEntityList.class);
  }

  private MethodSpec generateCodeMethod() {
    return generateByIndexMethod(
        MethodSpec.methodBuilder("code")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "index")
            .addParameter(classNames.entityImplementationTypeName(), "entity")
            .returns(int.class),
        this::toColumn);
  }

  private MethodSpec generateSequenceIndexMethod() {
    return MethodSpec.methodBuilder("sequenceIndex")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("return $L", fileDescriptor.sequenceIndex())
        .build();
  }

  private MethodSpec generateSequenceAtMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("sequenceAt")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "row")
            .returns(int.class);
    if (fileDescriptor.sequenceIndex() >= 0) {
      method.addStatement("return $L", rowOfColumn(fileDescriptor.sequenceField().get().name()));
    } else {
      method.addStatement("return 0");
    }
    return method.build();
  }

  private MethodSpec generateSequenceMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("sequence")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(classNames.entityImplementationTypeName(), "entity")
            .returns(int.class);
    if (fileDescriptor.sequenceIndex() >= 0) {
      method.addStatement("return entity.$L()", fileDescriptor.sequenceField().get().name());
    } else {
      method.addStatement("return 0");
    }
    return method.build();
  }

  private MethodSpec generateDefinedKeysMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("definedKeys")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(classNames.entityImplementationTypeName(), "entity")
            .returns(String.class)
            .addStatement("$T<String> keys = new $T<>()", List.class, ArrayList.class);
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      method
          .beginControlFlow("if (entity.$L())", hasMethodName(keyField.name()))
          .addStatement(
              "keys.add($T.$L)",
              classNames.entityImplementationTypeName(),
              fieldNameField(keyField.name()))
          .endControlFlow();
    }
    return method.addStatement("return String.join(\",\", keys)").build();
  }

  private MethodSpec generateDefinedValuesMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("definedValues")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(classNames.entityImplementationTypeName(), "entity")
            .returns(Object.class)
            .addStatement("$T<Object> values = new $T<>()", List.class, ArrayList.class);
    for (GtfsFieldDescriptor keyField : fileDescriptor.primaryKeys()) {
      method
          .beginControlFlow("if (entity.$L())", hasMethodName(keyField.name()))
          .addStatement("values.add(entity.$L())", keyField.name())
          .endControlFlow();
    }
    return method
        .addStatement(
            "return values.stream().reduce((a, b) -> (a.toString() + \",\" + b.toString()))"
                + ".orElse(null)")
        .build();
  }
}
