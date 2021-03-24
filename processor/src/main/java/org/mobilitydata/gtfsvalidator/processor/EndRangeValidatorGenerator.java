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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndRangeEqualNotice;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndRangeOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Generates validator classes to check the order of date or time range fields, e.g., {@code
 * start_date &lt; end_date}.
 *
 * <p>A range constraint is added with {@code EndRange} annotation in GTFS schema.
 */
public class EndRangeValidatorGenerator {

  private static final String VALIDATOR_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.validator";

  private final List<GtfsFileDescriptor> fileDescriptors;

  public EndRangeValidatorGenerator(List<GtfsFileDescriptor> fileDescriptors) {
    this.fileDescriptors = fileDescriptors;
  }

  public List<JavaFile> generateValidatorFiles() {
    List<JavaFile> validators = new ArrayList<>();
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      generateValidator(fileDescriptor).ifPresent(validators::add);
    }
    return validators;
  }

  private static Optional<JavaFile> generateValidator(GtfsFileDescriptor fileDescriptor) {
    GtfsEntityClasses entityClasses = new GtfsEntityClasses(fileDescriptor);
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(validatorName(fileDescriptor))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Generated.class)
            .addAnnotation(GtfsValidator.class)
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(SingleEntityValidator.class),
                    entityClasses.entityImplementationTypeName()));

    MethodSpec.Builder validateMethod =
        MethodSpec.methodBuilder("validate")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(void.class)
            .addParameter(entityClasses.entityImplementationTypeName(), "entity")
            .addParameter(NoticeContainer.class, "noticeContainer");

    boolean hasEndRange = false;
    for (GtfsFieldDescriptor startField : fileDescriptor.fields()) {
      if (!startField.endRange().isPresent()) {
        continue;
      }
      EndRangeDescriptor endRange = startField.endRange().get();
      GtfsFieldDescriptor endField = fileDescriptor.getFieldByName(endRange.field());
      if (endField == null) {
        reportWarning(
            "Cannot find field " + endRange.field() + " in table " + fileDescriptor.filename());
        continue;
      }
      if (startField.name().equals(endField.name())) {
        reportWarning(
            "Start and end range fields are the same: "
                + FieldNameConverter.gtfsColumnName(endRange.field())
                + " in table "
                + fileDescriptor.filename());
        continue;
      }
      hasEndRange = true;
      validateMethod.beginControlFlow(
          "if (entity.$L() && entity.$L())",
          FieldNameConverter.hasMethodName(startField.name()),
          FieldNameConverter.hasMethodName(endField.name()));

      validateMethod
          .beginControlFlow(
              "if (entity.$L().isAfter(entity.$L()))", startField.name(), endField.name())
          .addStatement(
              "noticeContainer.addValidationNotice(new $T($L))",
              StartAndEndRangeOutOfOrderNotice.class,
              generateNoticeContext(
                  fileDescriptor, startField, endField, StartEndRangeNoticeType.OUT_OF_ORDER));

      if (!endRange.allowEqual()) {
        validateMethod
            .nextControlFlow(
                "else if (entity.$L().equals(entity.$L()))", startField.name(), endField.name())
            .addStatement(
                "noticeContainer.addValidationNotice(new $T($L))",
                StartAndEndRangeEqualNotice.class,
                generateNoticeContext(
                    fileDescriptor, startField, endField, StartEndRangeNoticeType.EQUAL));
      }

      validateMethod.endControlFlow().endControlFlow();
    }

    typeSpec.addMethod(validateMethod.build());

    return hasEndRange
        ? Optional.of(JavaFile.builder(VALIDATOR_PACKAGE_NAME, typeSpec.build()).build())
        : Optional.empty();
  }

  enum StartEndRangeNoticeType {
    OUT_OF_ORDER,
    EQUAL;
  }

  private static CodeBlock generateNoticeContext(
      GtfsFileDescriptor fileDescriptor,
      GtfsFieldDescriptor startField,
      GtfsFieldDescriptor endField,
      StartEndRangeNoticeType noticeType) {
    TypeName tableLoaderTypeName = new GtfsEntityClasses(fileDescriptor).tableLoaderTypeName();
    CodeBlock.Builder block =
        CodeBlock.builder().add("$T.FILENAME, entity.csvRowNumber(), ", tableLoaderTypeName);
    if (fileDescriptor.primaryKey().isPresent()) {
      block.add("entity.$L(), ", fileDescriptor.primaryKey().get().name());
    }
    block.add("$T.$L, ", tableLoaderTypeName, FieldNameConverter.fieldNameField(startField.name()));
    if (noticeType.equals(StartEndRangeNoticeType.OUT_OF_ORDER)) {
      block.add("entity.$L().toString(), ", startField.name());
    }
    block.add(
        "$T.$L, entity.$L().toString()",
        tableLoaderTypeName,
        FieldNameConverter.fieldNameField(endField.name()),
        endField.name());
    return block.build();
  }

  private static String validatorName(GtfsFileDescriptor fileDescriptor) {
    return fileDescriptor.className() + "EndRangeValidator";
  }

  private static void reportWarning(String warning) {
    System.err.println(warning);
  }
}
