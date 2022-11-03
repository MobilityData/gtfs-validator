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
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldColumnIndex;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.FluentLogger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsLoader;
import org.mobilitydata.gtfsvalidator.notice.CsvParsingFailedNotice;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvFile;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.parsing.CsvRow;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;
import org.mobilitydata.gtfsvalidator.table.GtfsTableLoader;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

/**
 * Generates code for a loader for a GTFS table. The loader creates an instance of a corresponding
 * GTFS container class.
 *
 * <p>E.g., GtfsStopTableLoader class is generated for "stops.txt".
 */
public class TableLoaderGenerator {

  private static final int LOG_EVERY_N_ROWS = 200000;
  private final GtfsFileDescriptor fileDescriptor;
  private final GtfsEntityClasses classNames;

  public TableLoaderGenerator(GtfsFileDescriptor fileDescriptor) {
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

  private static String fieldColumnCache(GtfsFieldDescriptor field) {
    // There is a limited amount of possible values for certain field types, so it is more efficient
    // to use a single
    // cache for the whole table instead of dedicated caches for each field.
    switch (field.type()) {
      case TIME:
        return "timeCache";
      case DATE:
        return "dateCache";
      case COLOR:
        return "colorCache";
      case LANGUAGE_CODE:
        return "languageCodeCache";
      default:
        return field.name() + "ColumnCache";
    }
  }

  public JavaFile generateGtfsTableLoaderJavaFile() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateGtfsTableLoaderClass()).build();
  }

  public TypeSpec generateGtfsTableLoaderClass() {
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(classNames.tableLoaderSimpleName())
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(GtfsTableLoader.class),
                    classNames.entityImplementationTypeName()))
            .addAnnotation(GtfsLoader.class)
            .addAnnotation(Generated.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    typeSpec.addField(
        FieldSpec.builder(
                FluentLogger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$T.forEnclosingClass()", FluentLogger.class)
            .build());

    typeSpec.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
    typeSpec.addMethod(generateGtfsFilenameMethod());
    typeSpec.addMethod(generateIsRecommendedMethod());
    typeSpec.addMethod(generateIsRequiredMethod());
    typeSpec.addMethod(generateLoadMethod());
    typeSpec.addMethod(generateLoadMissingFileMethod());
    typeSpec.addMethod(generateGetColumnNamesMethod());
    typeSpec.addMethod(generateGetRequiredColumnNamesMethod());

    return typeSpec.build();
  }

  private MethodSpec generateGetColumnNamesMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    return MethodSpec.methodBuilder("getColumnNames")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(ParameterizedTypeName.get(Set.class, String.class))
        .addStatement(
            "return $T.of($L)",
            ImmutableSet.class,
            fileDescriptor.fields().stream()
                .map(field -> CodeBlock.of("$T.$L", gtfsEntityType, fieldNameField(field.name())))
                .collect(CodeBlock.joining(", ")))
        .build();
  }

  private MethodSpec generateGetRequiredColumnNamesMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    return MethodSpec.methodBuilder("getRequiredColumnNames")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(ParameterizedTypeName.get(Set.class, String.class))
        .addStatement(
            "return $T.of($L)",
            ImmutableSet.class,
            fileDescriptor.fields().stream()
                .filter(GtfsFieldDescriptor::required)
                .map(field -> CodeBlock.of("$T.$L", gtfsEntityType, fieldNameField(field.name())))
                .collect(CodeBlock.joining(", ")))
        .build();
  }

  private MethodSpec generateLoadMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    TypeName tableContainerTypeName = classNames.tableContainerTypeName();
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("load")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(InputStream.class, "inputStream")
            .addParameter(ValidatorProvider.class, "validatorProvider")
            .addParameter(NoticeContainer.class, "noticeContainer")
            .returns(
                ParameterizedTypeName.get(ClassName.get(GtfsTableContainer.class), gtfsEntityType));
    method
        .addStatement("$T csvFile", CsvFile.class)
        .beginControlFlow("try")
        .addStatement(
            "$T settings = $T.createDefaultParserSettings()",
            CsvParserSettings.class,
            CsvFile.class);
    if (fileDescriptor.maxCharsPerColumn().isPresent()) {
      method.addStatement(
          "settings.setMaxCharsPerColumn($L)", fileDescriptor.maxCharsPerColumn().get());
    }
    method
        .addStatement("csvFile = new $T(inputStream, gtfsFilename(), settings)", CsvFile.class)
        .nextControlFlow("catch ($T e)", TextParsingException.class)
        .addStatement(
            "noticeContainer.addValidationNotice(new $T(gtfsFilename(), e))",
            CsvParsingFailedNotice.class)
        .addStatement(
            "return new $T($T.INVALID_HEADERS)", tableContainerTypeName, TableStatus.class)
        .endControlFlow()
        .beginControlFlow("if (csvFile.isEmpty())")
        .addStatement(
            "noticeContainer.addValidationNotice(new $T(gtfsFilename()))", EmptyFileNotice.class)
        .addStatement("return new $T($T.EMPTY_FILE)", tableContainerTypeName, TableStatus.class)
        .endControlFlow()
        .addStatement("$T header = csvFile.getHeader()", CsvHeader.class)
        .addStatement("$T headerNotices = new $T()", NoticeContainer.class, NoticeContainer.class)
        .addStatement(
            "validatorProvider.getTableHeaderValidator().validate(gtfsFilename(), header, "
                + "getColumnNames(), getRequiredColumnNames(), headerNotices)")
        .addStatement("noticeContainer.addAll(headerNotices)")
        .beginControlFlow("if (headerNotices.hasValidationErrors())")
        .addStatement(
            "return new $T($T.INVALID_HEADERS)", tableContainerTypeName, TableStatus.class)
        .endControlFlow();

    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      method.addStatement(
          "final int $L = header.getColumnIndex($T.$L)",
          fieldColumnIndex(field.name()),
          gtfsEntityType,
          fieldNameField(field.name()));
    }

    // Several fields may reuse the same cache.
    Set<String> cacheVars = new HashSet<>();
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      if (cachingEnabled(field)) {
        String cacheVarName = fieldColumnCache(field);
        if (cacheVars.add(cacheVarName)) {
          method.addStatement(
              "final $T<$T> $L = new $T<>()",
              FieldCache.class,
              TypeName.get(field.javaType()),
              cacheVarName,
              FieldCache.class);
        }
      }
    }

    method
        .addStatement("final $T.Builder builder = new $T.Builder()", gtfsEntityType, gtfsEntityType)
        .addStatement(
            "final $T rowParser = new $T(gtfsFilename(), header,"
                + " validatorProvider.getFieldValidator())",
            RowParser.class,
            RowParser.class)
        .addStatement(
            "final $T entities = new $T<>()",
            ParameterizedTypeName.get(ClassName.get(List.class), gtfsEntityType),
            ArrayList.class)
        .addStatement("boolean hasUnparsableRows = false")
        .addStatement(
            "final $T singleEntityValidators = validatorProvider.createSingleEntityValidators("
                + "$T.class)",
            ParameterizedTypeName.get(
                ClassName.get(List.class),
                ParameterizedTypeName.get(
                    ClassName.get(SingleEntityValidator.class), gtfsEntityType)),
            gtfsEntityType);

    method
        .beginControlFlow("try")
        .beginControlFlow("for ($T row : csvFile)", CsvRow.class)
        .beginControlFlow("if (row.getRowNumber() % $L == 0)", LOG_EVERY_N_ROWS)
        .addStatement(
            "logger.atInfo().log($S, gtfsFilename(), row.getRowNumber())", "Reading %s, row %d")
        .endControlFlow()
        .addStatement("$T rowNotices = new $T()", NoticeContainer.class, NoticeContainer.class)
        .addStatement("rowParser.setRow(row, rowNotices)")
        .addStatement("final boolean validRowLength = rowParser.checkRowLength()")
        .beginControlFlow("if (validRowLength)")
        .addStatement("builder.clear()")
        .addStatement(
            "builder.$L(row.getRowNumber())", FieldNameConverter.setterMethodName("csvRowNumber"));

    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      CodeBlock fieldValue =
          CodeBlock.of(
              "rowParser.$L(\n$L, $T.$L$L)",
              gtfsTypeToParserMethod(field.type()),
              fieldColumnIndex(field.name()),
              FieldLevelEnum.class,
              field.required()
                  ? REQUIRED.name()
                  : (field.recommended() ? RECOMMENDED.name() : OPTIONAL.name()),
              field.numberBounds().isPresent()
                  ? ", RowParser.NumberBounds." + field.numberBounds().get()
                  : field.type() == FieldTypeEnum.ENUM
                      ? ",\n"
                          + field.javaType().toString()
                          + "::forNumber, "
                          + field.javaType().toString()
                          + ".UNRECOGNIZED"
                      : "");
      if (cachingEnabled(field)) {
        fieldValue = CodeBlock.of("$L.addIfAbsent($L)", fieldColumnCache(field), fieldValue);
      }
      method.addStatement(
          "builder.$L($L)", FieldNameConverter.setterMethodName(field.name()), fieldValue);
    }
    method.endControlFlow();

    method
        .beginControlFlow("if (rowNotices.hasValidationErrors())")
        .addStatement("hasUnparsableRows = true")
        .nextControlFlow("else if (validRowLength)")
        .addStatement("$T entity = builder.build()", gtfsEntityType)
        .addStatement(
            "$T.invokeSingleEntityValidators(entity, singleEntityValidators, noticeContainer)",
            ValidatorUtil.class)
        .addStatement("entities.add(entity)")
        .endControlFlow()
        .addStatement("noticeContainer.addAll(rowNotices)");

    method
        .endControlFlow() // end for (row)
        .nextControlFlow("catch ($T e)", TextParsingException.class)
        .addStatement(
            "noticeContainer.addValidationNotice(new $T(gtfsFilename(), e))",
            CsvParsingFailedNotice.class)
        .addStatement(
            "return new $T($T.UNPARSABLE_ROWS)", tableContainerTypeName, TableStatus.class)
        .nextControlFlow("finally");

    // Print statistics for cache efficiency.
    for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
      if (cachingEnabled(field)) {
        final String cacheName = fieldColumnCache(field);
        method.addStatement(
            "logger.atInfo().log("
                + "$S, gtfsFilename(), $T.$L, $L.getCacheSize(), $L.getLookupCount(), "
                + "$L.getHitRatio() * 100.0, $L.getMissRatio() * 100.0)",
            "Cache for %s %s: size = %d, lookup count = %d, hits = %.2f%%, misses = %.2f%%",
            gtfsEntityType,
            fieldNameField(field.name()),
            cacheName,
            cacheName,
            cacheName,
            cacheName);
      }
    }

    method.endControlFlow(); // end try-catch-finally

    method
        .beginControlFlow("if (hasUnparsableRows)")
        .addStatement(
            "logger.atSevere().log($S, gtfsFilename())", "Failed to parse some rows in %s")
        .addStatement(
            "return new $T($T.UNPARSABLE_ROWS)", tableContainerTypeName, TableStatus.class)
        .nextControlFlow("else")
        .addStatement(
            "$T table = $T.forHeaderAndEntities(header, entities, noticeContainer)",
            tableContainerTypeName,
            tableContainerTypeName)
        .addStatement(
            "$T.invokeSingleFileValidators(validatorProvider.createSingleFileValidators(table),"
                + " noticeContainer)",
            ValidatorUtil.class)
        .addStatement("return table")
        .endControlFlow();

    return method.build();
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

  private MethodSpec generateLoadMissingFileMethod() {
    TypeName gtfsEntityType = classNames.entityImplementationTypeName();
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("loadMissingFile")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ValidatorProvider.class, "validatorProvider")
            .addParameter(NoticeContainer.class, "noticeContainer")
            .returns(
                ParameterizedTypeName.get(ClassName.get(GtfsTableContainer.class), gtfsEntityType))
            .addAnnotation(Override.class)
            .addStatement(
                "$T table = new $T($T.MISSING_FILE)",
                classNames.tableContainerTypeName(),
                classNames.tableContainerTypeName(),
                TableStatus.class)
            .beginControlFlow("if (isRecommended())")
            .addStatement(
                "noticeContainer.addValidationNotice(new $T(gtfsFilename()))",
                MissingRecommendedFileNotice.class)
            .endControlFlow()
            .beginControlFlow("if (isRequired())")
            .addStatement(
                "noticeContainer.addValidationNotice(new $T(gtfsFilename()))",
                MissingRequiredFileNotice.class)
            .endControlFlow()
            .addStatement(
                "$T.invokeSingleFileValidators(validatorProvider.createSingleFileValidators(table),"
                    + " noticeContainer)",
                ValidatorUtil.class)
            .addStatement("return table");

    return method.build();
  }
}
