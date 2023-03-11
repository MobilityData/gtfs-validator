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
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.GtfsColumnDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityBuilder;
import org.mobilitydata.gtfsvalidator.table.GtfsFieldLoader;
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
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateGtfsTableDescriptorClass()).build();
  }

  public TypeSpec generateGtfsTableDescriptorClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.tableDescriptorSimpleName())
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(GtfsTableDescriptor.class),
                    classNames.entityImplementationTypeName()))
            .addAnnotation(Generated.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    typeSpec.addMethod(generateCreateContainerForInvalidStatusMethod());
    typeSpec.addMethod(generateCreateContainerForHeaderAndEntitiesMethod());
    typeSpec.addMethod(generateCreateEntityBuilderMethod());
    typeSpec.addMethod(generateGetEntityClassMethod());
    typeSpec.addMethod(generateGetColumnsMethod());
    typeSpec.addMethod(generateGetFieldLoadersMethod());

    typeSpec.addMethod(generateGtfsFilenameMethod());
    typeSpec.addMethod(generateIsRecommendedMethod());
    typeSpec.addMethod(generateIsRequiredMethod());

    return typeSpec.build();
  }

  private MethodSpec generateCreateContainerForHeaderAndEntitiesMethod() {
    return MethodSpec.methodBuilder("createContainerForHeaderAndEntities")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(CsvHeader.class, "header")
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(List.class), classNames.entityImplementationTypeName()),
            "entities")
        .addParameter(NoticeContainer.class, "noticeContainer")
        .returns(GtfsTableContainer.class)
        .addStatement(
            "return $T.forHeaderAndEntities(header, entities, noticeContainer)",
            classNames.tableContainerTypeName())
        .build();
  }

  private MethodSpec generateCreateContainerForInvalidStatusMethod() {
    return MethodSpec.methodBuilder("createContainerForInvalidStatus")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(GtfsTableContainer.TableStatus.class, "tableStatus")
        .returns(GtfsTableContainer.class)
        .addStatement("return new $T(tableStatus)", classNames.tableContainerTypeName())
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

  private MethodSpec generateGetEntityClassMethod() {
    return MethodSpec.methodBuilder("getEntityClass")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(Class.class)
        .addStatement("return $T.class", classNames.entityImplementationTypeName())
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
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      CodeBlock.Builder descriptor =
          CodeBlock.builder()
              .add(
                  "GtfsColumnDescriptor.builder()\n"
                      + ".setColumnName($T.$L)\n"
                      + ".setHeaderRequired($L)\n"
                      + ".setFieldLevel($T.$L)\n"
                      + ".setIsMixedCase($L)\n"
                      + ".setIsCached($L)\n",
                  gtfsEntityType,
                  fieldNameField(field.name()),
                  field.isHeaderRequired(),
                  FieldLevelEnum.class,
                  getFieldLevel(field),
                  field.mixedCase(),
                  cachingEnabled(field));
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

  private MethodSpec generateGetFieldLoadersMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("getFieldLoaders")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(ImmutableMap.class),
                    ClassName.get(String.class),
                    ClassName.get(GtfsFieldLoader.class)))
            .addStatement(
                "$T.Builder<$T, $T> builder = $T.builder()",
                ImmutableMap.class,
                String.class,
                GtfsFieldLoader.class,
                ImmutableMap.class);
    ClassName gtfsEntityType = classNames.entityImplementationTypeName();
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      TypeName boxedType =
          field.type() == FieldTypeEnum.ENUM
              ? ClassName.get(Integer.class)
              : ClassName.get(field.javaType()).box();
      TypeSpec.Builder loaderClass =
          TypeSpec.anonymousClassBuilder("")
              .addSuperinterface(
                  ParameterizedTypeName.get(
                      ClassName.get(GtfsFieldLoader.class),
                      gtfsEntityType.nestedClass("Builder"),
                      boxedType));
      MethodSpec.Builder loadMethod =
          MethodSpec.methodBuilder("load")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(RowParser.class, "rowParser")
              .addParameter(int.class, "columnIndex")
              .addParameter(GtfsColumnDescriptor.class, "columnDescriptor")
              .addParameter(
                  ParameterizedTypeName.get(ClassName.get(FieldCache.class), boxedType),
                  "fieldCache")
              .addParameter(gtfsEntityType.nestedClass("Builder"), "builder");

      CodeBlock fieldValue =
          field.type() == FieldTypeEnum.ENUM
              ? CodeBlock.of(
                  "rowParser.asEnum(columnIndex, columnDescriptor.fieldLevel(), $T::forNumber,"
                      + " $T.UNRECOGNIZED)",
                  ClassName.get(field.javaType()),
                  ClassName.get(field.javaType()))
              : CodeBlock.of(
                  "rowParser.$L(columnIndex, columnDescriptor.fieldLevel()$L)",
                  gtfsTypeToParserMethod(field.type()),
                  field.numberBounds().isPresent()
                      ? ", RowParser.NumberBounds." + field.numberBounds().get()
                      : "");

      loadMethod.addStatement(
          "builder.$L(\naddToCacheIfPresent(\n$L, fieldCache))",
          FieldNameConverter.setterMethodName(field.name()),
          fieldValue);
      loaderClass.addMethod(loadMethod.build());

      method.addStatement(
          "builder.put($T.$L, $L)",
          gtfsEntityType,
          fieldNameField(field.name()),
          loaderClass.build());
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
        .addStatement("return $T.FILENAME", classNames.entityImplementationTypeName())
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

  private MethodSpec generateIsRequiredMethod() {
    return MethodSpec.methodBuilder("isRequired")
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addAnnotation(Override.class)
        .addStatement("return $L", fileDescriptor.required())
        .build();
  }
}
