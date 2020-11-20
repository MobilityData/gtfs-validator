package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Describes a GTFS enumeration that consists of several integer constants.
 */
@AutoValue
public abstract class GtfsEnumDescriptor {
    public static Builder builder() {
        return new AutoValue_GtfsEnumDescriptor.Builder();
    }

    public abstract String name();

    public abstract ImmutableList<GtfsEnumValueDescriptor> values();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String value);

        public abstract ImmutableList.Builder<GtfsEnumValueDescriptor> valuesBuilder();

        public abstract GtfsEnumDescriptor build();
    }
}
