package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A custom JSON type adapter for parsing JSON objects with duplicate keys. The target class is
 * {@link Map}{@code <String, Object>}. as JSonElement is captured by the default Gson TypeAdapter.
 *
 * <p>When a JSON object has two keys with the same name at the same level, this type adapter throws
 * a {@link DuplicateJsonKeyException}.
 */
public class MapJsonTypeAdapter extends TypeAdapter<Map<String, Object>> {

  private static final Set<String> ALLOWED_KEYS = Set.of("type", "features", "coordinates", "id", "properties", "geometry");

  @Override
  public void write(JsonWriter out, Map<String, Object> value) throws IOException {
    new Gson().toJson(value, Map.class, out);
  }

  @Override
  public Map<String, Object> read(JsonReader in) throws IOException {
    return parseJsonObject(in);
  }

  private Map<String, Object> parseJsonObject(JsonReader in) throws IOException {
    Map<String, Object> map = new LinkedHashMap<>();

    in.beginObject();
    while (in.hasNext()) {
      String key = in.nextName();

      if (map.containsKey(key)) {
        throw new DuplicateJsonKeyException(key);
      }

      if (!ALLOWED_KEYS.contains(key)) {
        throw new UnknownJsonKeyException(key);
      }

      Object value = parseJsonValue(in);
      map.put(key, value);
    }
    in.endObject();

    return map;
  }

  private Object parseJsonValue(JsonReader in) throws IOException {
    switch (in.peek()) {
      case BEGIN_OBJECT:
        return parseJsonObject(in);
      case BEGIN_ARRAY:
        return parseJsonArray(in);
      case STRING:
        return in.nextString();
      case NUMBER:
        return in.nextDouble();
      case BOOLEAN:
        return in.nextBoolean();
      case NULL:
        in.nextNull();
        return null;
      default:
        throw new JsonParseException("Unexpected JSON token: " + in.peek());
    }
  }

  private Object parseJsonArray(JsonReader in) throws IOException {
    JsonArray jsonArray = new JsonArray();
    in.beginArray();
    while (in.hasNext()) {
      jsonArray.add(new Gson().toJsonTree(parseJsonValue(in)));
    }
    in.endArray();
    return jsonArray;
  }
}

