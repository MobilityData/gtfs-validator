package org.mobilitydata.gtfsvalidator.table;

import com.google.common.flogger.FluentLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.*;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.util.geojson.GeoJsonGeometryValidator;
import org.mobilitydata.gtfsvalidator.util.geojson.GeometryType;
import org.mobilitydata.gtfsvalidator.util.geojson.UnparsableGeoJsonFeatureException;
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
      noticeContainer.addValidationNotice(new MalformedJsonNotice(GtfsGeoJsonFeature.FILENAME));
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

  public List<GtfsGeoJsonFeature> extractFeaturesFromStream(
      InputStream inputStream, NoticeContainer noticeContainer)
      throws IOException, UnparsableGeoJsonFeatureException {
    List<GtfsGeoJsonFeature> features = new ArrayList<>();
    boolean hasUnparsableFeature = false;
    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
      if (!jsonObject.has("type")) {
        noticeContainer.addValidationNotice(new MissingRequiredElementNotice(null, "type", null));
        throw new UnparsableGeoJsonFeatureException("Missing required field 'type'");
      } else if (!jsonObject.get("type").getAsString().equals("FeatureCollection")) {
        noticeContainer.addValidationNotice(
            new UnsupportedGeoJsonTypeNotice(jsonObject.get("type").getAsString()));
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

          String type = geometry.get(GtfsGeoJsonFeature.GEOMETRY_TYPE_FIELD_NAME).getAsString();

          if (type.equals(GeometryType.POLYGON.getType())) {
            gtfsGeoJsonFeature.setGeometryType(GeometryType.POLYGON);
            Polygon polygon =
                geometryValidator.createPolygon(
                    geometry.getAsJsonArray(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJsonFeature);
            if (polygon == null) return null;
            gtfsGeoJsonFeature.setGeometryDefinition(polygon);

          } else if (type.equals(GeometryType.MULTI_POLYGON.getType())) {
            gtfsGeoJsonFeature.setGeometryType(GeometryType.MULTI_POLYGON);
            MultiPolygon multiPolygon =
                geometryValidator.createMultiPolygon(
                    geometry.getAsJsonArray(GtfsGeoJsonFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJsonFeature);
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
    addMissingRequiredFieldsNotice(missingRequiredFields, noticeContainer, featureId, featureIndex);
    return null;
  }

  private static void addMissingRequiredFieldsNotice(
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
