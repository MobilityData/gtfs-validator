package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * Describes a field in a GTFS table, e.g., stop_id in "stops.txt".
 */
@AutoValue
public abstract class GtfsFieldDescriptor {
    public static GtfsFieldDescriptor.Builder builder() {
        return new AutoValue_GtfsFieldDescriptor.Builder();
    }

    public abstract String name();

    public abstract FieldTypeEnum type();

    public abstract TypeMirror javaType();

    public abstract boolean required();

    public abstract boolean primaryKey();

    public abstract boolean firstKey();

    public abstract boolean sequenceKey();

    public abstract boolean index();

    public abstract Optional<ForeignKeyDescriptor> foreignKey();

    public abstract Optional<RowParser.NumberBounds> numberBounds();

    public abstract Optional<String> defaultValue();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String value);

        public abstract Builder setType(FieldTypeEnum value);

        public abstract Builder setJavaType(TypeMirror value);

        public abstract Builder setRequired(boolean value);

        public abstract Builder setPrimaryKey(boolean value);

        public abstract Builder setFirstKey(boolean value);

        public abstract Builder setSequenceKey(boolean value);

        public abstract Builder setIndex(boolean value);

        public abstract Builder setForeignKey(Optional<ForeignKeyDescriptor> value);

        public abstract Builder setForeignKey(ForeignKeyDescriptor value);

        public abstract Builder setNumberBounds(Optional<RowParser.NumberBounds> value);

        public abstract Builder setNumberBounds(RowParser.NumberBounds value);

        public abstract Builder setDefaultValue(Optional<String> value);

        public abstract Builder setDefaultValue(String value);

        public abstract GtfsFieldDescriptor build();
    }
}
