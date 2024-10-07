package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a field should not contain the replacement character (\uFFFD). A validator will be
 * automatically generated.
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("stops.txt")
 *   public interface GtfsStopSchema extends GtfsEntity {
 *       {@literal @}NoReplacementChar
 *       String stopName();
 *   }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface NoInvalidCharacters {}
