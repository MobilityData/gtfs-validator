package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;

/**
 * Describes a single value in a GTFS enumeration.
 */
@AutoValue
public abstract class GtfsEnumValueDescriptor {
    public static GtfsEnumValueDescriptor create(String name, int value) {
        return new AutoValue_GtfsEnumValueDescriptor(name, value);
    }

    public abstract String name();

    public abstract int value();

}
