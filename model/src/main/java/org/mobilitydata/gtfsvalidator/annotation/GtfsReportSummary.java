package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to designate the primary summary class for the GTFS validation report.
 *
 * <p>This annotation is applied to the class that encapsulates the overall summary metadata This
 * allows the annotation processor to locate and process the summary class regardless of its name or
 * location, making the build configuration more flexible.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GtfsReportSummary {}
