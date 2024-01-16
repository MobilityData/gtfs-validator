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

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldDefaultName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getValueMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.getterMethodName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.gtfsColumnName;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.hasMethodName;

import com.google.common.collect.ImmutableMap;
import com.google.common.geometry.S2LatLng;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
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
public class EntityGenerator {
  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public EntityGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
  }

  static ImmutableMap<String, TypeName> createEnumIntegerFieldTypesMap(
      List<GtfsEnumDescriptor> enumDescriptors) {
    return enumDescriptors.stream()
        .collect(
            ImmutableMap.toImmutableMap(
                GtfsEnumDescriptor::name,
                EntityImplementationGenerator::chooseEnumIntegerFieldType));
  }

  static CodeBlock getDefaultValue(GtfsFieldDescriptor field) {
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

  static Class<?> nullabilityAnnotation(GtfsFieldDescriptor field) {
    return getDefaultValue(field).toString().equals("null") ? Nullable.class : Nonnull.class;
  }

  public JavaFile generateGtfsEntityJavaFile() {
    return JavaFile.builder(fileDescriptor.packageName(), generateGtfsEntityInterface()).build();
  }

  public TypeSpec generateGtfsEntityInterface() {
    TypeSpec.Builder typeSpec =
        TypeSpec.interfaceBuilder(classNames.entitySimpleName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Generated.class)
            .addSuperinterface(GtfsEntity.class);

    for (TypeMirror superinterface : fileDescriptor.interfaces()) {
      typeSpec.addSuperinterface(superinterface);
    }

    generateFilenameAndFieldNameConstants(typeSpec);
    addDefaultValueFields(typeSpec);

    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addMethod(generateGetterMethod(field));
      maybeAddEnumValueGetter(field, typeSpec);
      typeSpec.addMethod(generateHasMethod(field));
    }
    for (LatLonDescriptor latLonDescriptor : fileDescriptor.latLonFields()) {
      typeSpec.addMethod(generateGetLatLonMethod(latLonDescriptor));
      typeSpec.addMethod(generateHasLatLonMethod(latLonDescriptor));
    }

    typeSpec.addMethod(generateBuilderMethod());

    return typeSpec.build();
  }

  private void generateFilenameAndFieldNameConstants(TypeSpec.Builder typeSpec) {
    typeSpec.addField(
        FieldSpec.builder(
                String.class, "FILENAME", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("$S", fileDescriptor.filename())
            .build());
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addField(
          FieldSpec.builder(
                  String.class,
                  fieldNameField(field.name()),
                  Modifier.PUBLIC,
                  Modifier.STATIC,
                  Modifier.FINAL)
              .initializer("$S", gtfsColumnName(field.name()))
              .build());
    }
  }

  private void addDefaultValueFields(TypeSpec.Builder typeSpec) {
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      typeSpec.addField(
          FieldSpec.builder(
                  field.resolvedFieldTypeName(),
                  fieldDefaultName(field.name()),
                  Modifier.PUBLIC,
                  Modifier.STATIC,
                  Modifier.FINAL)
              .initializer(getDefaultValue(field))
              .build());
    }
  }

  private MethodSpec generateGetterMethod(GtfsFieldDescriptor field) {
    return MethodSpec.methodBuilder(getterMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(TypeName.get(field.javaType()))
        .addAnnotation(nullabilityAnnotation(field))
        .build();
  }

  private void maybeAddEnumValueGetter(GtfsFieldDescriptor field, TypeSpec.Builder typeSpec) {
    if (!field.type().equals(FieldTypeEnum.ENUM)) {
      return;
    }
    typeSpec.addMethod(
        MethodSpec.methodBuilder(getValueMethodName(field.name()))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(int.class)
            .build());
  }

  private MethodSpec generateGetLatLonMethod(LatLonDescriptor latLonDescriptor) {
    return MethodSpec.methodBuilder(latLonDescriptor.latLonField())
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .returns(S2LatLng.class)
        .addStatement(
            "return $T.fromDegrees($L(), $L())",
            S2LatLng.class,
            latLonDescriptor.latField(),
            latLonDescriptor.lonField())
        .build();
  }

  private MethodSpec generateHasLatLonMethod(LatLonDescriptor latLonDescriptor) {
    return MethodSpec.methodBuilder(hasMethodName(latLonDescriptor.latLonField()))
        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
        .returns(boolean.class)
        .addStatement(
            "return $L() && $L()",
            hasMethodName(latLonDescriptor.latField()),
            hasMethodName(latLonDescriptor.lonField()))
        .build();
  }

  private MethodSpec generateHasMethod(GtfsFieldDescriptor field) {
    return MethodSpec.methodBuilder(hasMethodName(field.name()))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(boolean.class)
        .build();
  }

  private MethodSpec generateBuilderMethod() {
    return MethodSpec.methodBuilder("builder")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(classNames.entityBuilderTypeName())
        .addStatement("return new $T()", classNames.entityBuilderTypeName())
        .build();
  }
}
