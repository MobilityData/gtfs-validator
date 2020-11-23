package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface that defines schema for a single GTFS table, such as "stops.txt".
 * <p>
 * Set {@code singleRow = true} if the table may have a single row, such as "feed_info.txt".
 * <p>
 * Example.
 * <pre>
 *   @GtfsTable("routes.txt")
 *   public interface GtfsRouteSchema extends GtfsEntity {
 *       @DefaultValue("FFFFFF")
 *       GtfsColor routeColor();
 *   }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GtfsTable {
    String value();

    boolean singleRow() default false;
}
