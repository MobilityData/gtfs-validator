package org.mobilitydata.gtfsvalidator.columns;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsTestEntityColumnBased;

public class GtfsColumnBasedCollectionFactoryTest {

  private GtfsColumnStore store;

  private GtfsTestEntityColumnBased.Builder builder;

  private GtfsColumnAssignments assignments;

  private GtfsColumnBasedCollectionFactory<GtfsTestEntityColumnBased> factory;

  @Before
  public void before() {
    store = new GtfsColumnStore();
    builder = new GtfsTestEntityColumnBased.Builder(store);
    assignments = ((GtfsColumnBasedEntityBuilder) builder).getAssignments();
    factory =
        new GtfsColumnBasedCollectionFactory<>(
            store, assignments, GtfsTestEntityColumnBased::create);
  }

  @Test
  public void testAllEntitiesList() {
    List<GtfsTestEntityColumnBased> entities = factory.createAllEntitiesList();

    builder.clear();
    entities.add(builder.setCsvRowNumber(1).setId("a").setCode("xyz").build());
    builder.clear();
    entities.add(builder.setCsvRowNumber(3).setId("b").setCode("efg").build());

    assertThat(entities).hasSize(2);

    assertThat(entities.get(0)).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 0));
    assertThat(entities.get(1)).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 1));

    assertThrows(UnsupportedOperationException.class, () -> entities.clear());
  }

  @Test
  public void testSomeEntitiesList() {
    builder.clear();
    GtfsTestEntityColumnBased a = builder.setCsvRowNumber(1).setId("a").build();
    builder.clear();
    GtfsTestEntityColumnBased b = builder.setCsvRowNumber(3).setId("b").build();
    builder.clear();
    GtfsTestEntityColumnBased c = builder.setCsvRowNumber(4).setId("c").build();

    List<GtfsTestEntityColumnBased> entities = factory.createSomeEntitiesList();
    entities.add(c);
    entities.add(a);

    assertThat(entities).hasSize(2);

    assertThat(entities.get(0)).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 2));
    assertThat(entities.get(1)).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 0));

    GtfsTestEntityColumnBased existing = entities.set(0, b);
    assertThat(existing).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 2));
    assertThat(entities.get(0)).isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 1));

    assertThrows(UnsupportedOperationException.class, () -> entities.clear());
  }

  @Test
  public void testSomeEntitiesMap() {
    builder.clear();
    GtfsTestEntityColumnBased a = builder.setCsvRowNumber(1).setId("a").build();
    builder.clear();
    GtfsTestEntityColumnBased b = builder.setCsvRowNumber(3).setId("b").build();
    builder.clear();
    GtfsTestEntityColumnBased c = builder.setCsvRowNumber(4).setId("c").build();

    Map<String, GtfsTestEntityColumnBased> entities = factory.createSomeEntitiesMap();
    entities.put("a", a);
    entities.put("c", c);

    assertThat(entities).hasSize(2);

    assertThat(entities.get("a"))
        .isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 0));
    assertThat(entities.get("c"))
        .isEqualTo(GtfsTestEntityColumnBased.create(store, assignments, 2));

    assertThrows(UnsupportedOperationException.class, () -> entities.clear());
  }
}
