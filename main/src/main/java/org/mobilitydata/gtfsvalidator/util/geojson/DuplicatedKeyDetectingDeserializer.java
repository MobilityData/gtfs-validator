package org.mobilitydata.gtfsvalidator.util.geojson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class DuplicatedKeyDetectingDeserializer implements JsonDeserializer<JsonElement> {
  @Override
  public JsonElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject jsonObject = json.getAsJsonObject();
      Set<String> keys = new HashSet<>();
      for (String key : jsonObject.keySet()) {
        if (!keys.add(key)) {
          throw new JsonParseException("Duplicated key: " + key);
        }
      }
    }
    return json;
  }
}
