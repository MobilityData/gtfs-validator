/*
 * Copyright 2022 Google LLC
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
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyAmountNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Generates validator classes to check the validity of a currency amount value.
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount
 * @see InvalidCurrencyAmountNotice
 */
public class CurrencyAmountValidatorGenerator {

  private static final String VALIDATOR_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.validator";

  public List<JavaFile> generateValidatorFiles(List<GtfsFileDescriptor> fileDescriptors) {
    List<JavaFile> validators = new ArrayList<>();
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      if (fileDescriptor.fields().stream().anyMatch(f -> f.currencyFieldReference().isPresent())) {
        validators.add(generateValidator(fileDescriptor));
      }
    }
    return validators;
  }

  private static JavaFile generateValidator(GtfsFileDescriptor fileDescriptor) {
    GtfsEntityClasses entityClasses = new GtfsEntityClasses(fileDescriptor);
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(fileDescriptor.className() + "CurrencyAmountValidator")
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

    for (GtfsFieldDescriptor amountField : fileDescriptor.fields()) {
      if (amountField.currencyFieldReference().isEmpty()) {
        continue;
      }
      GtfsFieldDescriptor currencyField =
          fileDescriptor.getFieldByName(amountField.currencyFieldReference().get());
      if (currencyField == null) {
        throw new IllegalArgumentException(
            fileDescriptor.filename()
                + " "
                + amountField.name()
                + ": unknown @CurrencyAmount(currencyField=\""
                + amountField.currencyFieldReference().get()
                + "\") reference");
      }
      validateMethod
          .beginControlFlow(
              "if (entity.$L() && entity.$L())",
              FieldNameConverter.hasMethodName(amountField.name()),
              FieldNameConverter.hasMethodName(currencyField.name()))
          .addStatement("$T amount = entity.$L()", BigDecimal.class, amountField.name())
          .addStatement("$T currency = entity.$L()", Currency.class, currencyField.name())
          .beginControlFlow("if (amount.scale() != currency.getDefaultFractionDigits())")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\"$L\", \"$L\", entity.csvRowNumber(), amount))",
              InvalidCurrencyAmountNotice.class,
              fileDescriptor.filename(),
              amountField.name())
          .endControlFlow()
          .endControlFlow();
    }

    typeSpec.addMethod(validateMethod.build());

    return JavaFile.builder(VALIDATOR_PACKAGE_NAME, typeSpec.build()).build();
  }
}
