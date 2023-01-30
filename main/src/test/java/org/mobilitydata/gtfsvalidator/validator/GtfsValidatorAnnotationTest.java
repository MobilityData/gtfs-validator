package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.collect.ImmutableList;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;

public class GtfsValidatorAnnotationTest {

  /**
   * This test ensures that any class that extends {@link SingleEntityValidator} or {@link
   * FileValidator} is also annotated with {@link GtfsValidator}. Not including the annotation
   * typically indicates an error, as the validator won't be picked up by scanning code in {@link
   * ValidatorLoader}.
   */
  @Test
  public void testAllValidatorSubclassesAreAnnotatedWithGtfsValidator() {
    ImmutableList<String> validatorPackages =
        ImmutableList.of(ClassGraphDiscovery.DEFAULT_VALIDATOR_PACKAGE);
    ImmutableList<Class> validatorBaseClasses =
        ImmutableList.of(SingleEntityValidator.class, FileValidator.class);
    for (String packageName : validatorPackages) {
      try (ScanResult scanResult =
          new ClassGraph()
              .enableClassInfo()
              .enableAnnotationInfo()
              .acceptPackages(packageName)
              .scan()) {
        for (Class<?> validatorBaseClass : validatorBaseClasses) {
          for (ClassInfo classInfo : scanResult.getSubclasses(validatorBaseClass)) {
            AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(GtfsValidator.class);
            assertWithMessage(
                    classInfo.getName()
                        + ": "
                        + validatorBaseClass.getSimpleName()
                        + " validator subclass should be annotated with @GtfsValidator")
                .that(annotationInfo != null)
                .isTrue();
          }
        }
      }
    }
  }
}
