package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the primary key in a GTFS table.
 * <p>
 * This also adds a validation that all values are unique.
 * <p>
 * Note that {@code @PrimaryKey} does not imply that the field is required and you need to put an extra
 * {@code @Required} annotation in this case.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       @FieldType(FieldTypeEnum.ID)
 *       @Required
 *       @PrimaryKey
 *       String stopId();
 *   }
 * </pre>
 * <p>
 * This also adds a method to the container class to find an entity by its primary key.
 *
 * <pre>
 *   @Generated
 *   public class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {
 *       public GtfsStop byStopId(String key) {
 *           // ...
 *       }
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface PrimaryKey {
}
