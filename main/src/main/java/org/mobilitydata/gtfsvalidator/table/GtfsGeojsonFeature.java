package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** This class contains the information from one feature in the geojson file. */
public final class GtfsGeojsonFeature implements GtfsEntity {
  public static final String FILENAME = "locations.geojson";

  public static final String LOCATION_ID_FIELD_NAME = "location_id";

  private String locationId; // The id of a feature in the GeoJSON file.

  public GtfsGeojsonFeature() {}

  // TODO: Change the interface hierarchy so we dont need this. It's not relevant for geojson
  @Override
  public int csvRowNumber() {
    return 0;
  }

  @Nonnull
  public String locationId() {
    return locationId;
  }

  public boolean hasLocationId() {
    return locationId != null;
  }

  public void setLocationId(@Nullable String locationId) {
    this.locationId = locationId;
  }
}
