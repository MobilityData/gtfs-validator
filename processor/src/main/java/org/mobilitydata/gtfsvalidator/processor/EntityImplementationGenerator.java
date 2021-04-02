/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.clearMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldDefaultName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getValueMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getterMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.setterMethodName;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.geometry.S2LatLng;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.math.BigDecimal;
import java.time.ZoneId;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Generates a class that represent a single parsed row of a GTFS table.
 *
 * <p>E.g., GtfsStop class is generated for "stops.txt".
 */
public class EntityImplementationGenerator {
  private enum ClassContext {
    ENTITY,
    BUILDER
  };

  private static final String CSV_ROW_NUMBER = "csvRowNumber";
  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public EntityImplementationGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
  }

  private static int lastBitFieldNumber(int fieldCount) {
    // Each bitField has 32 bits. We need bitField0_ to store 1..32 fields,
    // bitField0_ and bitField1_ for 33..64 fields etc.
    return (fieldCount - 1) / 32;
  }

  /**
   * Returns name of a bitField with the given index.
   *
   * <p>Example.
   *
   * <p>bitFieldName(1) == "bitField1_"
   *
   * @param i number of a bitField, starting from 0.
   * @return name of a bitField, e.g., bitField0_.
   */
  private static String bitFieldName(int i) {
    return "bitField" + i + "_";
  }

  /**
   * Returns name of a bitField to store bit for GTFS field with a given number.
   *
   * <p>Examples
   *
   * <ul>
   *   <li>bitFieldName(1) == "bitField0_"
   *   <li>bitFieldName(32) == "bitField1_"
   * </ul>
   *
   * @param fieldNumber number of a GTFS field, starting from 0.
   * @return name of a bitField, e.g., bitField0_.
   */
  private static String bitFieldForFieldNumber(int fieldNumber) {
    // Bits for fields 0..31 are stored in bitField0_,
    // for fields 32..63 - in bitField1_ etc.
    return bitFieldName(fieldNumber / 32);
  }

  private static String maskForFieldNumber(int fieldNumber) {
    return "0x" + Integer.toHexString(1 << (fieldNumber % 32));
  }

  private static CodeBlock getDefaultValue(GtfsFieldDescriptor field) {
    if (field.defaultValue().isPresent()) {
      String valueString = field.defaultValue().get();
      switch (field.type()) {
        case INTEGER:
        case ENUM:
          return CodeBlock.of(Integer.toString(Integer.parseInt(valueString)));
        case FLOAT:
        case LATITUDE:
        case LONGITUDE:
          return CodeBlock.of(Double.toString(Double.parseDouble(valueString)));
        case COLOR:
          return CodeBlock.of(
              "$T.fromInt(0x$L)",
              GtfsColor.class,
              Integer.toHexString(Integer.parseInt(valueString, 16)));
        case TEXT:
          return CodeBlock.of("$S", valueString);
        default:
          // TODO: Support all types or throw an exception.
          break;
      }
    }
    switch (field.type()) {
      case ENUM:
      case INTEGER:
      case FLOAT:
      case LATITUDE:
      case LONGITUDE:
        return CodeBlock.of("0");
      case DECIMAL:
        return CodeBlock.of("$T.ZERO", BigDecimal.class);
      case COLOR:
        return CodeBlock.of("$T.fromInt(0)", GtfsColor.class);
      case TEXT:
      case URL:
      case PHONE_NUMBER:
      case ID:
      case EMAIL:
        return CodeBlock.of("\"\"");
      case DATE:
        return CodeBlock.of("$T.fromEpochDay(0)", GtfsDate.class);
      case TIME:
        return CodeBlock.of("$T.fromSecondsSinceMidnight(0)", GtfsTime.class);
      case TIMEZONE:
        return CodeBlock.of("$T.of(\"UTC\")", ZoneId.class);
      case CURRENCY_CODE:
      case LANGUAGE_CODE:
      default:
        return CodeBlock.of("null");
    }
  }

  private static Class<?> nullabilityAnnotation(GtfsFieldDescriptor field) {
    return getDefaultValue(field).toString().equals("null") ? Nullable.class : Nonnull.class;
  }

  private static TypeName getClassFieldType(GtfsFieldDescriptor field) {
    if (field.type() == FieldTypeEnum.ENUM) {
      return TypeName.INT;
    }
    return TypeName.get(field.javaType());
  }

  public JavaFile generateGtfsEntityJavaFile() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateGtfsEntityClass()).build();
  }

  private void addEntityOrBuilderFields(TypeSpec.Builder typeSpec) {
    typeSpec.addField(long.class, CSV_ROW_NUMBER, Modifier.PRIVATE);
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addField(getClassFieldType(field), field.name(), Modifier.PRIVATE);
    }
    for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
      typeSpec.addField(
          FieldSpec.builder(int.class, "bitField" + i + "_", Modifier.PRIVATE).build());
    }
  }

  private void addDefaultValueFields(TypeSpec.Builder typeSpec) {
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addField(
          FieldSpec.builder(
                  getClassFieldType(field),
                  fieldDefaultName(field.name()),
                  Modifier.PUBLIC,
                  Modifier.STATIC,
                  Modifier.FINAL)
              .initializer(getDefaultValue(field))
              .build());
    }
  }

  public TypeSpec generateGtfsEntityClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.entityImplementationSimpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Generated.class)
            .addSuperinterface(GtfsEntity.class);

    for (TypeMirror superinterface : fileDescriptor.interfaces()) {
      typeSpec.addSuperinterface(superinterface);
    }

    typeSpec.addMethod(
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addJavadoc("Use {@link Builder} class to construct an object.")
            .build());

    addEntityOrBuilderFields(typeSpec);
    addDefaultValueFields(typeSpec);

    int fieldNumber = 0;

    typeSpec.addMethod(
        MethodSpec.methodBuilder(getterMethodName(CSV_ROW_NUMBER))
            .addModifiers(Modifier.PUBLIC)
            .returns(long.class)
            .addAnnotation(Override.class)
            .addStatement("return $L", CSV_ROW_NUMBER)
            .build());
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addMethod(generateGetterMethod(field, ClassContext.ENTITY));
      maybeAddEnumValueGetter(field, typeSpec);
      typeSpec.addMethod(generateHasMethod(field, fieldNumber));
      ++fieldNumber;
    }
    for (LatLonDescriptor latLonDescriptor : fileDescriptor.latLonFields()) {
      typeSpec.addMethod(generateGetLatLonMethod(latLonDescriptor));
      typeSpec.addMethod(generateHasLatLonMethod(latLonDescriptor));
    }

    typeSpec.addType(generateGtfsEntityBuilderClass());

    return typeSpec.build();
  }

  private MethodSpec generateGetterMethod(GtfsFieldDescriptor field, ClassContext classContext) {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder(getterMethodName(field.name()))
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.get(field.javaType()))
            .addAnnotation(nullabilityAnnotation(field));
    if (classContext.equals(ClassContext.ENTITY)) {
      method.addAnnotation(Override.class);
    }
    if (field.type().equals(FieldTypeEnum.ENUM)) {
      method
          .addStatement(
              "$T result = $T.forNumber($L)", field.javaType(), field.javaType(), field.name())
          .addStatement("return result == null ? $T.UNRECOGNIZED : result", field.javaType());
    } else {
      method.addStatement("return $L", field.name());
    }
    return method.build();
  }

  private void maybeAddEnumValueGetter(GtfsFieldDescriptor field, TypeSpec.Builder typeSpec) {
    if (!field.type().equals(FieldTypeEnum.ENUM)) {
      return;
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder(getValueMethodName(field.name()))
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .addStatement("return $L", field.name())
            .build());
  }

  private MethodSpec generateGetLatLonMethod(LatLonDescriptor latLonDescriptor) {
    return MethodSpec.methodBuilder(latLonDescriptor.latLonField())
        .addModifiers(Modifier.PUBLIC)
        .returns(S2LatLng.class)
        .addStatement(
            "return $T.fromDegrees($L, $L)",
            S2LatLng.class,
            latLonDescriptor.latField(),
            latLonDescriptor.lonField())
        .build();
  }

  private MethodSpec generateHasLatLonMethod(LatLonDescriptor latLonDescriptor) {
    return MethodSpec.methodBuilder(hasMethodName(latLonDescriptor.latLonField()))
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addStatement(
            "return $L() && $L()",
            hasMethodName(latLonDescriptor.latField()),
            hasMethodName(latLonDescriptor.lonField()))
        .build();
  }

  private MethodSpec generateSetterMethod(GtfsFieldDescriptor field, int fieldNumber) {
    return MethodSpec.methodBuilder(setterMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC)
        .returns(classNames.entityBuilderTypeName())
        .addAnnotation(Nonnull.class)
        .addParameter(
            ParameterSpec.builder(getClassFieldType(field).box(), "value")
                .addAnnotation(Nullable.class)
                .build())
        .beginControlFlow("if (value == null)")
        .addStatement("return $L()", clearMethodName(field.name()))
        .endControlFlow()
        .addStatement("$L = value", field.name())
        .addStatement(
            "$L |= $L", bitFieldForFieldNumber(fieldNumber), maskForFieldNumber(fieldNumber))
        .addStatement("return this")
        .build();
  }

  private void maybeAddEnumValueSetter(GtfsFieldDescriptor field, TypeSpec.Builder typeSpec) {
    if (!field.type().equals(FieldTypeEnum.ENUM)) {
      return;
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder(setterMethodName(field.name()))
            .addModifiers(Modifier.PUBLIC)
            .returns(classNames.entityBuilderTypeName())
            .addAnnotation(Nonnull.class)
            .addParameter(
                ParameterSpec.builder(TypeName.get(field.javaType()), "value")
                    .addAnnotation(Nullable.class)
                    .build())
            .addStatement(
                "return value == null ? $L() : $L(value.getNumber())",
                clearMethodName(field.name()),
                setterMethodName(field.name()))
            .build());
  }

  private MethodSpec generateClearMethod(GtfsFieldDescriptor field, int fieldNumber) {
    return MethodSpec.methodBuilder(clearMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC)
        .returns(classNames.entityBuilderTypeName())
        .addAnnotation(Nonnull.class)
        .addStatement("$L = $L", field.name(), fieldDefaultName(field.name()))
        .addStatement(
            "$L &= ~$L", bitFieldForFieldNumber(fieldNumber), maskForFieldNumber(fieldNumber))
        .addStatement("return this")
        .build();
  }

  private MethodSpec generateHasMethod(GtfsFieldDescriptor field, int fieldNumber) {
    return MethodSpec.methodBuilder(hasMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addStatement(
            "return ($L & $L) != 0",
            bitFieldForFieldNumber(fieldNumber),
            maskForFieldNumber(fieldNumber))
        .build();
  }

  public TypeSpec generateGtfsEntityBuilderClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder("Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

    typeSpec.addMethod(
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addComment("Initialize all fields to default values.")
            .addStatement("clear()")
            .build());

    addEntityOrBuilderFields(typeSpec);

    typeSpec.addMethod(
        MethodSpec.methodBuilder(getterMethodName(CSV_ROW_NUMBER))
            .addModifiers(Modifier.PUBLIC)
            .returns(long.class)
            .addStatement("return $L", CSV_ROW_NUMBER)
            .build());
    typeSpec.addMethod(
        MethodSpec.methodBuilder(setterMethodName(CSV_ROW_NUMBER))
            .addModifiers(Modifier.PUBLIC)
            .returns(classNames.entityBuilderTypeName())
            .addParameter(long.class, "value")
            .addStatement("$L = value", CSV_ROW_NUMBER)
            .addStatement("return this")
            .build());

    int fieldNumber = 0;
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addMethod(generateGetterMethod(field, ClassContext.BUILDER));
      maybeAddEnumValueGetter(field, typeSpec);
      typeSpec.addMethod(generateSetterMethod(field, fieldNumber));
      maybeAddEnumValueSetter(field, typeSpec);
      typeSpec.addMethod(generateClearMethod(field, fieldNumber));
      ++fieldNumber;
    }
    for (LatLonDescriptor latLonDescriptor : fileDescriptor.latLonFields()) {
      typeSpec.addMethod(generateGetLatLonMethod(latLonDescriptor));
    }

    typeSpec.addMethod(generateBuilderBuildMethod());
    typeSpec.addMethod(generateBuilderClearMethod());

    return typeSpec.build();
  }

  private MethodSpec generateBuilderBuildMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    MethodSpec.Builder buildMethod =
        MethodSpec.methodBuilder("build")
            .addModifiers(Modifier.PUBLIC)
            .returns(gtfsEntityType)
            .addStatement("$T entity = new $T()", gtfsEntityType, gtfsEntityType)
            .addStatement("entity.$L = this.$L", CSV_ROW_NUMBER, CSV_ROW_NUMBER);
    for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
      buildMethod.addStatement("entity.$L = this.$L", bitFieldName(i), bitFieldName(i));
    }
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      buildMethod.addStatement("entity.$L = this.$L", field.name(), field.name());
    }
    buildMethod.addStatement("return entity");

    return buildMethod.build();
  }

  private MethodSpec generateBuilderClearMethod() {
    MethodSpec.Builder buildMethod =
        MethodSpec.methodBuilder("clear")
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addStatement("$L = 0", CSV_ROW_NUMBER);
    for (int i = 0; i <= lastBitFieldNumber(fileDescriptor.fields().size()); ++i) {
      buildMethod.addStatement("$L = 0", bitFieldName(i));
    }
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      buildMethod.addStatement("$L = $L", field.name(), fieldDefaultName(field.name()));
    }
    return buildMethod.build();
  }
}
