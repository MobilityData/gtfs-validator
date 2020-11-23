package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Generates a validation that an integer or a double (float) field is not zero.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("pathways.txt")
 *   public interface GtfsPathwaySchema extends GtfsEntity {
 *       @NonZero
 *       int stairCount();
 *   }
 * </pre>
 */
public @interface NonZero {
}
