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
import org.mobilitydata.gtfsvalidator.annotation.NoInvalidCharacters;
import org.mobilitydata.gtfsvalidator.notice.InvalidCharactersNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.SingleEntityValidator;

/**
 * Generates validator classes to check that a string field does not contain invalid characters such
 * as the replacement character (\uFFFD).
 *
 * @see NoInvalidCharacters
 * @see InvalidCharactersNotice
 */
public class NoInvalidCharactersValidatorGenerator {
  static final String REPLACEMENT_CHAR = "\uFFFD";

  public ImmutableList<TypeSpec> generateValidator(List<GtfsFileDescriptor> fileDescriptors) {
    ImmutableList.Builder<TypeSpec> validators = ImmutableList.builder();
    for (GtfsFileDescriptor fileDescriptor : fileDescriptors) {
      if (fileDescriptor.fields().stream().anyMatch(f -> f.noInvalidCharacters())) {
        validators.add(generateValidator(fileDescriptor));
      }
    }
    return validators.build();
  }

  private static TypeSpec generateValidator(GtfsFileDescriptor fileDescriptor) {
    GtfsEntityClasses entityClasses = new GtfsEntityClasses(fileDescriptor);
    TypeSpec.Builder typeSpec =
        TypeSpec.classBuilder(fileDescriptor.className() + "NoInvalidCharactersValidator")
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

    for (GtfsFieldDescriptor noInvalidCharactersField : fileDescriptor.fields()) {
      if (!noInvalidCharactersField.noInvalidCharacters()) {
        continue;
      }
      validateMethod
          .beginControlFlow(
              "if (entity.$L())", FieldNameConverter.hasMethodName(noInvalidCharactersField.name()))
          .addStatement("$T value = entity.$L()", String.class, noInvalidCharactersField.name())
          .beginControlFlow("if (value.contains(\"$L\"))", REPLACEMENT_CHAR)
          .addStatement(
              "noticeContainer.addValidationNotice(new $T(\"$L\", \"$L\", value, entity.csvRowNumber()))",
              InvalidCharactersNotice.class,
              fileDescriptor.filename(),
              FieldNameConverter.gtfsColumnName(noInvalidCharactersField.name()))
          .endControlFlow()
          .endControlFlow();
    }

    typeSpec.addMethod(validateMethod.build());

    return typeSpec.build();
  }
}
