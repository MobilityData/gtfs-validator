package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a validation that the field or a file is required.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsTable("agency.txt")
 *   public interface GtfsAgencySchema extends GtfsEntity {
 *       @FieldType(FieldTypeEnum.ID)
 *       @PrimaryKey
 *       String agencyId();
 *
 *       @Required
 *       String agencyName();
 *   }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Required {
}
