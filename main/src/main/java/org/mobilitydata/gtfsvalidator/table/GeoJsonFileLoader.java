package org.mobilitydata.gtfsvalidator.table;

import com.google.common.flogger.FluentLogger;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.*;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.notice.GeoJsonDuplicatedElementNotice;
import org.mobilitydata.gtfsvalidator.util.geojson.*;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

/**
 * This class knows how to load a GeoJSON file. Typical GeoJSON file: { "type": "FeatureCollection",
 * "features": [ { "id": "area_548", "type": "Feature", "geometry": { "type": "Polygon",
 * "coordinates": [ [ [ -122.4112929, 48.0834848 ], ... ] ] }, "properties": { "stop_name": "Some
 * name", "stop_desc": "Some description" } }, ... ] }
 */
public class GeoJsonFileLoader extends TableLoader {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private GeoJsonGeometryValidator geometryValidator;

  @Override
  public GtfsEntityContainer load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream inputStream,
      NoticeContainer noticeContainer) {
    GtfsGeoJsonFileDescriptor geoJsonFileDescriptor = (GtfsGeoJsonFileDescriptor) fileDescriptor;
    geometryValidator = new GeoJsonGeometryValidator(noticeContainer);
    try {
      List<GtfsGeoJsonFeature> entities = extractFeaturesFromStream(inputStream, noticeContainer);
      return geoJsonFileDescriptor.createContainerForEntities(entities, noticeContainer);
    } catch (JsonParseException jpex) {
      noticeContainer.addValidationNotice(
          new MalformedJsonNotice(GtfsGeoJsonFeature.FILENAME, jpex.getMessage()));
      logger.atSevere().withCause(jpex).log("Malformed JSON in locations.geojson");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (IOException ioex) {
      noticeContainer.addSystemError(new IOError(ioex));
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (UnparsableGeoJsonFeatureException ugex) {
      logger.atSevere().withCause(ugex).log("Unparsable GeoJSON feature");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (Exception ex) {
      logger.atSevere().withCause(ex).log("Error while loading locations.geojson");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    }
  }

  /**
   * Extracts features from the provided GeoJSON input stream.
   *
   * @param inputStream the input stream containing GeoJSON data
   * @param noticeContainer the container to collect validation notices
   * @return a list of parsed GeoJSON features
   * @throws IOException if an I/O error occurs while reading the input stream
   * @throws UnparsableGeoJsonFeatureException if any GeoJSON feature is unparsable
   */
  public List<GtfsGeoJsonFeature> extractFeaturesFromStream(
      InputStream inputStream, NoticeContainer noticeContainer)
      throws IOException, UnparsableGeoJsonFeatureException {
    List<GtfsGeoJsonFeature> features = new ArrayList<>();
    boolean hasUnparsableFeature = false;
    GsonBuilder gsonBuilder = new GsonBuilder();
    // Using the MapJsonTypeAdapter to be able to parse JSON objects with duplicate keys and
    // unsupported Gson library features
    gsonBuilder.registerTypeAdapter(
        new TypeToken<Map<String, Object>>() {}.getType(), new MapJsonTypeAdapter());
    Gson gson = gsonBuilder.create();

    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonElement root =
          gson.toJsonTree(gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType()));
      if (!root.isJsonObject()) {
        throw new JsonParseException("Expected a JSON object at the root");
      }
      JsonObject jsonObject = root.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        String key = entry.getKey();
        if (!"type".equals(key) && !"features".equals(key)) {
          noticeContainer.addValidationNotice(
              new GeoJsonUnknownElementNotice(GtfsGeoJsonFeature.FILENAME, key));
        }
      }
      if (!jsonObject.has("type")) {
        noticeContainer.addValidationNotice(new MissingRequiredElementNotice(null, "type", null));
        throw new UnparsableGeoJsonFeatureException("Missing required field 'type'");
      } else if (!jsonObject.get("type").getAsString().equals("FeatureCollection")) {
        noticeContainer.addValidationNotice(
            new UnsupportedGeoJsonTypeNotice(
                jsonObject.get("type").getAsString(),
                "Unsupported GeoJSON type: "
                    + jsonObject.get("type").getAsString()
                    + ". Use 'FeatureCollection' instead."));
        throw new UnparsableGeoJsonFeatureException("Unsupported GeoJSON type");
      }
      JsonArray featuresArray = jsonObject.getAsJsonArray("features");
      for (int i = 0; i < featuresArray.size(); i++) {
        JsonElement feature = featuresArray.get(i);
        GtfsGeoJsonFeature gtfsGeoJsonFeature = extractFeature(feature, noticeContainer, i);
        hasUnparsableFeature |= gtfsGeoJsonFeature == null;
        if (gtfsGeoJsonFeature != null) {
          features.add(gtfsGeoJsonFeature);
        }
      }
    } catch (DuplicateJsonKeyException exception) {
      noticeContainer.addValidationNotice(
          new GeoJsonDuplicatedElementNotice(GtfsGeoJsonFeature.FILENAME, exception.getKey()));
    }
    if (hasUnparsableFeature) {
      throw new UnparsableGeoJsonFeatureException("Unparsable GeoJSON feature");
    }
    return features;
  }

  public GtfsGeoJsonFeature extractFeature(
      JsonElement feature, NoticeContainer noticeContainer, int featureIndex) {
    GtfsGeoJsonFeature gtfsGeoJsonFeature;
    List<String> missingRequiredFields = new ArrayList<>();
    String featureId = null;
    if (feature.isJsonObject()) {
      JsonObject featureObject = feature.getAsJsonObject();

      // Check for unknown elements in the featureObject
      for (Map.Entry<String, JsonElement> entry : featureObject.entrySet()) {
        String key = entry.getKey();
        if (!GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME.equals(key)
            && !GtfsGeoJsonFeature.FEATURE_TYPE_FIELD_NAME.equals(key)
            && !GtfsGeoJsonFeature.FEATURE_PROPERTIES_FIELD_NAME.equals(key)
            && !GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME.equals(key)) {
          noticeContainer.addValidationNotice(
              new GeoJsonUnknownElementNotice(GtfsGeoJsonFeature.FILENAME, key));
        }
      }

      // Handle feature id
      if (!featureObject.has(GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME);
      } else {
        featureId = featureObject.get(GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME).getAsString();
        if (featureId == null || featureId.isEmpty()) {
          missingRequiredFields.add(
              GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME);
        }
      }

      // Handle feature type
      if (!featureObject.has(GtfsGeoJsonFeature.FEATURE_TYPE_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJsonFeature.FEATURE_TYPE_FIELD_NAME);
      } else if (!featureObject
          .get(GtfsGeoJsonFeature.FEATURE_TYPE_FIELD_NAME)
          .getAsString()
          .equals("Feature")) {
        noticeContainer.addValidationNotice(
            new UnsupportedFeatureTypeNotice(
                featureIndex,
                featureId,
                featureObject.get(GtfsGeoJsonFeature.FEATURE_TYPE_FIELD_NAME).getAsString()));
      }

      // Handle properties
      if (!featureObject.has(GtfsGeoJsonFeature.FEATURE_PROPERTIES_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJsonFeature.FEATURE_PROPERTIES_FIELD_NAME);
      }

      // Handle geometry
      if (!featureObject.has(GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME);
      } else {
        JsonObject geometry = featureObject.getAsJsonObject(GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME);
        // Check for unknown elements in the geometry object
        for (Map.Entry<String, JsonElement> entry : geometry.entrySet()) {
          String key = entry.getKey();
          if (!GtfsGeoJsonFeature.GEOMETRY_TYPE_FIELD_NAME.equals(key)
              && !GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME.equals(key)) {
            noticeContainer.addValidationNotice(
                new GeoJsonUnknownElementNotice(GtfsGeoJsonFeature.FILENAME, key));
          }
        }
        // Handle geometry type and coordinates
        if (!geometry.has(GtfsGeoJsonFeature.GEOMETRY_TYPE_FIELD_NAME)) {
          missingRequiredFields.add(
              GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME
                  + '.'
                  + GtfsGeoJsonFeature.GEOMETRY_TYPE_FIELD_NAME);
        } else if (!geometry.has(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME)) {
          missingRequiredFields.add(
              GtfsGeoJsonFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJsonFeature.GEOMETRY_FIELD_NAME
                  + '.'
                  + GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME);
        } else if (missingRequiredFields
            .isEmpty()) { // All required fields are present - Validate geometry
          // Create a new GtfsGeoJsonFeature
          gtfsGeoJsonFeature = new GtfsGeoJsonFeature();
          gtfsGeoJsonFeature.setFeatureId(
              featureObject.get(GtfsGeoJsonFeature.FEATURE_ID_FIELD_NAME).getAsString());
          gtfsGeoJsonFeature.setFeatureIndex(featureIndex);

          String type = geometry.get(GtfsGeoJsonFeature.GEOMETRY_TYPE_FIELD_NAME).getAsString();

          validateCoordinates(noticeContainer, featureIndex, geometry, gtfsGeoJsonFeature);

          if (type.equals(GeometryType.POLYGON.getType())) {
            gtfsGeoJsonFeature.setGeometryType(GeometryType.POLYGON);
            Polygon polygon =
                geometryValidator.createPolygon(
                    geometry.getAsJsonArray(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJsonFeature,
                    featureIndex);
            if (polygon == null) return null;
            gtfsGeoJsonFeature.setGeometryDefinition(polygon);

          } else if (type.equals(GeometryType.MULTI_POLYGON.getType())) {
            gtfsGeoJsonFeature.setGeometryType(GeometryType.MULTI_POLYGON);
            MultiPolygon multiPolygon =
                geometryValidator.createMultiPolygon(
                    geometry.getAsJsonArray(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJsonFeature,
                    featureIndex);
            if (multiPolygon == null) return null;
            gtfsGeoJsonFeature.setGeometryDefinition(multiPolygon);

          } else {
            noticeContainer.addValidationNotice(
                new UnsupportedGeometryTypeNotice(featureIndex, featureId, type));
          }
          JsonObject properties =
              featureObject.getAsJsonObject(GtfsGeoJsonFeature.FEATURE_PROPERTIES_FIELD_NAME);
          if (properties.has(GtfsGeoJsonFeature.FEATURE_PROPERTIES_STOP_NAME_FIELD_NAME)) {
            gtfsGeoJsonFeature.setStopName(
                properties
                    .get(GtfsGeoJsonFeature.FEATURE_PROPERTIES_STOP_NAME_FIELD_NAME)
                    .getAsString());
          }
          if (properties.has(GtfsGeoJsonFeature.FEATURE_PROPERTIES_STOP_DESC_FIELD_NAME)) {
            gtfsGeoJsonFeature.setStopDesc(
                properties
                    .get(GtfsGeoJsonFeature.FEATURE_PROPERTIES_STOP_DESC_FIELD_NAME)
                    .getAsString());
          }

          return gtfsGeoJsonFeature;
        }
      }
    }
    addMissingRequiredFieldsNotices(
        missingRequiredFields, noticeContainer, featureId, featureIndex);
    return null;
  }

  private static void validateCoordinates(
      NoticeContainer noticeContainer,
      int featureIndex,
      JsonObject geometry,
      GtfsGeoJsonFeature gtfsGeoJsonFeature) {
    // Validate that the coordinates are not near the origin or the poles
    JsonArray coordinates =
        geometry.getAsJsonArray(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME);
    for (int i = 0; i < coordinates.size(); i++) {
      for (int j = 0; j < coordinates.get(i).getAsJsonArray().size(); j++) {
        JsonArray point = coordinates.get(i).getAsJsonArray().get(j).getAsJsonArray();
        double lon = point.get(0).getAsDouble();
        double lat = point.get(1).getAsDouble();
        if (Math.abs(lon) <= 1 && Math.abs(lat) <= 1) {
          noticeContainer.addValidationNotice(
              new PointNearOriginNotice(
                  GtfsGeoJsonFeature.FILENAME,
                  gtfsGeoJsonFeature.featureId(),
                  lat,
                  lon,
                  featureIndex));
        } else if (Math.abs(lat) >= 89) {
          noticeContainer.addValidationNotice(
              new PointNearPoleNotice(
                  GtfsGeoJsonFeature.FILENAME,
                  gtfsGeoJsonFeature.featureId(),
                  lat,
                  lon,
                  featureIndex));
        }
      }
    }
  }

  private static void addMissingRequiredFieldsNotices(
      List<String> missingRequiredFields,
      NoticeContainer noticeContainer,
      String featureId,
      int featureIndex) {
    for (String missingRequiredField : missingRequiredFields) {
      noticeContainer.addValidationNotice(
          new MissingRequiredElementNotice(featureId, missingRequiredField, featureIndex));
    }
  }
}
