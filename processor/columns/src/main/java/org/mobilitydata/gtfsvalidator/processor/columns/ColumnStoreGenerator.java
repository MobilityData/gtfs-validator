package org.mobilitydata.gtfsvalidator.processor.columns;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.mobilitydata.gtfsvalidator.annotation.ColumnStoreTypes;
import org.mobilitydata.gtfsvalidator.annotation.Generated;

public class ColumnStoreGenerator {

  private static final String INITIAL_CAPACITY_FIELD = "INITIAL_CAPACITY";

  private static final String PRIMITIVE_PRESENCE_FIELD = "primitivePresenceByColumnIndex";

  TypeSpec generate(TypeElement typesElement) {
    List<ColumnType> columnTypes = getColumnTypeMirrors(typesElement);

    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(getStoreClassName(typesElement))
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Generated.class)
            .addSuperinterface(typesElement.asType())
            .superclass(ClassName.get("org.mobilitydata.gtfsvalidator.columns", "ColumnStoreBase"));

    generateFields(columnTypes, typeSpec);

    for (ColumnType columnType : columnTypes) {
      generateReserveMethod(columnType, typeSpec);
      generateHasMethod(columnType, typeSpec);
      generateGetMethod(columnType, typeSpec);
      generateSetMethod(columnType, typeSpec);
    }

    typeSpec.addMethod(generateTrimToSizeMethod(columnTypes));

    return typeSpec.build();
  }

  private static String getStoreClassName(TypeElement typesElement) {
    return typesElement.getSimpleName().toString().replace("Columns", "Column") + "Store";
  }

  private List<ColumnType> getColumnTypeMirrors(TypeElement typesElement) {
    return typesElement.getAnnotationMirrors().stream()
        .filter(am -> am.getAnnotationType().toString().equals(ColumnStoreTypes.class.getName()))
        .flatMap(am -> am.getElementValues().entrySet().stream())
        .filter(elem -> elem.getKey().toString().equals("value()"))
        .flatMap(elem -> ((List<AnnotationValue>) elem.getValue().getValue()).stream())
        .map(av -> constructColumnType((TypeMirror) av.getValue()))
        .collect(Collectors.toList());
  }

  private void generateFields(List<ColumnType> columnTypes, Builder typeSpec) {
    typeSpec.addField(
        FieldSpec.builder(
                int.class,
                INITIAL_CAPACITY_FIELD,
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.FINAL)
            .initializer("10")
            .build());
    typeSpec.addField(
        FieldSpec.builder(
                ParameterizedTypeName.get(List.class, byte[].class),
                PRIMITIVE_PRESENCE_FIELD,
                Modifier.PRIVATE,
                Modifier.FINAL)
            .initializer("new $T<>()", ArrayList.class)
            .build());

    for (ColumnType columnType : columnTypes) {
      ParameterizedTypeName listOfArraysType =
          ParameterizedTypeName.get(
              ClassName.get(List.class), ArrayTypeName.of(ClassName.get(columnType.type)));
      typeSpec.addField(
          FieldSpec.builder(
                  listOfArraysType, columnType.fieldName, Modifier.PRIVATE, Modifier.FINAL)
              .initializer("new $T<>()", ArrayList.class)
              .build());
    }
  }

  private void generateReserveMethod(ColumnType columnType, TypeSpec.Builder typeSpec) {
    CodeBlock.Builder body = CodeBlock.builder();
    if (columnType.type.getKind().isPrimitive()) {
      body.addStatement(
              "$T[] values = new $T[$L]",
              columnType.typeName,
              columnType.typeName,
              INITIAL_CAPACITY_FIELD)
          .addStatement("$L.add(values)", columnType.fieldName)
          .addStatement("int col = $L.size() - 1", columnType.fieldName)
          .addStatement(
              "reservePrimitivePresence($L, col, $L)",
              PRIMITIVE_PRESENCE_FIELD,
              INITIAL_CAPACITY_FIELD)
          .addStatement("return col");
    } else {
      body.addStatement(
          "return reserveColumn($L, $T.class, $L)",
          columnType.fieldName,
          columnType.typeName,
          INITIAL_CAPACITY_FIELD);
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder("reserve" + columnType.name + "Column")
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addCode(body.build())
            .build());
  }

  private void generateHasMethod(ColumnType columnType, TypeSpec.Builder typeSpec) {
    CodeBlock.Builder body = CodeBlock.builder();
    if (columnType.type.getKind().isPrimitive()) {
      body.addStatement("return hasPrimitive($L, columnIndex, row)", PRIMITIVE_PRESENCE_FIELD);
    } else {
      body.addStatement("return hasValue($L, columnIndex, row)", columnType.fieldName);
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder("has" + columnType.name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "columnIndex")
            .addParameter(int.class, "row")
            .returns(boolean.class)
            .addCode(body.build())
            .build());
  }

  private void generateGetMethod(ColumnType columnType, TypeSpec.Builder typeSpec) {
    CodeBlock.Builder body = CodeBlock.builder();
    if (columnType.type.getKind().isPrimitive()) {
      body.addStatement(
          "return has$L(columnIndex, row) ? $L.get(columnIndex)[row] : defaultValue",
          columnType.name,
          columnType.fieldName);
    } else {
      body.addStatement(
          "return getValue($L, columnIndex, row, defaultValue)", columnType.fieldName);
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder("get" + columnType.name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "columnIndex")
            .addParameter(int.class, "row")
            .addParameter(columnType.typeName, "defaultValue")
            .returns(columnType.typeName)
            .addCode(body.build())
            .build());
  }

  private void generateSetMethod(ColumnType columnType, TypeSpec.Builder typeSpec) {
    CodeBlock.Builder body = CodeBlock.builder();
    if (columnType.type.getKind().isPrimitive()) {
      body.add(generatePrimitiveSetMethod(columnType));
    } else {
      body.addStatement("setValue($L, columnIndex, row, value)", columnType.fieldName);
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder("set" + columnType.name)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(int.class, "columnIndex")
            .addParameter(int.class, "row")
            .addParameter(columnType.typeName, "value")
            .addCode(body.build())
            .build());
  }

  private CodeBlock generatePrimitiveSetMethod(ColumnType columnType) {
    return CodeBlock.builder()
        .addStatement(
            "$T[] values = $L.get(columnIndex)", columnType.typeName, columnType.fieldName)
        .beginControlFlow("if (values.length <= row)")
        .addStatement("int newSize = calculateNewCapacity(values.length, row + 1)")
        .addStatement("values = Arrays.copyOf(values, newSize)")
        .addStatement("$L.set(columnIndex, values)", columnType.fieldName)
        .endControlFlow()
        .addStatement("values[row] = value")
        .addStatement("setPrimitivePresence($L, columnIndex, row)", PRIMITIVE_PRESENCE_FIELD)
        .build();
  }

  private MethodSpec generateTrimToSizeMethod(List<ColumnType> columnTypes) {
    CodeBlock.Builder code = CodeBlock.builder();
    for (ColumnType columnType : columnTypes) {
      if (columnType.type.getKind().isPrimitive()) {
        code.beginControlFlow("for (int i=0; i < $L.size(); ++i)", columnType.fieldName)
            .addStatement("$T[] values = $L.get(i)", columnType.typeName, columnType.fieldName)
            .addStatement(
                "$L.set(i, $T.copyOf(values, newSize))", columnType.fieldName, Arrays.class)
            .endControlFlow();
      } else {
        code.addStatement("trimToSize($L, newSize)", columnType.fieldName);
      }
    }
    return MethodSpec.methodBuilder("trimToSize")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(int.class, "newSize")
        .addCode(code.build())
        .build();
  }

  private static ColumnType constructColumnType(TypeMirror typeMirror) {
    String name = getName(typeMirror);
    String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name) + "sByColumnIndex";
    return new ColumnType(name, fieldName, typeMirror);
  }

  private static String getName(TypeMirror columnType) {
    switch (columnType.getKind()) {
      case BYTE:
        return "Byte";
      case SHORT:
        return "Short";
      case INT:
        return "Int";
      case DOUBLE:
        return "Double";
      case DECLARED:
        return ((DeclaredType) columnType).asElement().getSimpleName().toString();
      default:
        throw new UnsupportedOperationException("Unsupported kind: " + columnType.getKind());
    }
  }

  private static class ColumnType {
    final String name;
    final String fieldName;
    final TypeMirror type;
    final TypeName typeName;

    ColumnType(String name, String fieldName, TypeMirror type) {
      this.name = name;
      this.fieldName = fieldName;
      this.type = type;
      this.typeName = ClassName.get(type);
    }
  }
}
