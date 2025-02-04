package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GeoJsonTypeAdapter extends TypeAdapter<JsonElement> {

  @Override
  public void write(JsonWriter out, JsonElement value) throws IOException {
    out.jsonValue(value.toString());
  }

  @Override
  public JsonElement read(JsonReader in) throws IOException {
    return readElement(in, new HashSet<>()); // Start with an empty set
  }

  private JsonElement readElement(JsonReader in, Set<String> parentKeys) throws IOException {
    if (in.peek() == JsonToken.BEGIN_OBJECT) {
      Set<String> localKeys = new HashSet<>(parentKeys);  // Track keys for this object
      JsonObject jsonObject = new JsonObject();
      in.beginObject();

      while (in.hasNext()) {
        String key = in.nextName();
        if (!localKeys.add(key)) {
          System.out.println(">>> Duplicate key detected: " + key);
          throw new JsonParseException("Duplicated element in locations.geojson: " + key);
        }
        jsonObject.add(key, readElement(in, localKeys));  // Recursively process
      }

      in.endObject();
      return jsonObject;
    } else {
      return Streams.parse(in);  // Handle primitive values
    }
  }

  private boolean isKnownElement(String key) {
    Set<String> knownElements = Set.of("type", "features", "id", "properties", "geometry", "coordinates");
    return knownElements.contains(key);
  }
}