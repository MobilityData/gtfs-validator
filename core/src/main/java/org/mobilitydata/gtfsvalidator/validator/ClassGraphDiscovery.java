package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.table.GtfsTableDescriptor;

/** Discovers GTFS table descriptor and validator classes in the given Java packages. */
public class ClassGraphDiscovery {

  public static final String DEFAULT_VALIDATOR_PACKAGE = "org.mobilitydata.gtfsvalidator.validator";
  public static final String DEFAULT_TABLE_PACKAGE = "org.mobilitydata.gtfsvalidator.table";

  private ClassGraphDiscovery() {}

  /** Discovers GtfsTableDescriptor subclasses in the default table package. */
  @SuppressWarnings("unchecked")
  public static ImmutableList<Class<? extends GtfsTableDescriptor<?>>> discoverTables() {
    ImmutableList.Builder<Class<? extends GtfsTableDescriptor<?>>> tableDescriptors =
        ImmutableList.builder();
    try (ScanResult scanResult =
        new ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(DEFAULT_TABLE_PACKAGE)
            .scan()) {
      for (ClassInfo classInfo : scanResult.getSubclasses(GtfsTableDescriptor.class)) {
        tableDescriptors.add((Class<? extends GtfsTableDescriptor<?>>) classInfo.loadClass());
      }
    }
    return tableDescriptors.build();
  }

  /** Discovers validator classes in the default package path. */
  public static ImmutableList<Class<?>> discoverValidatorsInDefaultPackage() {
    return discoverValidators(ImmutableList.of(DEFAULT_VALIDATOR_PACKAGE));
  }

  /**
   * Discovers validator classes in the given list of packages.
   *
   * @param validatorPackages list of package names for discovering validator classes
   */
  public static ImmutableList<Class<?>> discoverValidators(
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
