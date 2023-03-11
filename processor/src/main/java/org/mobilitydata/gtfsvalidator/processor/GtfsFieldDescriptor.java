/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;
import java.util.Optional;
import javax.lang.model.type.TypeMirror;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.parsing.RowParser;

/** Describes a field in a GTFS table, e.g., stop_id in "stops.txt". */
@AutoValue
public abstract class GtfsFieldDescriptor {

  public static GtfsFieldDescriptor.Builder builder() {
    return new AutoValue_GtfsFieldDescriptor.Builder();
  }

  // Static properties.
  public abstract String name();

  public abstract FieldTypeEnum type();

  public abstract TypeMirror javaType();

  public abstract Optional<PrimaryKey> primaryKey();

  public abstract boolean index();

  public abstract boolean cached();

  public abstract Optional<ForeignKeyDescriptor> foreignKey();

  public abstract Optional<String> defaultValue();

  public abstract Optional<EndRangeDescriptor> endRange();

  public abstract Optional<String> currencyFieldReference();

  // Dynamic properties.
  public abstract boolean recommended();

  public abstract boolean valueRequired();

  public abstract boolean columnRequired();

  public boolean isHeaderRequired() {
    return columnRequired() || valueRequired();
  }

  public abstract boolean mixedCase();

  public abstract Optional<RowParser.NumberBounds> numberBounds();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setName(String value);

    public abstract Builder setType(FieldTypeEnum value);

    public abstract Builder setJavaType(TypeMirror value);

    public abstract Builder setRecommended(boolean value);

    public abstract Builder setValueRequired(boolean value);

    public abstract Builder setColumnRequired(boolean value);

    public abstract Builder setMixedCase(boolean value);

    public abstract Builder setPrimaryKey(PrimaryKey annotation);

    public abstract Builder setIndex(boolean value);

    public abstract Builder setCached(boolean value);

    public abstract Builder setForeignKey(Optional<ForeignKeyDescriptor> value);

    public abstract Builder setForeignKey(ForeignKeyDescriptor value);

    public abstract Builder setNumberBounds(Optional<RowParser.NumberBounds> value);

    public abstract Builder setNumberBounds(RowParser.NumberBounds value);

    public abstract Builder setDefaultValue(Optional<String> value);

    public abstract Builder setDefaultValue(String value);

    public abstract Builder setEndRange(Optional<EndRangeDescriptor> value);

    public abstract Builder setEndRange(EndRangeDescriptor value);

    public abstract Builder setCurrencyFieldReference(String currencyField);

    public abstract GtfsFieldDescriptor build();
  }
}
