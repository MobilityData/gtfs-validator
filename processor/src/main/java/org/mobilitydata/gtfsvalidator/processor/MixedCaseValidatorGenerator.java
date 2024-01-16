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

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MixedCaseRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Generates validator classes to check that a string field contains mixed case characters.
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.MixedCase
 * @see MixedCaseRecommendedFieldNotice
 */
public class MixedCaseValidatorGenerator {
  public ImmutableList<TypeSpec> generateValidator(List<GtfsFileDescriptor> fileDescriptors) {
    ImmutableList.Builder<TypeSpec> validators = ImmutableList.builder();
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      if (fileDescriptor.fields().stream().anyMatch(f -> f.mixedCase())) {
        validators.add(generateValidator(fileDescriptor));
      }
    }
    return validators.build();
  }

  private static TypeSpec generateValidator(GtfsFileDescriptor fileDescriptor) {
    GtfsEntityClasses entityClasses = new GtfsEntityClasses(fileDescriptor);
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(fileDescriptor.className() + "MixedCaseValidator")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Generated.class)
            .addAnnotation(GtfsValidator.class)
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(SingleEntityValidator.class), entityClasses.entityTypeName()));

    MethodSpec.Builder validateMethod =
        MethodSpec.methodBuilder("validate")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(void.class)
            .addParameter(entityClasses.entityTypeName(), "entity")
            .addParameter(NoticeContainer.class, "noticeContainer");

    for (GtfsFieldDescriptor mixedCaseField : fileDescriptor.fields()) {
      if (!mixedCaseField.mixedCase()) {
        continue;
      }
      validateMethod
          .beginControlFlow(
              "if (entity.$L())", FieldNameConverter.hasMethodName(mixedCaseField.name()))
          .addStatement("$T value = entity.$L()", String.class, mixedCaseField.name())
          .addStatement("$T[] tokens = value.split(\"[^\\\\p{L}]+\")", String.class)
          .addComment(
              "If there is only one token, and no numbers, check that it is not all lowercase")
          .beginControlFlow("if (tokens.length == 1)")
          .beginControlFlow(
              "if (tokens[0].length() > 1 && !tokens[0].matches(\".*\\\\d+.*\") && tokens[0].matches(\"^\\\\p{Ll}+$$\"))")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\"$L\", \"$L\", value, entity.csvRowNumber()))",
              MixedCaseRecommendedFieldNotice.class,
              fileDescriptor.filename(),
              FieldNameConverter.gtfsColumnName(mixedCaseField.name()))
          .endControlFlow()
          .endControlFlow()
          .beginControlFlow("else")
          .addComment(
              "If there are multiple tokens, find all without numbers and check that at least one is mixed case")
          .addStatement("boolean hasMixedCaseToken = false")
          .addStatement("int noNumberTokensCount = 0")
          .beginControlFlow("for (String token : tokens)")
          .beginControlFlow("if (token.length() == 1 || token.matches(\".*\\\\d+.*\"))")
          .addStatement("continue")
          .endControlFlow()
          .beginControlFlow("else")
          .addStatement("noNumberTokensCount++")
          .beginControlFlow("if (token.matches(\"^(?=.*\\\\p{Lu})(?=.*\\\\p{Ll}).*$$\"))")
          .addStatement("hasMixedCaseToken = true")
          .endControlFlow()
          .endControlFlow()
          .endControlFlow()
          .beginControlFlow("if (noNumberTokensCount >= 2 && !hasMixedCaseToken)")
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\"$L\", \"$L\", value, entity.csvRowNumber()))",
              MixedCaseRecommendedFieldNotice.class,
              fileDescriptor.filename(),
              FieldNameConverter.gtfsColumnName(mixedCaseField.name()))
          .endControlFlow()
          .endControlFlow()
          .endControlFlow();
    }

    typeSpec.addMethod(validateMethod.build());

    return typeSpec.build();
  }
}
