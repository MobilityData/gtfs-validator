/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum.OPTIONAL;
import static org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum.RECOMMENDED;
import static org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum.REQUIRED;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnBasedEntityBuilder;
import org.mobilitydata.gtfsvalidator.columns.GtfsColumnStore;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.GtfsColumnDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityBuilder;
import org.mobilitydata.gtfsvalidator.table.GtfsEnumDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsSetter;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;

/**
 * Generates code for a GtfsTableDescriptor subclass for a specific GTFS table.
 *
 * <p>E.g., GtfsStopTableDescriptor class is generated for "stops.txt".
 */
public class TableDescriptorGenerator {

  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public TableDescriptorGenerator(GtfsFileDescriptor fileDescriptor) {
    this.fileDescriptor = fileDescriptor;
    this.classNames = new GtfsEntityClasses(fileDescriptor);
  }

  private static String gtfsTypeToParserMethod(FieldTypeEnum typeEnum) {
    return "as" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, typeEnum.toString());
  }

  private boolean cachingEnabled(final GtfsFieldDescriptor field) {
    // FIXME: Add a way to disable all caching with a command-line flag.
    if (field.cached()) {
      return true;
    }
    if (field.primaryKey().isPresent()) {
      // Single-column primary keys are not cached because caches are per-table, and primary keys
      // are unique to each row within so by definition they won't be used more than once and won't
      // benefit from being cached.
      if (fileDescriptor.hasSingleColumnPrimaryKey()) {
        return false;
      }

      // By comparison, multi-column primary keys are cacheable, since single columns of the key
      // are likely duplicated across rows (e.g. shape_id in shapes.txt, trip_id in stop_times.txt).
    }
    // Caching is enabled by default for certain field types.
    return field.type() == FieldTypeEnum.COLOR
        || field.type() == FieldTypeEnum.DATE
        || field.type() == FieldTypeEnum.TIME
        || field.type() == FieldTypeEnum.LANGUAGE_CODE
        || field.type() == FieldTypeEnum.ID;
  }

  public JavaFile generateGtfsDescriptorJavaFile() {
    return JavaFile.builder(fileDescriptor.packageName(), generateGtfsTableDescriptorClass())
        .build();
  }

  public TypeSpec generateGtfsTableDescriptorClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.tableDescriptorSimpleName())
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(GtfsTableDescriptor.class), classNames.entityTypeName()))
            .addAnnotation(Generated.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    typeSpec.addMethod(generateConstructor());

    typeSpec.addMethod(generateCreateContainerForInvalidStatusMethod());
    typeSpec.addMethod(generateCreateContainerForHeaderAndEntitiesMethod());
    typeSpec.addMethod(generateCreateEntityBuilderMethod());
    typeSpec.addMethod(generateCreateColumnBasedEntityBuilderMethod());
    typeSpec.addMethod(generateGetEntityClassMethod());
    typeSpec.addMethod(generateGetColumnsMethod());

    typeSpec.addMethod(generateGtfsFilenameMethod());
    typeSpec.addMethod(generateIsRecommendedMethod());
    typeSpec.addMethod(generateMaxCharsPerColumnMethod());

    return typeSpec.build();
  }

  private MethodSpec generateConstructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addStatement("setRequired($L)", fileDescriptor.required())
        .build();
  }

  private MethodSpec generateCreateContainerForHeaderAndEntitiesMethod() {
    return MethodSpec.methodBuilder("createContainerForHeaderAndEntities")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(ClassName.get(List.class), classNames.entityTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .returns(GtfsTableContainer.class)
        .addStatement(
            "return $T.forHeaderAndEntities(this, header, entities, noticeContainer)",
            classNames.tableContainerTypeName())
        .build();
  }

  private MethodSpec generateCreateContainerForInvalidStatusMethod() {
    return MethodSpec.methodBuilder("createContainerForInvalidStatus")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .returns(GtfsTableContainer.class)
        .addStatement("return new $T(this, tableStatus)", classNames.tableContainerTypeName())
        .build();
  }

  private MethodSpec generateCreateEntityBuilderMethod() {
    return MethodSpec.methodBuilder("createEntityBuilder")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(GtfsEntityBuilder.class)
        .addStatement("return new $T.Builder()", classNames.entityImplementationTypeName())
        .build();
  }

  private MethodSpec generateCreateColumnBasedEntityBuilderMethod() {
    return MethodSpec.methodBuilder("createColumnBasedEntityBuilder")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ClassName.get(GtfsColumnStore.class), "store")
        .returns(GtfsColumnBasedEntityBuilder.class)
        .addStatement(
            "return new $T.Builder(store)", classNames.columnBasedEntityImplementationTypeName())
        .build();
  }

  private MethodSpec generateGetEntityClassMethod() {
    return MethodSpec.methodBuilder("getEntityClass")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(Class.class)
        .addStatement("return $T.class", classNames.entityTypeName())
        .build();
  }

  private MethodSpec generateGetColumnsMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("getColumns")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(ImmutableList.class), ClassName.get(GtfsColumnDescriptor.class)))
            .addStatement(
                "$T.Builder<$T> builder = $T.builder()",
                ImmutableList.class,
                GtfsColumnDescriptor.class,
                ImmutableList.class);
    TypeName gtfsEntityType = classNames.entityTypeName();
    ClassName gtfsEntityBuilderClassName = classNames.entityBuilderTypeName();
    ClassName columnBasedGtfsEntityBuilderClassName = classNames.columnBasedEntityBuilderTypeName();
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      TypeName nameThisSomethingBetter =
          (field.type() == FieldTypeEnum.ENUM) ? ClassName.INT : ClassName.get(field.javaType());
      CodeBlock.Builder descriptor =
          CodeBlock.builder()
              .add("GtfsColumnDescriptor.builder()\n")
              .add(".setColumnName($T.$L)\n", gtfsEntityType, fieldNameField(field.name()))
              .add(".setHeaderRequired($L)\n", field.isHeaderRequired())
              .add(".setHeaderRecommended($L)\n", field.columnRecommended())
              .add(".setFieldLevel($T.$L)\n", FieldLevelEnum.class, getFieldLevel(field))
              .add(".setJavaType($T.class)\n", ClassName.get(field.javaType()))
              .add(".setFieldType($T.$L)\n", ClassName.get(FieldTypeEnum.class), field.type())
              .add(
                  ".setEntityBuilderSetter(($T) $T::$L)\n",
                  ParameterizedTypeName.get(
                      ClassName.get(GtfsSetter.class),
                      gtfsEntityBuilderClassName,
                      nameThisSomethingBetter.box()),
                  gtfsEntityBuilderClassName,
                  FieldNameConverter.setterMethodName(field.name()))
              .add(
                  ".setColumnBasedEntityBuilderSetter(($T) $T::$L)\n",
                  ParameterizedTypeName.get(
                      ClassName.get(GtfsSetter.class),
                      columnBasedGtfsEntityBuilderClassName,
                      nameThisSomethingBetter.box()),
                  columnBasedGtfsEntityBuilderClassName,
                  FieldNameConverter.setterMethodName(field.name()))
              .add(".setIsMixedCase($L)\n", field.mixedCase())
              .add(".setIsCached($L)\n", cachingEnabled(field));

      if (field.type() == FieldTypeEnum.ENUM) {
        descriptor.add(
            ".setEnumDescriptor($T.create($T::forNumber, $T.UNRECOGNIZED))",
            GtfsEnumDescriptor.class,
            ClassName.get(field.javaType()),
            ClassName.get(field.javaType()));
      }
      field
          .numberBounds()
          .ifPresent(
              bounds ->
                  descriptor.add(
                      ".setNumberBounds($T.$L)\n", RowParser.NumberBounds.class, bounds));
      method.addStatement("builder.add($L.build())", descriptor.build());
    }
    method.addStatement("return builder.build()");
    return method.build();
  }

  static FieldLevelEnum getFieldLevel(GtfsFieldDescriptor field) {
    return field.valueRequired() ? REQUIRED : field.recommended() ? RECOMMENDED : OPTIONAL;
  }

  private MethodSpec generateGtfsFilenameMethod() {
    return MethodSpec.methodBuilder("gtfsFilename")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addStatement("return $T.FILENAME", classNames.entityTypeName())
        .build();
  }

  private MethodSpec generateIsRecommendedMethod() {
    return MethodSpec.methodBuilder("isRecommended")
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addAnnotation(Override.class)
        .addStatement("return $L", fileDescriptor.recommended())
        .build();
  }

  private MethodSpec generateMaxCharsPerColumnMethod() {
    MethodSpec.Builder m =
        MethodSpec.methodBuilder("maxCharsPerColumn")
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(Optional.class, Integer.class))
            .addAnnotation(Override.class);
    if (fileDescriptor.maxCharsPerColumn().isPresent()) {
      m.addStatement("return Optional.of($L)", fileDescriptor.maxCharsPerColumn().get());
    } else {
      m.addStatement("return Optional.empty()");
    }
    return m.build();
  }
}
