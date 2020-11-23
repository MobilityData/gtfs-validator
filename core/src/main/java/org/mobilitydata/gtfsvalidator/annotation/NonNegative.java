package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Generates a validation that an integer or a double (float) field is not negative.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("stop_times.txt")
 *   public interface GtfsStopTimeSchema extends GtfsEntity {
 *       @NonNegative
 *       double shapeDistTraveled();
 *   }
 * </pre>
 */
public @interface NonNegative {
}
