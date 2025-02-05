package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.StringReader;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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

    Exception exception = assertThrows(DuplicateJsonKeyException.class, () -> {
      adapter.read(reader);
    });

    String expectedMessage = "Duplicated Key: type";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.trim().equals(expectedMessage.trim()));
  }

  /**
   * Test that the custom JSON type adapter can handle a simple JSON object that don't contain
   * duplicate keys.
   */
  @Test
  public void testDuplicateKeyExceptionNestedLevel() {
    String json = "{\"type\": \"Alice\", \"features\": { \"properties\": \"Bob\", \"properties\": \"abc\" }}";
    JsonReader reader = new JsonReader(new StringReader(json));
    MapJsonTypeAdapter adapter = new MapJsonTypeAdapter();

    Exception exception = assertThrows(DuplicateJsonKeyException.class, () -> {
      adapter.read(reader);
    });

    String expectedMessage = "Duplicated Key: properties";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.trim().equals(expectedMessage.trim()));
  }
}
