package org.mobilitydata.gtfsvalidator.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnAssignments;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedCollectionFactory;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedEntityBuilder;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnStore;

public class ColumnBasedEntityBuilderGenerator {

  static final String CLASS_NAME = "Builder";

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;
  private final GtfsFieldDescriptor csvRowNumberField;

  public ColumnBasedEntityBuilderGenerator(
      GtfsFileDescriptor fileDescriptor,
      GtfsEntityClasses classNames,
      TypeMirror primitiveIntType) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = classNames;
    this.csvRowNumberField =
        GtfsFieldDescriptor.builder()
            .setName("csvRowNumber")
            .setType(FieldTypeEnum.INTEGER)
            .setJavaType(primitiveIntType)
            .build();
  }

  public TypeSpec create() {
    TypeSpec.Builder typeBuilder =
        TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(GtfsColumnBasedEntityBuilder.class),
                    ClassName.get("", classNames.columnBasedEntityImplementationSimpleName())));

    generateFields(typeBuilder);

    typeBuilder.addMethod(generateConstructor());
    typeBuilder.addMethod(generateCsvRowNumberGetter());
    generateSetters(typeBuilder);
    typeBuilder.addMethod(generateBuildMethod());
    typeBuilder.addMethod(generateCloseMethod());
    typeBuilder.addMethod(generateAssignmentsMethod());
    typeBuilder.addMethod(generateCollectionFactoryMethod());

    return typeBuilder.build();
  }

  private void generateFields(Builder typeBuilder) {
    typeBuilder.addField(
        FieldSpec.builder(ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME, "assignments")
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .initializer("new $T()", ColumnBasedEntityColumnAssignmentsGenerator.TYPE_NAME)
            .build());
  }

  private MethodSpec generateConstructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ClassName.get(GtfsColumnStore.class), "store")
        .addStatement("super(store)")
        .build();
  }

  private MethodSpec generateCsvRowNumberGetter() {
    return MethodSpec.methodBuilder("csvRowNumber")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(int.class)
        .addStatement("return store.getInt(assignments.csvRowNumber(), rowIndex, -1)")
        .build();
  }

  private void generateSetters(TypeSpec.Builder typeSpec) {
    generateSetter(csvRowNumberField, typeSpec);
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      generateSetter(field, typeSpec);
    }
  }

  private static void generateSetter(GtfsFieldDescriptor field, TypeSpec.Builder typeSpec) {
    String columnGetterName = ColumnBasedEntityImplementationGenerator.getFieldTypeColumn(field);

    CodeBlock valueExpression = CodeBlock.of("value");
    if (field.type() == FieldTypeEnum.ENUM) {
      valueExpression = CodeBlock.of("value.$LValue()", field.enumDescriptor().get().fieldType());
    }

    CodeBlock.Builder code = CodeBlock.builder();
    if (!field.javaType().getKind().isPrimitive()) {
      code.beginControlFlow("if (value == null)").addStatement("return this").endControlFlow();
    }
    code.beginControlFlow("if (assignments.$L() == -1)", field.name())
        .addStatement(
            "assignments.$L(store.reserve$LColumn())",
            FieldNameConverter.setterMethodName(field.name()),
            columnGetterName)
        .endControlFlow()
        .addStatement(
            "store.set$L(assignments.$L(), rowIndex, $L)",
            columnGetterName,
            field.name(),
            valueExpression);

    TypeName parameterType =
        field.type() == FieldTypeEnum.ENUM
            ? ClassName.get(Integer.class)
            : TypeName.get(field.javaType());
    typeSpec.addMethod(
        MethodSpec.methodBuilder(FieldNameConverter.setterMethodName(field.name()))
            .addModifiers(Modifier.PUBLIC)
            .addParameter(parameterType, "value")
            .returns(ClassName.get("", "Builder"))
            .addCode(code.build())
            .addStatement("return this")
            .build());
  }

  private MethodSpec generateBuildMethod() {
    ClassName entityType =
        ClassName.get("", classNames.columnBasedEntityImplementationSimpleName());
    return MethodSpec.methodBuilder("build")
        .addModifiers(Modifier.PUBLIC)
        .returns(entityType)
        .addStatement("return new $T(store, assignments, rowIndex)", entityType)
        .build();
  }

  private MethodSpec generateCloseMethod() {
    return MethodSpec.methodBuilder("close")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .addStatement("store.trimToSize(rowIndex + 1)")
        .build();
  }

  private MethodSpec generateAssignmentsMethod() {
    return MethodSpec.methodBuilder("getAssignments")
        .addModifiers(Modifier.PROTECTED)
        .addAnnotation(Override.class)
        .returns(GtfsColumnAssignments.class)
        .addStatement("return assignments")
        .build();
  }

  private MethodSpec generateCollectionFactoryMethod() {
    return MethodSpec.methodBuilder("getCollectionFactory")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(
            ParameterizedTypeName.get(
                ClassName.get(GtfsColumnBasedCollectionFactory.class),
                classNames.columnBasedEntityImplementationTypeName()))
        .addStatement(
            "return new $T<$T>(store, assignments, $T::create)",
            GtfsColumnBasedCollectionFactory.class,
            classNames.columnBasedEntityImplementationTypeName(),
            classNames.columnBasedEntityImplementationTypeName())
        .build();
  }
}
