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
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsLoader;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvFile;
import org.mobilitydata.gtfsvalidator.parsing.CsvRow;
import org.mobilitydata.gtfsvalidator.parsing.FieldCache;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableLoader;
import org.mobilitydata.gtfsvalidator.validator.TableHeaderValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import javax.lang.model.element.Modifier;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldColumnIndex;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.fieldNameField;
import static org.mobilitydata.gtfsvalidator.processor.FieldNameConverter.gtfsColumnName;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

/**
 * Generates code for a loader for a GTFS table. The loader creates an instance of a corresponding GTFS container class.
 * <p>
 * E.g., GtfsStopTableLoader class is generated for "stops.txt".
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

    private static boolean cachingEnabled(final GtfsFieldDescriptor field) {
        // FIXME: Add a way to disable all caching with a command-line flag.
        if (field.cached()) {
            return true;
        }
        if (field.primaryKey()) {
            // Primary keys are not cached because caches are per-table, and primary keys are unique to each row within
            // the table, so by definition they won't be used more than once and won't benefit from being cached.
            return false;
        }
        // Caching is enabled by default for certain field types.
        return field.type() == FieldTypeEnum.COLOR
                || field.type() == FieldTypeEnum.DATE
                || field.type() == FieldTypeEnum.TIME
                || field.type() == FieldTypeEnum.LANGUAGE_CODE
                || field.type() == FieldTypeEnum.ID;
    }

    private static String fieldColumnCache(GtfsFieldDescriptor field) {
        // There is a limited amount of possible values for certain field types, so it is more efficient to use a single
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
        return JavaFile.builder(
                TABLE_PACKAGE_NAME, generateGtfsTableLoaderClass()).build();
    }

    public TypeSpec generateGtfsTableLoaderClass() {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classNames.tableLoaderSimpleName())
                .superclass(ParameterizedTypeName.get(ClassName.get(GtfsTableLoader.class), classNames.entityImplementationTypeName()))
                .addAnnotation(GtfsLoader.class)
                .addAnnotation(Generated.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        typeSpec.addField(
                FieldSpec.builder(FluentLogger.class, "logger", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.forEnclosingClass()", FluentLogger.class).build());
        typeSpec.addField(
                FieldSpec.builder(String.class, "FILENAME", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", fileDescriptor.filename()).build());
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            typeSpec.addField(
                    FieldSpec.builder(String.class, fieldNameField(field.name()), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$S", gtfsColumnName(field.name())).build());
        }

        typeSpec.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
        typeSpec.addMethod(generateGtfsFilenameMethod());
        typeSpec.addMethod(generateIsRequiredMethod());
        typeSpec.addMethod(generateLoadMethod());
        typeSpec.addMethod(generateLoadMissingFileMethod());
        typeSpec.addMethod(generateGetColumnNamesMethod());
        typeSpec.addMethod(generateGetRequiredColumnNamesMethod());

        return typeSpec.build();
    }

    private MethodSpec generateGetColumnNamesMethod() {
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.ensureCapacity(fileDescriptor.fields().size());
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            fieldNames.add(fieldNameField(field.name()));
        }

        MethodSpec.Builder method = MethodSpec.methodBuilder("getColumnNames")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(Set.class, String.class))
                .addStatement("return $T.of($L)",
                        ImmutableSet.class, String.join(", ", fieldNames));

        return method.build();
    }

    private MethodSpec generateGetRequiredColumnNamesMethod() {
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.ensureCapacity(fileDescriptor.fields().size());
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            if (field.required()) {
                fieldNames.add(fieldNameField(field.name()));
            }
        }

        MethodSpec.Builder method = MethodSpec.methodBuilder("getRequiredColumnNames")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(Set.class, String.class))
                .addStatement("return $T.of($L)",
                        ImmutableSet.class, String.join(", ", fieldNames));

        return method.build();
    }

    private MethodSpec generateLoadMethod() {
        TypeName gtfsEntityType = classNames.entityImplementationTypeName();
        TypeName tableContainerTypeName = classNames.tableContainerTypeName();
        MethodSpec.Builder method = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Reader.class, "reader")
                .addParameter(GtfsFeedName.class, "feedName")
                .addParameter(ValidatorLoader.class, "validatorLoader")
                .addParameter(NoticeContainer.class, "noticeContainer")
                .returns(ParameterizedTypeName.get(ClassName.get(GtfsTableContainer.class), gtfsEntityType))
                .addStatement("$T csvFile = new $T(reader, FILENAME)", CsvFile.class, CsvFile.class)
                .beginControlFlow("if (csvFile.isEmpty())")
                .addStatement(
                        "noticeContainer.addNotice(new $T(FILENAME))", EmptyFileNotice.class)
                .addStatement(
                        "$T table = $T.forEmptyFile()", tableContainerTypeName, tableContainerTypeName)
                .addStatement(
                        "validatorLoader.invokeSingleFileValidators(table, noticeContainer)")
                .addStatement("return table")
                .endControlFlow()
                .beginControlFlow("if (!new $T().validate(FILENAME, csvFile.getColumnNames(), " +
                                "getColumnNames(), getRequiredColumnNames(), noticeContainer))",
                        TableHeaderValidator.class)
                .addStatement("return $T.forInvalidHeaders()", tableContainerTypeName)
                .endControlFlow();

        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            method.addStatement(
                    "final int $L = csvFile.getColumnIndex($L)",
                    fieldColumnIndex(field.name()), fieldNameField(field.name()));
        }

        // Several fields may reuse the same cache.
        Set<String> cacheVars = new HashSet<>();
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            if (cachingEnabled(field)) {
                String cacheVarName = fieldColumnCache(field);
                if (cacheVars.add(cacheVarName)) {
                    method.addStatement(
                            "final $T<$T> $L = new $T<>()",
                            FieldCache.class, TypeName.get(field.javaType()), cacheVarName, FieldCache.class);
                }
            }
        }

        method.addStatement("$T.Builder builder = new $T.Builder()", gtfsEntityType, gtfsEntityType)
                .addStatement(
                        "$T rowParser = new $T(feedName, noticeContainer)", RowParser.class, RowParser.class)
                .addStatement("$T entities = new $T<>()", ParameterizedTypeName.get(ClassName.get(List.class),
                        gtfsEntityType), ArrayList.class);
        method.beginControlFlow(
                "for ($T row : csvFile)", CsvRow.class);

        method.beginControlFlow("if (row.getRowNumber() % $L == 0)", LOG_EVERY_N_ROWS)
                .addStatement("logger.atInfo().log($S, FILENAME, row.getRowNumber())", "Reading %s, row %d")
                .endControlFlow();

        method
                .addStatement("rowParser.setRow(row)")
                .addStatement("rowParser.checkRowColumnCount(csvFile)")
                .addStatement("builder.clear()")
                .addStatement("builder.$L(row.getRowNumber())", FieldNameConverter.setterMethodName("csvRowNumber"));

        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            CodeBlock fieldValue = CodeBlock.of("rowParser.$L($L, $T.$L$L)",
                    gtfsTypeToParserMethod(field.type()),
                    fieldColumnIndex(field.name()),
                    RowParser.class,
                    field.required() ? "REQUIRED" : "OPTIONAL",
                    field.numberBounds().isPresent() ? ", RowParser.NumberBounds." + field.numberBounds().get()
                            :
                            field.type() == FieldTypeEnum.ENUM
                                    ? ", " + field.javaType().toString() + "::forNumber" : "");
            if (cachingEnabled(field)) {
                fieldValue = CodeBlock.of("$L.addIfAbsent($L)", fieldColumnCache(field), fieldValue);
            }
            method.addStatement("builder.$L($L)", FieldNameConverter.setterMethodName(field.name()), fieldValue);
        }

        method.beginControlFlow("if (!rowParser.hasParseErrorsInRow())")
                .addStatement("$T entity = builder.build()", gtfsEntityType)
                .addStatement("validatorLoader.invokeSingleEntityValidators(entity, noticeContainer)")
                .addStatement("entities.add(entity)")
                .endControlFlow();

        method.endControlFlow();  // end for (row)

        // Print statistics for cache efficiency.
        for (GtfsFieldDescriptor field : fileDescriptor.fields()) {
            if (cachingEnabled(field)) {
                final String cacheName = fieldColumnCache(field);
                method.addStatement(
                        "logger.atInfo().log(" +
                                "$S, gtfsFilename(), $L, $L.getCacheSize(), $L.getLookupCount(), " +
                                "$L.getHitRatio() * 100.0, $L.getMissRatio() * 100.0)",
                        "Cache for %s %s: size = %d, lookup count = %d, hits = %.2f%%, misses = %.2f%%",
                        fieldNameField(field.name()),
                        cacheName, cacheName, cacheName, cacheName);
            }
        }

        method.addStatement("$T table = $T.forEntities(entities, noticeContainer)", tableContainerTypeName, tableContainerTypeName)
                .addStatement(
                        "validatorLoader.invokeSingleFileValidators(table, noticeContainer)")
                .addStatement("return table");

        return method.build();
    }

    private MethodSpec generateGtfsFilenameMethod() {
        return MethodSpec.methodBuilder("gtfsFilename")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return FILENAME")
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
        MethodSpec.Builder method = MethodSpec.methodBuilder("loadMissingFile")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ValidatorLoader.class, "validatorLoader")
                .addParameter(NoticeContainer.class, "noticeContainer")
                .returns(ParameterizedTypeName.get(ClassName.get(GtfsTableContainer.class), gtfsEntityType))
                .addAnnotation(Override.class)
                .addStatement("$T table = $T.forMissingFile()",
                        classNames.tableContainerTypeName(), classNames.tableContainerTypeName())
                .beginControlFlow("if (isRequired())")
                .addStatement("noticeContainer.addNotice(new $T(gtfsFilename()))", MissingRequiredFileError.class)
                .endControlFlow()
                .addStatement("validatorLoader.invokeSingleFileValidators(table, noticeContainer)")
                .addStatement("return table");

        return method.build();
    }

}
