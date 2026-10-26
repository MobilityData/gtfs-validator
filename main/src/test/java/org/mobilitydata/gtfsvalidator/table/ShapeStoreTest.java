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

@RunWith(JUnit4.class)
public class ShapeStoreTest {

  private static GtfsShape point(int row, String shapeId, int sequence) {
    return new GtfsShape.Builder()
        .setCsvRowNumber(row)
        .setShapeId(shapeId)
        .setShapePtSequence(sequence)
        .setShapePtLat(45.0 + row * 1e-7)
        .setShapePtLon(-73.0 - row * 1e-7)
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

  private static List<List<Object>> fields(List<GtfsShape> points) {
    return points.stream().map(ShapeStoreTest::fields).collect(Collectors.toList());
  }

  private static ShapeStore store(int threshold, List<GtfsShape> rows, NoticeContainer n) {
    ShapeStore store = new ShapeStore(threshold);
    store.addAll(rows);
    store.finish(GtfsShape.FILENAME, n);
    return store;
  }

  @Test
  public void columns_keepEveryFieldAndItsPresence() {
    List<GtfsShape> rows = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      rows.add(
          i % 2 == 0
              ? new GtfsShape.Builder()
                  .setCsvRowNumber(i + 2)
                  .setShapeId("s" + i % 3)
                  .setShapePtSequence(i)
                  .setShapePtLat(45.5)
                  .setShapePtLon(-73.5)
                  .setShapeDistTraveled(i * 2.5)
                  .build()
              : new GtfsShape.Builder().setCsvRowNumber(i + 2).build());
    }

    ShapeStore columns = store(0, rows, new NoticeContainer());

    assertThat(columns.isColumnar()).isTrue();
    assertThat(fields(columns)).isEqualTo(fields(rows));
  }

  @Test
  public void add_switchesToColumnsAtThreshold() {
    ShapeStore store = new ShapeStore(3);
    List<GtfsShape> rows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      rows.add(point(i + 2, "s", i));
      store.add(rows.get(i));
    }
    assertThat(store.isColumnar()).isFalse();

    rows.add(point(5, "s", 3));
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
      GtfsShape point = point(i + 2, "shape" + (i * 7919 % 97), -i / 3);
      rows.add(point);
      byShape.put(point.shapeId(), point);
    }
    for (String shapeId : byShape.keySet()) {
      byShape
          .get(shapeId)
          .sort((a, b) -> Integer.compare(a.shapePtSequence(), b.shapePtSequence()));
    }

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      ShapeStore store = store(threshold, rows, new NoticeContainer());

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
    ShapeStore store = store(0, List.of(point(2, "s", 1), point(3, "s", 2)), new NoticeContainer());

    List<GtfsShape> shape = store.byShapeId("s");

    assertThat(shape.get(1)).isSameInstanceAs(shape.get(1));
  }

  @Test
  public void duplicateKeys_reportFirstOccurrenceInFileOrder() {
    List<GtfsShape> rows =
        List.of(
            point(2, "a", 1),
            point(3, "b", 1),
            point(4, "a", 1),
            point(5, "a", 2),
            point(6, "a", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      NoticeContainer notices = new NoticeContainer();
      store(threshold, rows, notices);

      assertThat(notices.getValidationNotices())
          .containsExactly(
              new DuplicateKeyNotice("shapes.txt", 2, 4, "shape_id,shape_pt_sequence", "a,1"),
              new DuplicateKeyNotice("shapes.txt", 2, 6, "shape_id,shape_pt_sequence", "a,1"))
          .inOrder();
    }
  }

  @Test
  public void byShapeIdAndSequence_returnsFirstRowInFileOrder() {
    List<GtfsShape> rows = List.of(point(2, "s", 2), point(3, "s", 1), point(4, "s", 1));

    for (int threshold : new int[] {0, Integer.MAX_VALUE}) {
      ShapeStore store = store(threshold, rows, new NoticeContainer());

      assertThat(store.byShapeIdAndSequence("s", 1).get().csvRowNumber()).isEqualTo(3);
      assertThat(store.byShapeIdAndSequence("s", 3)).isEqualTo(Optional.empty());
      assertThat(store.byShapeIdAndSequence("x", 1)).isEqualTo(Optional.empty());
    }
  }
}
