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

import com.google.common.geometry.S2LatLng;
import com.squareup.javapoet.*;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Generates validator classes to check that points described by lat-lon fields are not too close to
 * the origin or to the poles.
 *
 * <p>Pairs of fields with suffixes {@code _lat, _lon} and types {@code LATITUDE, LONGITUDE} are
 * detected automatically.
 */
public class LatLonValidatorGenerator {

  private static final String VALIDATOR_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.validator";

  private final List<GtfsFileDescriptor> fileDescriptors;

  public LatLonValidatorGenerator(List<GtfsFileDescriptor> fileDescriptors) {
    this.fileDescriptors = fileDescriptors;
  }

  public List<JavaFile> generateValidatorFiles() {
    List<JavaFile> validators = new ArrayList<>();
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      if (!fileDescriptor.latLonFields().isEmpty()) {
        validators.add(generateValidator(fileDescriptor));
      }
    }
    return validators;
  }

  private static JavaFile generateValidator(GtfsFileDescriptor fileDescriptor) {
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

    addPoles(typeSpec);

    MethodSpec.Builder validateMethod =
        MethodSpec.methodBuilder("validate")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(void.class)
            .addParameter(entityClasses.entityImplementationTypeName(), "entity")
            .addParameter(NoticeContainer.class, "noticeContainer");

    for (LatLonDescriptor latLonDescriptor : fileDescriptor.latLonFields()) {
      validateLatLon(fileDescriptor, latLonDescriptor, validateMethod);
    }

    typeSpec.addMethod(validateMethod.build());

    return JavaFile.builder(VALIDATOR_PACKAGE_NAME, typeSpec.build()).build();
  }

  private static void addPoles(TypeSpec.Builder typeSpec) {
    typeSpec.addField(
        FieldSpec.builder(
                S2LatLng.class, "NORTH_POLE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$T.fromDegrees(90, 0)", S2LatLng.class)
            .build());
    typeSpec.addField(
        FieldSpec.builder(
                S2LatLng.class, "SOUTH_POLE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$T.fromDegrees(-90, 0)", S2LatLng.class)
            .build());
  }

  private static void validateLatLon(
      GtfsFileDescriptor fileDescriptor,
      LatLonDescriptor latLonDescriptor,
      MethodSpec.Builder validateMethod) {
    validateMethod.beginControlFlow(
        "if (entity.$L())", FieldNameConverter.hasMethodName(latLonDescriptor.latLonField()));

    validateMethod
        .beginControlFlow(
            "if (Math.abs(entity.$L()) <= 1.0 && Math.abs(entity.$L()) <= 1.0)",
            latLonDescriptor.latField(),
            latLonDescriptor.lonField())
        .addStatement(
            "noticeContainer.addValidationNotice(new $T($L))",
            PointNearOriginNotice.class,
            generateNoticeContext(fileDescriptor, latLonDescriptor))
        .endControlFlow();

    validateMethod
        .addStatement("$T point = entity.$L()", S2LatLng.class, latLonDescriptor.latLonField())
        .beginControlFlow(
            "if (point.getEarthDistance(NORTH_POLE) <= 1.0 || point.getEarthDistance(SOUTH_POLE)"
                + " <= 1.0)")
        .addStatement(
            "noticeContainer.addValidationNotice(new $T($L))",
            PointNearPoleNotice.class,
            generateNoticeContext(fileDescriptor, latLonDescriptor))
        .endControlFlow();

    validateMethod.endControlFlow();
  }

  private static CodeBlock generateNoticeContext(
      GtfsFileDescriptor fileDescriptor, LatLonDescriptor latLonDescriptor) {
    TypeName tableLoaderTypeName = new GtfsEntityClasses(fileDescriptor).tableLoaderTypeName();
    CodeBlock.Builder block =
        CodeBlock.builder().add("$T.FILENAME, entity.csvRowNumber(), ", tableLoaderTypeName);
    if (fileDescriptor.primaryKey().isPresent()) {
      block.add("entity.$L(), ", fileDescriptor.primaryKey().get().name());
    }
    block.add(
        "$T.$L, entity.$L(), $T.$L, entity.$L()",
        tableLoaderTypeName,
        FieldNameConverter.fieldNameField(latLonDescriptor.latField()),
        latLonDescriptor.latField(),
        tableLoaderTypeName,
        FieldNameConverter.fieldNameField(latLonDescriptor.lonField()),
        latLonDescriptor.lonField());
    return block.build();
  }

  private static String validatorName(GtfsFileDescriptor fileDescriptor) {
    return fileDescriptor.className() + "LatLonValidator";
  }
}
