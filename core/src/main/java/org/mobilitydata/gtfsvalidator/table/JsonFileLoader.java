package org.mobilitydata.gtfsvalidator.table;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.IOError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

public class JsonFileLoader {

  public static GtfsJsonContainer load(
      GtfsJsonDescriptor tableDescriptor,
      ValidatorProvider validatorProvider,
      InputStream inputStream,
      NoticeContainer noticeContainer) {
    try {
      List<GtfsJson> entities = extractFeaturesFromStream(inputStream, noticeContainer);
//      List<String> locationIds = extractIdsFromStream(inputStream);


//      for (String locationId : locationIds) {
//        GtfsJson entity = new GtfsJson();
//        entity.setLocationId(locationId);
//        // builder.setLocationId(locationId);
//        entities.add(entity);
//      }

      GtfsJsonContainer<GtfsJson, GtfsJsonDescriptor> container =
          tableDescriptor.createContainerForEntities(entities, noticeContainer);
      return container;
    } catch (IOException ioex) {
      noticeContainer.addSystemError(new IOError(ioex));
      return (GtfsJsonContainer)
          tableDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    }
  }

  public static List<GtfsJson> extractFeaturesFromStream(InputStream inputStream, NoticeContainer noticeContainer) throws IOException {
    List<GtfsJson> features = new ArrayList<>();
    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
      JsonArray featuresArray = jsonObject.getAsJsonArray("features");
      for (JsonElement feature : featuresArray) {
        GtfsJson gtfsJson = extractFeature(feature, noticeContainer);
        if (gtfsJson != null) {
          features.add(gtfsJson);
        }
      }
    }
    return features;
  }

  public static GtfsJson extractFeature(JsonElement feature, NoticeContainer noticeContainer) {
    GtfsJson gtfsJson = new GtfsJson();
    if (feature.isJsonObject()) {
      JsonObject featureObject = feature.getAsJsonObject();
      if (featureObject.has("properties")) {
        JsonObject properties = featureObject.getAsJsonObject("properties");
        // Add stop_name and stop_desc
      } else {
        // Add a notice because properties is required
      }
      if (featureObject.has("id")) {
        gtfsJson.setLocationId(featureObject.get("id").getAsString());
      } else {
        // Add a notice because id is required
      }

      if (featureObject.has("geometry")) {
        JsonObject geometry = featureObject.getAsJsonObject("geometry");
        if (geometry.has("type")) {
          String type = geometry.get("type").getAsString();
          if (type.equals("Polygon")) {
            // Extract the polygon
          } else if (type.equals("Multipolygon")) {
            // extract the multipolygon
          }
        } else {
          // Add a notice because type is required
        }
      } else {
        // Add a notice because geometry is required
      }
    }
    return gtfsJson;
  }

  public static List<String> extractIdsFromStream(InputStream inputStream) throws IOException {
    List<String> ids = new ArrayList<>();
    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
      JsonArray features = jsonObject.getAsJsonArray("features");
      for (JsonElement feature : features) {
        JsonObject featureObject = feature.getAsJsonObject();
        if (featureObject.has("id")) {
          ids.add(featureObject.get("id").getAsString());
        }
      }
    }
    return ids;
  }
}
