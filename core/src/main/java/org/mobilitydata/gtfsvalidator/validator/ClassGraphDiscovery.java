package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ImmutableList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.table.GtfsDescriptor;

/** Discovers GTFS table descriptor and validator classes in the given Java packages. */
public class ClassGraphDiscovery {

  public static final String DEFAULT_VALIDATOR_PACKAGE = "org.mobilitydata.gtfsvalidator.validator";
  public static final String DEFAULT_TABLE_PACKAGE = "org.mobilitydata.gtfsvalidator.table";
  /** Default packages to find notices in open-source validator. */
  public static final ImmutableList<String> DEFAULT_NOTICE_PACKAGES =
      ImmutableList.of(
          "org.mobilitydata.gtfsvalidator.notice", "org.mobilitydata.gtfsvalidator.validator");

  private ClassGraphDiscovery() {}

  /** Discovers GtfsTableDescriptor subclasses in the default table package. */
  @SuppressWarnings("unchecked")
  public static ImmutableList<Class<? extends GtfsDescriptor<?>>> discoverTables() {
    ImmutableList.Builder<Class<? extends GtfsDescriptor<?>>> tableDescriptors =
        ImmutableList.builder();
    try (ScanResult scanResult =
        new ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(DEFAULT_TABLE_PACKAGE)
            .scan()) {
      for (ClassInfo classInfo : scanResult.getSubclasses(GtfsDescriptor.class)) {
        tableDescriptors.add((Class<? extends GtfsDescriptor<?>>) classInfo.loadClass());
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

  /**
   * Finds all subclasses of {@link Notice} that belong to the given packages.
   *
   * <p>This function also dives into validator classes that may contain inner notice classes.
   */
  public static ImmutableList<Class<Notice>> discoverNoticeSubclasses(List<String> packages) {
    String[] packagesAsArray = packages.toArray(new String[] {});
    ImmutableList.Builder<Class<Notice>> notices = ImmutableList.builder();
    ClassGraph classGraph =
        new ClassGraph()
            .enableClassInfo()
            .acceptPackages(packagesAsArray)
            .ignoreClassVisibility()
            .verbose();
    try (ScanResult scanResult = classGraph.scan()) {
      for (ClassInfo classInfo : scanResult.getSubclasses(Notice.class)) {
        if (classInfo.isAbstract()) {
          continue;
        }
        Class<?> clazz = classInfo.loadClass();
        notices.add((Class<Notice>) clazz);
      }
    }
    return notices.build();
  }
}
