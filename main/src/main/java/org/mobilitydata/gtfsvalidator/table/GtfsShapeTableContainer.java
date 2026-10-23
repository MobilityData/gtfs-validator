package org.mobilitydata.gtfsvalidator.table;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

/** Container for shapes.txt, written by hand to store large tables as columns. */
public final class GtfsShapeTableContainer
    extends GtfsTableContainer<GtfsShape, GtfsTableDescriptor<GtfsShape>> {
  private static final ImmutableList<String> KEY_COLUMN_NAMES =
      ImmutableList.of(GtfsShape.SHAPE_ID_FIELD_NAME, GtfsShape.SHAPE_PT_SEQUENCE_FIELD_NAME);

  private final ShapeStore store;

  private GtfsShapeTableContainer(
      GtfsTableDescriptor<GtfsShape> descriptor, CsvHeader header, ShapeStore store) {
    super(descriptor, TableStatus.PARSABLE_HEADERS_AND_ROWS, header);
    this.store = store;
  }

  public GtfsShapeTableContainer(
      GtfsTableDescriptor<GtfsShape> descriptor, TableStatus tableStatus) {
    super(descriptor, tableStatus, CsvHeader.EMPTY);
    this.store = new ShapeStore(ShapeStore.COLUMN_THRESHOLD);
    store.finish(GtfsShape.FILENAME, new NoticeContainer());
  }

  static List<GtfsShape> createEntityList() {
    return new ShapeStore(ShapeStore.COLUMN_THRESHOLD);
  }

  @Override
  public Class<GtfsShape> getEntityClass() {
    return GtfsShape.class;
  }

  @Override
  public String gtfsFilename() {
    return GtfsShape.FILENAME;
  }

  @Override
  public List<GtfsShape> getEntities() {
    return store;
  }

  /** Creates a table with given header and entities. */
  public static GtfsShapeTableContainer forHeaderAndEntities(
      GtfsTableDescriptor<GtfsShape> descriptor,
      CsvHeader header,
      List<GtfsShape> entities,
      NoticeContainer noticeContainer) {
    ShapeStore store;
    if (entities instanceof ShapeStore) {
      store = (ShapeStore) entities;
    } else {
      store = new ShapeStore(ShapeStore.COLUMN_THRESHOLD);
      store.addAll(entities);
    }
    store.finish(GtfsShape.FILENAME, noticeContainer);
    return new GtfsShapeTableContainer(descriptor, header, store);
  }

  /** Creates a table with given entities and empty header. This method is intended for tests. */
  public static GtfsShapeTableContainer forEntities(
      List<GtfsShape> entities, NoticeContainer noticeContainer) {
    return forHeaderAndEntities(
        new GtfsShapeTableDescriptor(), CsvHeader.EMPTY, entities, noticeContainer);
  }

  /** Creates a table with the given TableStatus. This method is intended for tests. */
  public static GtfsShapeTableContainer forStatus(TableStatus tableStatus) {
    return new GtfsShapeTableContainer(new GtfsShapeTableDescriptor(), tableStatus);
  }

  /** Returns the points of a shape, sorted by shape_pt_sequence. */
  public List<GtfsShape> byShapeId(String key) {
    return store.byShapeId(key);
  }

  /** Returns the points of each shape, sorted by shape_pt_sequence, computed on access. */
  public Map<String, List<GtfsShape>> byShapeIdAsMap() {
    return store.byShapeIdAsMap();
  }

  /**
   * @deprecated creates every shape point at once, use {@link #byShapeIdAsMap()}.
   */
  @Deprecated
  public ListMultimap<String, GtfsShape> byShapeIdMap() {
    return store.byShapeIdMap();
  }

  @Override
  public Optional<GtfsShape> byTranslationKey(String recordId, String recordSubId) {
    try {
      return store.byShapeIdAndSequence(
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
