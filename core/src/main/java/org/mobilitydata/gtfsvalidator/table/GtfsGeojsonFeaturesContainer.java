package org.mobilitydata.gtfsvalidator.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public class GtfsGeojsonFeaturesContainer
    extends GtfsEntityContainer<GtfsGeojsonFeature, GtfsGeojsonFileDescriptor> {

  private final Map<String, GtfsGeojsonFeature> byLocationIdMap = new HashMap<>();

  private final List<GtfsGeojsonFeature> entities;

  public GtfsGeojsonFeaturesContainer(
      GtfsGeojsonFileDescriptor descriptor,
      List<GtfsGeojsonFeature> entities,
      NoticeContainer noticeContainer) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS);
    this.entities = entities;
    setupIndices(noticeContainer);
  }

  public GtfsGeojsonFeaturesContainer(
      GtfsGeojsonFileDescriptor descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<GtfsGeojsonFeature> getEntityClass() {
    return GtfsGeojsonFeature.class;
  }

  @Override
  public List<GtfsGeojsonFeature> getEntities() {
    return entities;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }

  @Override
  public Optional<GtfsGeojsonFeature> byTranslationKey(String recordId, String recordSubId) {
    return Optional.empty();
  }

  private void setupIndices(NoticeContainer noticeContainer) {
    for (GtfsGeojsonFeature newEntity : entities) {
      if (!newEntity.hasLocationId()) {
        continue;
      }
      GtfsGeojsonFeature oldEntity = byLocationIdMap.getOrDefault(newEntity.locationId(), null);
      if (oldEntity != null) {
        noticeContainer.addValidationNotice(
            new DuplicateKeyNotice(
                gtfsFilename(),
                newEntity.csvRowNumber(),
                oldEntity.csvRowNumber(),
                GtfsGeojsonFeature.LOCATION_ID_FIELD_NAME,
                newEntity.locationId()));
      } else {
        byLocationIdMap.put(newEntity.locationId(), newEntity);
      }
    }
  }
}
