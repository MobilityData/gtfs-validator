package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;

/**
 * Describes a foreign key relation in a GTFS table.
 */
@AutoValue
public abstract class ForeignKeyDescriptor {
    public static ForeignKeyDescriptor create(String table, String field) {
        return new AutoValue_ForeignKeyDescriptor(table, field);
    }

    public abstract String table();

    public abstract String field();
}
