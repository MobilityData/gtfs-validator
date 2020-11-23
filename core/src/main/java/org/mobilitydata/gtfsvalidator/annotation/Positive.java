package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Generates a validation that an integer or a double (float) field is positive.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("pathways.txt")
 *   public interface GtfsPathwaySchema extends GtfsEntity {
 *       @Positive
 *       int traversalTime();
 *   }
 * </pre>
 */
public @interface Positive {
}
