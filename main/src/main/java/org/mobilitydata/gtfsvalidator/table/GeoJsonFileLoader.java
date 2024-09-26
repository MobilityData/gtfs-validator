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

public class GeoJsonFileLoader extends TableLoader {

  @Override
  public GtfsEntityContainer load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream inputStream,
      NoticeContainer noticeContainer) {
    GtfsGeojsonFileDescriptor geojsonFileDescriptor = (GtfsGeojsonFileDescriptor) fileDescriptor;
    try {
      List<GtfsGeojsonFeature> entities = extractFeaturesFromStream(inputStream, noticeContainer);
      return geojsonFileDescriptor.createContainerForEntities(entities, noticeContainer);
    } catch (IOException ioex) {
      noticeContainer.addSystemError(new IOError(ioex));
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    }
  }

  public static List<GtfsGeojsonFeature> extractFeaturesFromStream(
      InputStream inputStream, NoticeContainer noticeContainer) throws IOException {
    List<GtfsGeojsonFeature> features = new ArrayList<>();
    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
      JsonArray featuresArray = jsonObject.getAsJsonArray("features");
      for (JsonElement feature : featuresArray) {
        GtfsGeojsonFeature gtfsGeojsonFeature = extractFeature(feature, noticeContainer);
        if (gtfsGeojsonFeature != null) {
          features.add(gtfsGeojsonFeature);
        }
      }
    }
    return features;
  }

  public static GtfsGeojsonFeature extractFeature(
      JsonElement feature, NoticeContainer noticeContainer) {
    GtfsGeojsonFeature gtfsGeojsonFeature = null;
    if (feature.isJsonObject()) {
      JsonObject featureObject = feature.getAsJsonObject();
      if (featureObject.has("properties")) {
        JsonObject properties = featureObject.getAsJsonObject("properties");
        // Add stop_name and stop_desc
      } else {
        // Add a notice because properties is required
      }
      if (featureObject.has("id")) {
        gtfsGeojsonFeature = new GtfsGeojsonFeature();
        gtfsGeojsonFeature.setFeatureId(featureObject.get("id").getAsString());
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
    return gtfsGeojsonFeature;
  }
}
