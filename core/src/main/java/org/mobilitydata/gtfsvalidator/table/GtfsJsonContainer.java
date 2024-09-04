package org.mobilitydata.gtfsvalidator.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

public class GtfsJsonContainer<T extends GtfsJson, D extends GtfsDescriptor>
    extends GtfsContainer<T, D> {

  private final Map<String, GtfsJson> byLocationIdMap = new HashMap<>();

  private final List<T> entities;

  public GtfsJsonContainer(D descriptor, List<T> entities, NoticeContainer noticeContainer) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS);
    this.entities = entities;
    setupIndices(noticeContainer);
  }

  public GtfsJsonContainer(GtfsDescriptor<GtfsJson> descriptor, TableStatus tableStatus) {
    super((D) descriptor, tableStatus);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<T> getEntityClass() {
    return (Class<T>) GtfsJson.class;
  }

  @Override
  public List<T> getEntities() {
    return (List<T>) entities;
  }

  @Override
  public String gtfsFilename() {
    return "locations.geojson";
  }

  @Override
  public Optional<T> byTranslationKey(String recordId, String recordSubId) {
    return Optional.empty();
  }

  private void setupIndices(NoticeContainer noticeContainer) {
    for (GtfsJson newEntity : entities) {
      if (!newEntity.hasLocationId()) {
        continue;
      }
      GtfsJson oldEntity = byLocationIdMap.getOrDefault(newEntity.locationId(), null);
      if (oldEntity != null) {
        noticeContainer.addValidationNotice(
            new DuplicateKeyNotice(
                gtfsFilename(),
                newEntity.csvRowNumber(),
                oldEntity.csvRowNumber(),
                GtfsJson.LOCATION_ID_FIELD_NAME,
                newEntity.locationId()));
      } else {
        byLocationIdMap.put(newEntity.locationId(), newEntity);
      }
    }
  }
}
