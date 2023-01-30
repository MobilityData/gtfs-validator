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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.type.TypeMirror;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType;

/** Describes a GTFS file (CSV table), e.g., "stops.txt". */
@AutoValue
public abstract class GtfsFileDescriptor {
  public static Builder builder() {
    return new AutoValue_GtfsFileDescriptor.Builder();
  }

  // GTFS file name, e.g., "routes.txt".
  public abstract String filename();

  public abstract String className();

  public abstract boolean recommended();

  public abstract boolean required();

  public abstract boolean singleRow();

  public abstract Optional<Integer> maxCharsPerColumn();

  public abstract ImmutableList<GtfsFieldDescriptor> fields();

  public abstract ImmutableMap<String, GtfsFieldDescriptor> fieldByName();

  public GtfsFieldDescriptor getFieldByName(String name) {
    return fieldByName().get(name);
  }

  public abstract ImmutableList<TypeMirror> interfaces();

  public abstract ImmutableList<GtfsFieldDescriptor> primaryKeys();

  public boolean hasSingleColumnPrimaryKey() {
    return primaryKeys().size() == 1;
  }

  public GtfsFieldDescriptor getSingleColumnPrimaryKey() {
    Preconditions.checkState(hasSingleColumnPrimaryKey());
    return primaryKeys().get(0);
  }

  public boolean hasMultiColumnPrimaryKey() {
    return primaryKeys().size() > 1;
  }

  public abstract ImmutableList<GtfsFieldDescriptor> indices();

  public abstract ImmutableList<LatLonDescriptor> latLonFields();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setFilename(String value);

    public abstract String filename();

    public abstract Builder setClassName(String value);

    public abstract Builder setRecommended(boolean value);

    public abstract Builder setRequired(boolean value);

    public abstract Builder setSingleRow(boolean value);

    public abstract Builder setMaxCharsPerColumn(int maxCharsPerColumn);

    public abstract ImmutableList.Builder<GtfsFieldDescriptor> fieldsBuilder();

    abstract ImmutableList<GtfsFieldDescriptor> fields();

    abstract Builder setFieldByName(ImmutableMap<String, GtfsFieldDescriptor> value);

    public abstract ImmutableList.Builder<TypeMirror> interfacesBuilder();

    abstract Builder setPrimaryKeys(ImmutableList<GtfsFieldDescriptor> values);

    abstract Builder setIndices(ImmutableList<GtfsFieldDescriptor> value);

    abstract Builder setLatLonFields(ImmutableList<LatLonDescriptor> value);

    abstract GtfsFileDescriptor autoBuild();

    public GtfsFileDescriptor build() {
      ImmutableMap.Builder<String, GtfsFieldDescriptor> fieldsMapBuilder = ImmutableMap.builder();
      ImmutableList.Builder<GtfsFieldDescriptor> indicesBuilder = ImmutableList.builder();
      ImmutableList.Builder<GtfsFieldDescriptor> primaryKeysBuilder = ImmutableList.builder();
      for (GtfsFieldDescriptor field : fields()) {
        fieldsMapBuilder.put(field.name(), field);
        if (field.primaryKey().isPresent()) {
          primaryKeysBuilder.add(field);
        }
        if (field.index()) {
          indicesBuilder.add(field);
        }
      }
      ImmutableMap<String, GtfsFieldDescriptor> fieldsMap = fieldsMapBuilder.buildOrThrow();
      ImmutableList.Builder<LatLonDescriptor> latLonBuilder = ImmutableList.builder();
      for (GtfsFieldDescriptor field : fields()) {
        if (!field.type().equals(FieldTypeEnum.LATITUDE)) {
          continue;
        }
        GtfsFieldDescriptor lonField =
            fieldsMap.get(field.name().substring(0, field.name().length() - 3) + "Lon");
        if (!lonField.type().equals(FieldTypeEnum.LONGITUDE)) {
          continue;
        }
        latLonBuilder.add(
            LatLonDescriptor.create(field.name(), lonField.name(), field.name() + "Lon"));
      }

      setFieldByName(fieldsMap);
      setIndices(indicesBuilder.build());
      setPrimaryKeys(primaryKeysBuilder.build());
      setLatLonFields(latLonBuilder.build());

      validateIsSequenceUsedForSortingAnnotation();
      validateTranslationRecordTypeAnnotations();

      return autoBuild();
    }

    private void validateIsSequenceUsedForSortingAnnotation() {
      long count =
          fields().stream()
              .map(GtfsFieldDescriptor::primaryKey)
              .flatMap(Optional::stream)
              .filter(PrimaryKey::isSequenceUsedForSorting)
              .count();
      if (count > 1) {
        throw new IllegalArgumentException(
            filename()
                + ": At most one field can be annotated with @PrimarKey.isSequenceUsedForSorting = true");
      }
    }

    private void validateTranslationRecordTypeAnnotations() {
      Map<TranslationRecordIdType, Long> translationRecordTypeCounts =
          fields().stream()
              .map(GtfsFieldDescriptor::primaryKey)
              .flatMap(Optional::stream)
              .collect(
                  Collectors.groupingBy(
                      PrimaryKey::translationRecordIdType, Collectors.counting()));
      long recordIdCount =
          translationRecordTypeCounts.getOrDefault(TranslationRecordIdType.RECORD_ID, 0L);
      long recordSubIdCount =
          translationRecordTypeCounts.getOrDefault(TranslationRecordIdType.RECORD_SUB_ID, 0L);

      if (recordIdCount > 1) {
        throw new IllegalArgumentException(
            filename()
                + ": At most one field can be annotated with TranslationRecordIdType.RECORD_ID");
      }
      if (recordSubIdCount > 1) {
        throw new IllegalArgumentException(
            filename()
                + ": At most one field can be annotated with TranslationRecordIdType.RECORD_SUB_ID");
      }
      if (recordIdCount == 0 && recordSubIdCount == 1) {
        throw new IllegalArgumentException(
            filename()
                + ": Field annotated with TranslationRecordIdType.RECORD_SUB_ID without an existing TranslationRecordIdType.RECORD_ID field");
      }
    }
  }
}
