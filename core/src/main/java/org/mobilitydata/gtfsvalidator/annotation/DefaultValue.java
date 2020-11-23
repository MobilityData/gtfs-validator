package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Specifies a default value for a particular GTFS field.
 * <p>
 * The value needs to be given as a string in the same form as it would appear in a GTFS file.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("routes.txt")
 *   public interface GtfsRouteSchema extends GtfsEntity {
 *       @DefaultValue("FFFFFF")
 *       GtfsColor routeColor();
 *   }
 * </pre>
 */
public @interface DefaultValue {
    String value();
}
