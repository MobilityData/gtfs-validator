package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.*;

/**
 * Specifies a value for a GTFS enum. This information will be used to generate the actual Java class for the enum.
 * <p>
 * This annotation should be applied to an interface called ${TypeName}Enum and the annotation processor creates a
 * ${TypeName} Java enum for it.
 * <p>
 * Example. Annotation processor creates {@code GtfsLocationType} for the given schema.
 *
 * <pre>
 *   @GtfsEnumValue(name = "STOP", value = 0)
 *   @GtfsEnumValue(name = "STATION", value = 1)
 *   @GtfsEnumValue(name = "ENTRANCE", value = 2)
 *   @GtfsEnumValue(name = "GENERIC_NODE", value = 3)
 *   @GtfsEnumValue(name = "BOARDING_AREA", value = 4)
 *   public interface GtfsLocationTypeEnum {
 *   }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(GtfsEnumValues.class)
public @interface GtfsEnumValue {
    String name();

    int value();
}
