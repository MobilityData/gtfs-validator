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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * Describes a GTFS file (CSV table), e.g., "stops.txt".
 */
@AutoValue
public abstract class GtfsFileDescriptor {
    public static Builder builder() {
        return new AutoValue_GtfsFileDescriptor.Builder();
    }

    // GTFS file name, e.g., "routes.txt".
    public abstract String filename();

    public abstract String className();

    public abstract boolean required();

    public abstract boolean singleRow();

    public abstract ImmutableList<GtfsFieldDescriptor> fields();

    public abstract ImmutableMap<String, GtfsFieldDescriptor> fieldByName();

    public GtfsFieldDescriptor getFieldByName(String name) {
        return fieldByName().get(name);
    }

    public abstract ImmutableList<TypeMirror> interfaces();

    public abstract Optional<GtfsFieldDescriptor> primaryKey();

    public abstract Optional<GtfsFieldDescriptor> firstKey();

    public abstract Optional<GtfsFieldDescriptor> sequenceKey();

    public abstract ImmutableList<GtfsFieldDescriptor> indices();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setFilename(String value);

        public abstract Builder setClassName(String value);

        public abstract Builder setRequired(boolean value);

        public abstract Builder setSingleRow(boolean value);

        public abstract ImmutableList.Builder<GtfsFieldDescriptor> fieldsBuilder();

        abstract ImmutableList<GtfsFieldDescriptor> fields();

        abstract Builder setFieldByName(ImmutableMap<String, GtfsFieldDescriptor> value);

        public abstract ImmutableList.Builder<TypeMirror> interfacesBuilder();

        abstract Builder setPrimaryKey(GtfsFieldDescriptor value);

        abstract Builder setFirstKey(GtfsFieldDescriptor value);

        abstract Builder setSequenceKey(GtfsFieldDescriptor value);

        abstract Builder setIndices(ImmutableList<GtfsFieldDescriptor> value);

        abstract GtfsFileDescriptor autoBuild();

        public GtfsFileDescriptor build() {
            ImmutableMap.Builder<String, GtfsFieldDescriptor> fieldsMapBuilder =
                    ImmutableMap.builder();
            ImmutableList.Builder<GtfsFieldDescriptor> indicesBuilder = ImmutableList.builder();
            for (GtfsFieldDescriptor field : fields()) {
                fieldsMapBuilder.put(field.name(), field);
                if (field.primaryKey()) {
                    setPrimaryKey(field);
                } else if (field.firstKey()) {
                    setFirstKey(field);
                } else if (field.sequenceKey()) {
                    setSequenceKey(field);
                }
                if (field.index()) {
                    indicesBuilder.add(field);
                }
            }
            setFieldByName(fieldsMapBuilder.build());
            setIndices(indicesBuilder.build());
            return autoBuild();
        }
    }
}
