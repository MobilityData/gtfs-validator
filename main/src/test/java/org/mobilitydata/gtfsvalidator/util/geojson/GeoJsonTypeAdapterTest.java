package org.mobilitydata.gtfsvalidator.util.geojson;

import static org.junit.Assert.assertThrows;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class GeoJsonTypeAdapterTest {

  Gson gson;

  @Before
  public void before() {
    gson =
        (new GsonBuilder())
            .registerTypeAdapter(
                new TypeToken<Map<String, Object>>() {}.getType(), new MapJsonTypeAdapter())
            .create();
  }

  /**
   * Test that the custom JSON type adapter can handle a throws DuplicateJsonKeyException when: - A
   * JSON object has two keys with the same name at the same level.
   */
  @Test
  public void testDuplicateKeyExceptionSameLevel() {
    final var json = "{ \"type\": 1, \"type\": 2 }";
    JsonReader reader = new JsonReader(new StringReader(json));
    MapJsonTypeAdapter adapter = new MapJsonTypeAdapter();
    assertThrows(
        DuplicateJsonKeyException.class,
        () -> {
          adapter.read(reader);
        });
  }

  /**
   * Test that the custom JSON type adapter can handle a simple JSON object that don't contain
   * duplicate keys.
   */
  @Test
  public void testDuplicateKeyExceptionNestedLevel() {
    String json =
        "{\"type\": \"Alice\", \"features\": { \"properties\": \"Bob\", \"properties\": \"abc\" }}";
    JsonReader reader = new JsonReader(new StringReader(json));
    MapJsonTypeAdapter adapter = new MapJsonTypeAdapter();

    assertThrows(
        DuplicateJsonKeyException.class,
        () -> {
          adapter.read(reader);
        });
  }
}
