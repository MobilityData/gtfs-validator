/*
 * Copyright 2024 MobilityData
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
package org.mobilitydata.gtfsvalidator.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.DuplicateGeoJsonKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * Container for GeoJSON features. Contrarily to the csv containers, this class is not auto
 * generated since we have only one such class.
 */
public class GtfsGeoJsonFeaturesContainer
    extends GtfsEntityContainer<GtfsGeoJsonFeature, GtfsGeoJsonFileDescriptor> {

  private final Map<String, GtfsGeoJsonFeature> byLocationIdMap = new HashMap<>();

  private final List<GtfsGeoJsonFeature> entities;

  public GtfsGeoJsonFeaturesContainer(
      GtfsGeoJsonFileDescriptor descriptor,
      List<GtfsGeoJsonFeature> entities,
      NoticeContainer noticeContainer) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS);
    this.entities = entities;
    setupIndices(noticeContainer);
  }

  public GtfsGeoJsonFeaturesContainer(
      GtfsGeoJsonFileDescriptor descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<GtfsGeoJsonFeature> getEntityClass() {
    return GtfsGeoJsonFeature.class;
  }

  @Override
  public List<GtfsGeoJsonFeature> getEntities() {
    return entities;
  }

  @Override
  public String gtfsFilename() {
    return GtfsGeoJsonFeature.FILENAME;
  }

  @Override
  public Optional<GtfsGeoJsonFeature> byTranslationKey(String recordId, String recordSubId) {
    return Optional.empty();
  }

  private void setupIndices(NoticeContainer noticeContainer) {
    for (GtfsGeoJsonFeature newEntity : entities) {
      if (!newEntity.hasFeatureId()) {
        continue;
      }
      GtfsGeoJsonFeature oldEntity = byLocationIdMap.getOrDefault(newEntity.featureId(), null);
      if (oldEntity == null) {
        byLocationIdMap.put(newEntity.featureId(), newEntity);
      } else {
        noticeContainer.addValidationNotice(
            new DuplicateGeoJsonKeyNotice(
                oldEntity.featureId(), oldEntity.featureIndex(), newEntity.featureIndex()));
      }
    }
  }

  public Map<String, GtfsGeoJsonFeature> byLocationIdMap() {
    return byLocationIdMap;
  }

  public GtfsGeoJsonFeature byLocationId(String locationId) {
    return byLocationIdMap.get(locationId);
  }
}
