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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.TABLE_PACKAGE_NAME;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.table.GtfsEnum;

/**
 * Generates an enum class for a GTFS enumeration. The class implements convenient methods {@code
 * forNumber()} and {@code getNumber()} for conversion to and from an integer.
 */
public class EnumGenerator {
  public static final String ENUM_SUFFIX = "Enum";
  private final GtfsEnumDescriptor enumDescriptor;

  public EnumGenerator(GtfsEnumDescriptor enumDescriptor) {
    this.enumDescriptor = enumDescriptor;
  }

  public static String createEnumName(String interfaceName) {
    return interfaceName.substring(0, interfaceName.length() - ENUM_SUFFIX.length());
  }

  private static MethodSpec getNumberMethod() {
    return MethodSpec.methodBuilder("getNumber")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(int.class)
        .addStatement("return value")
        .build();
  }

  public JavaFile generateEnumJavaFile() {
    return JavaFile.builder(TABLE_PACKAGE_NAME, generateEnumClass()).build();
  }

  /** Returns the integer value for the special {@code UNRECOGNIZED} constant */
  static int getMinUnrecognizedValue(GtfsEnumDescriptor enumDescriptor) {
    int minValue = 0;
    for (GtfsEnumValueDescriptor enumValue : enumDescriptor.values()) {
      minValue = min(minValue, enumValue.value());
    }
    return minValue - 1;
  }

  /** Returns the max valid integer value for a GTFS enum. */
  static int getMaxValue(GtfsEnumDescriptor enumDescriptor) {
    int maxValue = 0;
    for (GtfsEnumValueDescriptor enumValue : enumDescriptor.values()) {
      maxValue = max(maxValue, enumValue.value());
    }
    return maxValue;
  }

  private TypeSpec generateEnumClass() {
    TypeSpec.Builder enumType =
        TypeSpec.enumBuilder(enumDescriptor.name())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Generated.class)
            .addSuperinterface(GtfsEnum.class);

    for (GtfsEnumValueDescriptor enumValue : enumDescriptor.values()) {
      enumType.addEnumConstant(
          enumValue.name(),
          TypeSpec.anonymousClassBuilder(Integer.toString(enumValue.value())).build());
    }
    enumType.addEnumConstant(
        "UNRECOGNIZED",
        TypeSpec.anonymousClassBuilder(Integer.toString(getMinUnrecognizedValue(enumDescriptor)))
            .build());

    enumType.addField(int.class, "value", Modifier.PRIVATE, Modifier.FINAL);

    enumType.addMethod(
        MethodSpec.constructorBuilder()
            .addParameter(int.class, "value")
            .addStatement("this.value = value")
            .build());

    enumType.addMethod(forNumberMethod());
    enumType.addMethod(getNumberMethod());

    return enumType.build();
  }

  private MethodSpec forNumberMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("forNumber")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeVariableName.get(enumDescriptor.name()))
            .addParameter(int.class, "value");
    method.beginControlFlow("switch (value)");
    for (GtfsEnumValueDescriptor enumValue : enumDescriptor.values()) {
      method.addStatement("case $L: return $L", enumValue.value(), enumValue.name());
    }
    method.addStatement("default: return null");
    method.endControlFlow();
    return method.build();
  }
}
