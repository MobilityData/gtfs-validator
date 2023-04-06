package org.mobilitydata.gtfsvalidator.testgtfs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.*;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

/** Test class to avoid dependency on the real GtfsStopTableContainer and annotation processor. */
public final class GtfsStopTableContainer extends GtfsTableContainer<GtfsStop> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsStop.STOP_ID_FIELD_NAME);

  private List<GtfsStop> entities;

  private Map<String, GtfsStop> byStopIdMap = new HashMap<>();

  private ListMultimap<String, GtfsStop> byZoneIdMap = ArrayListMultimap.create();

  private ListMultimap<String, GtfsStop> byParentStationMap = ArrayListMultimap.create();

  private GtfsStopTableContainer(CsvHeader header, List<GtfsStop> entities) {
    super(TableStatus.PARSABLE_HEADERS_AND_ROWS, header);
    this.entities = entities;
  }

  public GtfsStopTableContainer(TableStatus tableStatus) {
    super(tableStatus, CsvHeader.EMPTY);
    this.entities = new ArrayList<>();
  }

  @Override
  public Class<GtfsStop> getEntityClass() {
    return GtfsStop.class;
  }

  @Override
  public String gtfsFilename() {
    return GtfsStop.FILENAME;
  }

  @Override
  public boolean isRecommended() {
    return false;
  }

  @Override
  public boolean isRequired() {
    return true;
  }

  @Override
  public List<GtfsStop> getEntities() {
    return entities;
  }

  /** Creates a table with given header and entities */
  public static GtfsStopTableContainer forHeaderAndEntities(
      CsvHeader header, List<GtfsStop> entities, NoticeContainer noticeContainer) {
    GtfsStopTableContainer table = new GtfsStopTableContainer(header, entities);
    table.setupIndices(noticeContainer);
    return table;
  }

  /**
   * Creates a table with given entities and empty header. This method is intended to be used in
   * tests.
   */
  public static GtfsStopTableContainer forEntities(
      List<GtfsStop> entities, NoticeContainer noticeContainer) {
    return forHeaderAndEntities(CsvHeader.EMPTY, entities, noticeContainer);
  }

  public Optional<GtfsStop> byStopId(String key) {
    return Optional.ofNullable(byStopIdMap.getOrDefault(key, null));
  }

  /** @return List of org.mobilitydata.gtfsvalidator.table.GtfsStop */
  public List<GtfsStop> byZoneId(String key) {
    return byZoneIdMap.get(key);
  }

  /**
   * @return ListMultimap keyed on zone_id with values that are Lists of
   *     org.mobilitydata.gtfsvalidator.table.GtfsStop
   */
  public ListMultimap<String, GtfsStop> byZoneIdMap() {
    return byZoneIdMap;
  }

  /** @return List of org.mobilitydata.gtfsvalidator.table.GtfsStop */
  public List<GtfsStop> byParentStation(String key) {
    return byParentStationMap.get(key);
  }

  /**
   * @return ListMultimap keyed on parent_station with values that are Lists of
   *     org.mobilitydata.gtfsvalidator.table.GtfsStop
   */
  public ListMultimap<String, GtfsStop> byParentStationMap() {
    return byParentStationMap;
  }

  @Override
  public Optional<GtfsStop> byTranslationKey(String recordId, String recordSubId) {
    return Optional.ofNullable(byStopIdMap.getOrDefault(recordId, null));
  }

  @Override
  public ImmutableList<String> getKeyColumnNames() {
    return KEY_COLUMN_NAMES;
  }

  private void setupIndices(NoticeContainer noticeContainer) {
    for (GtfsStop newEntity : entities) {
      if (!newEntity.hasStopId()) {
        continue;
      }
      GtfsStop oldEntity = byStopIdMap.getOrDefault(newEntity.stopId(), null);
      if (oldEntity != null) {
        noticeContainer.addValidationNotice(
            new DuplicateKeyNotice(
                gtfsFilename(),
                newEntity.csvRowNumber(),
                oldEntity.csvRowNumber(),
                GtfsStop.STOP_ID_FIELD_NAME,
                newEntity.stopId()));
      } else {
        byStopIdMap.put(newEntity.stopId(), newEntity);
      }
    }
  }
}
