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
import org.mobilitydata.gtfsvalidator.util.geojson.GeoJSONGeometryValidator;
import org.mobilitydata.gtfsvalidator.util.geojson.GeometryType;
import org.mobilitydata.gtfsvalidator.util.geojson.UnparsableGeoJSONFeatureException;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;

/**
 * This class knows how to load a GeoJSON file. Typical GeoJSON file: { "type": "FeatureCollection",
 * "features": [ { "id": "area_548", "type": "Feature", "geometry": { "type": "Polygon",
 * "coordinates": [ [ [ -122.4112929, 48.0834848 ], ... ] ] }, "properties": { "stop_name": "Some
 * name", "stop_desc": "Some description" } }, ... ] }
 */
public class GeoJSONFileLoader extends TableLoader {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private GeoJSONGeometryValidator geometryValidator;

  @Override
  public GtfsEntityContainer load(
      GtfsFileDescriptor fileDescriptor,
      ValidatorProvider validatorProvider,
      InputStream inputStream,
      NoticeContainer noticeContainer) {
    GtfsGeoJSONFileDescriptor geoJSONFileDescriptor = (GtfsGeoJSONFileDescriptor) fileDescriptor;
    geometryValidator = new GeoJSONGeometryValidator(noticeContainer);
    try {
      List<GtfsGeoJSONFeature> entities = extractFeaturesFromStream(inputStream, noticeContainer);
      return geoJSONFileDescriptor.createContainerForEntities(entities, noticeContainer);
    } catch (JsonParseException jpex) {
      // TODO: Add a notice for malformed locations.geojson
      logger.atSevere().withCause(jpex).log("Malformed JSON in locations.geojson");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (IOException ioex) {
      noticeContainer.addSystemError(new IOError(ioex));
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (UnparsableGeoJSONFeatureException ugex) {
      logger.atSevere().withCause(ugex).log("Unparsable GeoJSON feature");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    } catch (Exception ex) {
      logger.atSevere().withCause(ex).log("Error while loading locations.geojson");
      return fileDescriptor.createContainerForInvalidStatus(TableStatus.UNPARSABLE_ROWS);
    }
  }

  public List<GtfsGeoJSONFeature> extractFeaturesFromStream(
      InputStream inputStream, NoticeContainer noticeContainer)
      throws IOException, UnparsableGeoJSONFeatureException {
    List<GtfsGeoJSONFeature> features = new ArrayList<>();
    boolean hasUnparsableFeature = false;
    try (InputStreamReader reader = new InputStreamReader(inputStream)) {
      JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
      JsonArray featuresArray = jsonObject.getAsJsonArray("features");
      int i = 0;
      for (JsonElement feature : featuresArray) {
        GtfsGeoJSONFeature gtfsGeoJSONFeature = extractFeature(feature, noticeContainer, i++);
        hasUnparsableFeature |= gtfsGeoJSONFeature == null;
        if (gtfsGeoJSONFeature != null) {
          features.add(gtfsGeoJSONFeature);
        }
      }
    }
    if (hasUnparsableFeature) {
      throw new UnparsableGeoJSONFeatureException("Unparsable GeoJSON feature");
    }
    return features;
  }

  public GtfsGeoJSONFeature extractFeature(
      JsonElement feature, NoticeContainer noticeContainer, int featureIndex) {
    GtfsGeoJSONFeature gtfsGeoJSONFeature;
    List<String> missingRequiredFields = new ArrayList<>();
    String featureId = null;
    if (feature.isJsonObject()) {
      JsonObject featureObject = feature.getAsJsonObject();
      // Handle feature id
      if (!featureObject.has(GtfsGeoJSONFeature.FEATURE_ID_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJSONFeature.FEATURE_ID_FIELD_NAME);
      } else {
        featureId = featureObject.get(GtfsGeoJSONFeature.FEATURE_ID_FIELD_NAME).getAsString();
        if (featureId == null || featureId.isEmpty()) {
          missingRequiredFields.add(
              GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJSONFeature.FEATURE_ID_FIELD_NAME);
        }
      }

      // Handle properties
      if (!featureObject.has(GtfsGeoJSONFeature.FEATURE_PROPERTIES_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJSONFeature.FEATURE_PROPERTIES_FIELD_NAME);
      } else {
        // TODO: parse stop_name and stop_desc
      }

      // Handle geometry
      if (!featureObject.has(GtfsGeoJSONFeature.GEOMETRY_FIELD_NAME)) {
        missingRequiredFields.add(
            GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                + '.'
                + GtfsGeoJSONFeature.GEOMETRY_FIELD_NAME);
      } else {
        JsonObject geometry = featureObject.getAsJsonObject(GtfsGeoJSONFeature.GEOMETRY_FIELD_NAME);
        // Handle geometry type and coordinates
        if (!geometry.has(GtfsGeoJSONFeature.GEOMETRY_TYPE_FIELD_NAME)) {
          missingRequiredFields.add(
              GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJSONFeature.GEOMETRY_FIELD_NAME
                  + '.'
                  + GtfsGeoJSONFeature.GEOMETRY_TYPE_FIELD_NAME);
        } else if (!geometry.has(GtfsGeoJSONFeature.GEOMETRY_COORDINATES_FIELD_NAME)) {
          missingRequiredFields.add(
              GtfsGeoJSONFeature.FEATURE_COLLECTION_FIELD_NAME
                  + '.'
                  + GtfsGeoJSONFeature.GEOMETRY_FIELD_NAME
                  + '.'
                  + GtfsGeoJSONFeature.GEOMETRY_COORDINATES_FIELD_NAME);
        } else if (missingRequiredFields
            .isEmpty()) { // All required fields are present - Validate geometry
          // Create a new GtfsGeoJsonFeature
          gtfsGeoJSONFeature = new GtfsGeoJSONFeature();
          gtfsGeoJSONFeature.setFeatureId(
              featureObject.get(GtfsGeoJSONFeature.FEATURE_ID_FIELD_NAME).getAsString());

          String type = geometry.get(GtfsGeoJSONFeature.GEOMETRY_TYPE_FIELD_NAME).getAsString();

          if (type.equals(GeometryType.POLYGON.getType())) {
            gtfsGeoJSONFeature.setGeometryType(GeometryType.POLYGON);
            Polygon polygon =
                geometryValidator.createPolygon(
                    geometry.getAsJsonArray(GtfsGeoJSONFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJSONFeature);
            if (polygon == null) return null;
            gtfsGeoJSONFeature.setGeometryDefinition(polygon);

          } else if (type.equals(GeometryType.MULTI_POLYGON.getType())) {
            gtfsGeoJSONFeature.setGeometryType(GeometryType.MULTI_POLYGON);
            MultiPolygon multiPolygon =
                geometryValidator.createMultiPolygon(
                    geometry.getAsJsonArray(GtfsGeoJSONFeature.GEOMETRY_COORDINATES_FIELD_NAME),
                    gtfsGeoJSONFeature);
            if (multiPolygon == null) return null;
            gtfsGeoJSONFeature.setGeometryDefinition(multiPolygon);

          } else {
            noticeContainer.addValidationNotice(
                new UnsupportedGeometryTypeNotice(featureIndex, featureId, type));
          }

          return gtfsGeoJSONFeature;
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
    if (featureId == null) {
      featureId = "N/A";
    }
    for (String missingRequiredField : missingRequiredFields) {
      noticeContainer.addValidationNotice(
          new MissingRequiredElementNotice(featureId, missingRequiredField, featureIndex));
    }
  }
}
