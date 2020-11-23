package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Asks annotation processor to create an index for quick search on a given field. The field does not need to have
 * unique values.
 * <p>
 * Note that {@code PrimaryKey} already implies an index.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       @FieldType(FieldTypeEnum.ID)
 *       @Index
 *       String zoneId();
 *   }
 * </pre>
 * <p>
 * This generated the following method.
 *
 * <pre>
 *   public class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {
 *       public List<GtfsStop> byZoneId(String key) {
 *           // ...
 *       }
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Index {
}
