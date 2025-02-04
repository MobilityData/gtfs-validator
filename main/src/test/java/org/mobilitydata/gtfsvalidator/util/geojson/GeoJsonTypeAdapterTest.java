package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
  @Test(expected = DuplicateJsonKeyException.class)
  public void testDuplicateKeyExceptionSameLevel() {
    final var json = "{ \"name\": 1, \"name\": 2 }";
    gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
  }

  /**
   * Test that the custom JSON type adapter can handle a simple JSON object that don't contain
   * duplicate keys.
   */
  @Test
  public void testDuplicateKeyExceptionDiffLevel() {
    String json = "{\"name\": \"Alice\", \"age\": 25, \"level2\": { \"name\": \"Bob\"  }  }";
    gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
  }

  /**
   * Test that the custom JSON type adapter can handle a throws DuplicateJsonKeyException when: - A
   * JSON object has two keys with the same name at level different from the root.
   */
  @Test(expected = DuplicateJsonKeyException.class)
  public void testDuplicateKeyExceptionInnerLevel() {
    String json =
        "{\"name\": \"Alice\", \"age\": 25, \"level2\": { \"name1\": \"Bob\", \"name1\": \"Bob\"  }  }";
    gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
  }
}
