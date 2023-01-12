package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;

/*
Discovers GTFS validator classes in the given Java packages.
 */
public class ClassGraphValidatorDiscoverer {

  public static final String DEFAULT_VALIDATOR_PACKAGE = "org.mobilitydata.gtfsvalidator.validator";

  private ClassGraphValidatorDiscoverer() {
  }

  /**
   * Discovers validator classes in the default package path.
   */
  public static ImmutableList<Class<?>> discoverInDefaultPackage() {
    return discoverInPackages(ImmutableList.of(DEFAULT_VALIDATOR_PACKAGE));
  }

  /**
   * Discovers validator classes in the given list of packages.
   *
   * @param validatorPackages list of package names for discovering validator classes
   */
  public static ImmutableList<Class<?>> discoverInPackages(
      ImmutableList<String> validatorPackages) {
    ImmutableList.Builder<Class<?>> validatorClasses = ImmutableList.builder();
    for (String packageName : validatorPackages) {
      try (ScanResult scanResult =
          new ClassGraph()
              .enableClassInfo()
              .enableAnnotationInfo()
              .acceptPackages(packageName)
              .scan()) {
        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(GtfsValidator.class)) {
          Class<?> clazz = classInfo.loadClass();
          if (SingleEntityValidator.class.isAssignableFrom(clazz)
              || FileValidator.class.isAssignableFrom(clazz)) {
            validatorClasses.add(clazz);
          }
        }
      }
    }
    return validatorClasses.build();
  }

}

