package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/*
Discovers GtfsTableDescriptor subclasses in the default table package.
 */
public class ClassGraphTableDiscoverer {

  public static final String DEFAULT_TABLE_PACKAGE = "org.mobilitydata.gtfsvalidator.table";

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private ClassGraphTableDiscoverer() {
  }

  public static ImmutableList<GtfsTableDescriptor<?>> discover() {
    ImmutableList.Builder<GtfsTableDescriptor<?>> tableDescriptors = ImmutableList.builder();
    try (ScanResult scanResult =
        new ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(DEFAULT_TABLE_PACKAGE)
            .scan()) {
      for (ClassInfo classInfo : scanResult.getSubclasses(GtfsTableDescriptor.class)) {
        Class<?> clazz = classInfo.loadClass();
        GtfsTableDescriptor descriptor;
        try {
          descriptor = clazz.asSubclass(GtfsTableDescriptor.class).getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
          logger.atSevere().withCause(e).log(
              "Possible bug in GTFS annotation processor: expected a constructor without parameters"
                  + " for %s",
              clazz.getName());
          continue;
        }
        tableDescriptors.add(descriptor);
      }
    }
    return tableDescriptors.build();
  }
}
