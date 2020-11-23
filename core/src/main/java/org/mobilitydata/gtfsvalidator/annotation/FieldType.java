package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies type of a GTFS field, e.g., {@code COLOR} or {@code LATITUDE}.
 * <p>
 * Many types, such as {@code INTEGER}, {@code FLOAT} or {@code TEXT}, are deducted from the return type in the schema
 * definition:
 *
 * <pre>
 *   @GtfsTable("routes.txt")
 *   public interface GtfsRouteSchema extends GtfsEntity {
 *       // Field type TEXT is deducted from String.
 *       String routeShortName();
 *   }
 * </pre>
 * <p>
 * However, if you want to have, e.g., {@code LATITUDE} instead of {@code FLOAT} or {@code PHONE_NUMBER} instead of
 * {@code TEXT}, you have to use this annotation.
 *
 * <pre>
 *   @GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *     @FieldType(FieldTypeEnum.LATITUDE)
 *     double stopLat();
 *
 *     @FieldType(FieldTypeEnum.LONGITUDE)
 *     double stopLon();
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FieldType {
    FieldTypeEnum value();
}
