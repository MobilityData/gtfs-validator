package org.mobilitydata.gtfsvalidator.processor;

import static org.mobilitydata.gtfsvalidator.processor.GtfsEntityClasses.VALIDATOR_PACKAGE_NAME;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.mobilitydata.gtfsvalidator.annotation.Generated;
import org.mobilitydata.gtfsvalidator.validator.GtfsValidatorRegistry;

public class ValidatorRegistryGenerator {
  private final List<ClassName> validatorClasses;

  public ValidatorRegistryGenerator(List<ClassName> validatorClasses) {
    this.validatorClasses = validatorClasses;
  }

  public JavaFile generateRegistry() {
    return JavaFile.builder(VALIDATOR_PACKAGE_NAME, generateTableRegistryClass()).build();
  }

  private TypeSpec generateTableRegistryClass() {
    return TypeSpec.classBuilder("DefaultValidatorRegistry")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Generated.class)
        .addSuperinterface(GtfsValidatorRegistry.class)
        .addMethod(generateGetValidatorClassesMethod())
        .build();
  }

  private MethodSpec generateGetValidatorClassesMethod() {
    MethodSpec.Builder method =
        MethodSpec.methodBuilder("getValidatorClasses")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(ImmutableList.class),
                    ParameterizedTypeName.get(
                        ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class))));

    method.addStatement(
        "$T.Builder builder = $T.builder()", ImmutableList.class, ImmutableList.class);
    for (ClassName validatorClass : validatorClasses) {
      method.addStatement("builder.add($T.class)", validatorClass);
    }
    method.addStatement("return builder.build()");
    return method.build();
  }
}
