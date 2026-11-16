package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;

@RunWith(JUnit4.class)
public class GtfsShapeColumnsTest {

  private static GtfsShape shape(int row, String shapeId, int sequence) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(row)
        .setShapeId(shapeId)
        .setShapePtLat(45.0 + row * 1e-6)
        .setShapePtLon(-73.0 - row * 1e-6)
        .setShapePtSequence(sequence)
        .build();
  }

  private static GtfsShape fullShape(int row) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(row)
        .setShapeId("sh" + row % 3)
        .setShapePtLat(45.5017 + row * 1e-7)
        .setShapePtLon(-73.5673 - row * 1e-7)
        .setShapePtSequence(row)
        .setShapeDistTraveled(row * 1.5)
        .build();
  }

  private static List<Object> fields(GtfsShape e) {
    return ImmutableList.of(
        e.csvRowNumber(),
        e.hasShapeId() + e.shapeId(),
        e.hasShapePtLat() + ":" + e.shapePtLat(),
        e.hasShapePtLon() + ":" + e.shapePtLon(),
        e.hasShapePtSequence() + ":" + e.shapePtSequence(),
        e.hasShapeDistTraveled() + ":" + e.shapeDistTraveled());
  }

  private static List<List<Object>> fields(List<GtfsShape> shapes) {
    return shapes.stream().map(GtfsShapeColumnsTest::fields).collect(Collectors.toList());
  }

  private static GtfsShapeTableContainer table(
      int threshold, List<GtfsShape> rows, NoticeContainer n) {
    ColumnarEntityList<GtfsShape> entities = GtfsShapeTableContainer.createEntityList(threshold);
    entities.addAll(rows);
    return GtfsShapeTableContainer.forHeaderAndEntities(
        new GtfsShapeTableDescriptor(), CsvHeader.EMPTY, entities, n);
  }

  private static ColumnarEntityList<GtfsShape> entities(GtfsShapeTableContainer table) {
    return (ColumnarEntityList<GtfsShape>) table.getEntities();
  }

  @Test
  public void columns_keepEveryFieldAndItsPresence() {
    List<GtfsShape> rows = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      rows.add(
          i % 2 == 0 ? fullShape(i + 2) : new GtfsShape.Builder().setCsvRowNumber(i + 2).build());
    }

    GtfsShapeTableContainer table = table(0, rows, new NoticeContainer());

    assertThat(entities(table).isColumnar()).isTrue();
    assertThat(fields(table.getEntities())).isEqualTo(fields(rows));
  }

  @Test
  public void columns_spanManyChunks() {
    // More than 16 chunks, so that the arrays of chunks grow too.
    int n = 17 * ColumnarEntityList.CHUNK_SIZE + 5;
    List<GtfsShape> rows = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      GtfsShape.Builder builder =
          new GtfsShape.Builder()
              .setCsvRowNumber(i + 2)
              .setShapeId("sh" + i / 10)
              .setShapePtLat(45.0 + i * 1e-7)
              .setShapePtLon(-73.0 - i * 1e-7)
              .setShapePtSequence(i % 10);
      // Only one chunk has distances, so the other chunks of that column are not allocated.
      if (i / ColumnarEntityList.CHUNK_SIZE == 3) {
        builder.setShapeDistTraveled(i * 2.0);
      }
      rows.add(builder.build());
    }

    GtfsShapeTableContainer table = table(0, rows, new NoticeContainer());

    assertThat(entities(table).isColumnar()).isTrue();
    for (int i :
        new int[] {
          0,
          ColumnarEntityList.CHUNK_SIZE - 1,
          ColumnarEntityList.CHUNK_SIZE,
          3 * ColumnarEntityList.CHUNK_SIZE + 1,
          16 * ColumnarEntityList.CHUNK_SIZE,
          n - 1
        }) {
      assertThat(fields(table.getEntities().get(i))).isEqualTo(fields(rows.get(i)));
    }
    assertThat(fields(table.byShapeId("sh" + (n - 1) / 10)))
        .isEqualTo(fields(rows.subList((n - 1) / 10 * 10, n)));
  }

  @Test
  public void add_switchesToColumnsAtThreshold() {
    ColumnarEntityList<GtfsShape> store = GtfsShapeTableContainer.createEntityList(3);
    List<GtfsShape> rows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      rows.add(fullShape(i + 2));
      store.add(rows.get(i));
    }
    assertThat(store.isColumnar()).isFalse();

    rows.add(fullShape(5));
    store.add(rows.get(3));
    store.finish(GtfsShape.FILENAME, new NoticeContainer());

    assertThat(store.isColumnar()).isTrue();
    assertThat(fields(store)).isEqualTo(fields(rows));
  }

  @Test
  public void index_sameInBothModes() {
    List<GtfsShape> rows = new ArrayList<>();
    ListMultimap<String, GtfsShape> byShape = ArrayListMultimap.create();
    for (int i = 0; i < 500; i++) {
      // Sequences go down within a shape and some repeat, so sorting and ties are exercised.
      GtfsShape shape = shape(i + 2, "shape" + (i * 7919 % 97), -i / 3);
      rows.add(shape);
      byShape.put(shape.shapeId(), shape);
    }
    for (String shapeId : byShape.keySet()) {
      byShape
          .get(shapeId)
          .sort((a, b) -> Integer.compare(a.shapePtSequence(), b.shapePtSequence()));
    }

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      GtfsShapeTableContainer store = table(threshold, rows, new NoticeContainer());

      assertThat(store.byShapeIdAsMap().keySet())
          .containsExactlyElementsIn(byShape.keySet())
          .inOrder();
      for (Map.Entry<String, List<GtfsShape>> shape : store.byShapeIdAsMap().entrySet()) {
        assertThat(fields(shape.getValue())).isEqualTo(fields(byShape.get(shape.getKey())));
        assertThat(fields(store.byShapeId(shape.getKey()))).isEqualTo(fields(shape.getValue()));
      }
      assertThat(store.byShapeIdMap().keySet())
          .containsExactlyElementsIn(byShape.keySet())
          .inOrder();
      assertThat(store.byShapeId("unknown")).isEmpty();
    }
  }

  @Test
  public void byShapeId_columns_returnsTheSameObjectOnEachGet() {
    GtfsShapeTableContainer store =
        table(0, List.of(shape(2, "sh", 1), shape(3, "sh", 2)), new NoticeContainer());

    List<GtfsShape> points = store.byShapeId("sh");

    assertThat(points.get(1)).isSameInstanceAs(points.get(1));
  }

  @Test
  public void duplicateKeys_reportFirstOccurrenceInFileOrder() {
    List<GtfsShape> rows =
        List.of(
            shape(2, "sh1", 1),
            shape(3, "sh2", 1),
            shape(4, "sh1", 1),
            shape(5, "sh1", 2),
            shape(6, "sh1", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      NoticeContainer notices = new NoticeContainer();
      table(threshold, rows, notices);

      assertThat(notices.getValidationNotices())
          .containsExactly(
              new DuplicateKeyNotice("shapes.txt", 2, 4, "shape_id,shape_pt_sequence", "sh1,1"),
              new DuplicateKeyNotice("shapes.txt", 2, 6, "shape_id,shape_pt_sequence", "sh1,1"))
          .inOrder();
    }
  }

  @Test
  public void byTranslationKey_returnsFirstRowInFileOrder() {
    List<GtfsShape> rows = List.of(shape(2, "sh", 2), shape(3, "sh", 1), shape(4, "sh", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      GtfsShapeTableContainer store = table(threshold, rows, new NoticeContainer());

      assertThat(store.byTranslationKey("sh", "1").get().csvRowNumber()).isEqualTo(3);
      assertThat(store.byTranslationKey("sh", "3")).isEqualTo(Optional.empty());
      assertThat(store.byTranslationKey("x", "1")).isEqualTo(Optional.empty());
      assertThat(store.byTranslationKey("sh", "one")).isEqualTo(Optional.empty());
    }
  }
}
