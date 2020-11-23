package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a reference to a foreign key. A validator for data integrity will be generated automatically.
 * <p>
 * Note that {@code @ForeignKey} does not imply that the field is required and you need to put an extra
 * {@code @Required} annotation in this case.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("trips.txt")
 *   public interface GtfsTripSchema extends GtfsEntity {
 *     @FieldType(FieldTypeEnum.ID)
 *     @Required
 *     @ForeignKey(table = "routes.txt", field = "route_id")
 *     String routeId();
 *   }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ForeignKey {
    String table();

    String field();
}
