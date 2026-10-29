package org.mobilitydata.gtfsvalidator.table;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

/** Container for stop_times.txt, written by hand to store large tables as columns. */
public final class GtfsStopTimeTableContainer
    extends GtfsTableContainer<GtfsStopTime, GtfsTableDescriptor<GtfsStopTime>> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsStopTime.TRIP_ID_FIELD_NAME, GtfsStopTime.STOP_SEQUENCE_FIELD_NAME);

  private final StopTimeStore store;

  private GtfsStopTimeTableContainer(
      GtfsTableDescriptor<GtfsStopTime> descriptor, CsvHeader header, StopTimeStore store) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS, header);
    this.store = store;
  }

  public GtfsStopTimeTableContainer(
      GtfsTableDescriptor<GtfsStopTime> descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus, CsvHeader.EMPTY);
    this.store = new StopTimeStore(StopTimeStore.COLUMN_THRESHOLD);
    store.finish(GtfsStopTime.FILENAME, new NoticeContainer());
  }

  static List<GtfsStopTime> createEntityList() {
    return new StopTimeStore(StopTimeStore.COLUMN_THRESHOLD);
  }

  @Override
  public Class<GtfsStopTime> getEntityClass() {
    return GtfsStopTime.class;
  }

  @Override
  public String gtfsFilename() {
    return GtfsStopTime.FILENAME;
  }

  @Override
  public List<GtfsStopTime> getEntities() {
    return store;
  }

  /** Creates a table with given header and entities. */
  public static GtfsStopTimeTableContainer forHeaderAndEntities(
      GtfsTableDescriptor<GtfsStopTime> descriptor,
      CsvHeader header,
      List<GtfsStopTime> entities,
      NoticeContainer noticeContainer) {
    StopTimeStore store;
    if (entities instanceof StopTimeStore) {
      store = (StopTimeStore) entities;
    } else {
      store = new StopTimeStore(StopTimeStore.COLUMN_THRESHOLD);
      store.addAll(entities);
    }
    store.finish(GtfsStopTime.FILENAME, noticeContainer);
    return new GtfsStopTimeTableContainer(descriptor, header, store);
  }

  /** Creates a table with given entities and empty header. This method is intended for tests. */
  public static GtfsStopTimeTableContainer forEntities(
      List<GtfsStopTime> entities, NoticeContainer noticeContainer) {
    return forHeaderAndEntities(
        new GtfsStopTimeTableDescriptor(), CsvHeader.EMPTY, entities, noticeContainer);
  }

  /** Creates a table with the given TableStatus. This method is intended for tests. */
  public static GtfsStopTimeTableContainer forStatus(TableStatus tableStatus) {
    return new GtfsStopTimeTableContainer(new GtfsStopTimeTableDescriptor(), tableStatus);
  }

  /** Returns the stop times of a trip, sorted by stop_sequence. */
  public List<GtfsStopTime> byTripId(String key) {
    return store.byTripId(key);
  }

  /** Returns the stop times of each trip, sorted by stop_sequence, computed on access. */
  public Map<String, List<GtfsStopTime>> byTripIdAsMap() {
    return store.byTripIdAsMap();
  }

  /**
   * @deprecated creates every stop time at once, use {@link #byTripIdAsMap()}.
   */
  @Deprecated
  public ListMultimap<String, GtfsStopTime> byTripIdMap() {
    return store.byTripIdMap();
  }

  public List<GtfsStopTime> byStopId(String key) {
    return store.byStopId(key);
  }

  /**
   * @deprecated creates every stop time at once, use {@link #byStopId(String)}.
   */
  @Deprecated
  public ListMultimap<String, GtfsStopTime> byStopIdMap() {
    return store.byStopIdMap();
  }

  @Override
  public Optional<GtfsStopTime> byTranslationKey(String recordId, String recordSubId) {
    try {
      return store.byTripIdAndStopSequence(
          Strings.isNullOrEmpty(recordId) ? "" : recordId,
          Strings.isNullOrEmpty(recordSubId) ? 0 : Integer.parseInt(recordSubId));
    } catch (NumberFormatException ex) {
      return Optional.empty();
    }
  }

  @Override
  public ImmutableList<String> getKeyColumnNames() {
    return KEY_COLUMN_NAMES;
  }
}
