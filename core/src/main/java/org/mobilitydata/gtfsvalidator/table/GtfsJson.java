package org.mobilitydata.gtfsvalidator.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GtfsJson implements GtfsEntity {
  public static final String FILENAME = "locations.geojson";

  public static final String LOCATION_ID_FIELD_NAME = "location_id";

  private String locationId;   // The id of a feature in the GeoJSON file.

  public GtfsJson() {}

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

  //  public static final class Builder implements GtfsEntityBuilder<GtfsJson> {
  //    private static final String DEFAULT_LOCATION_ID = null;
  //    private int csvRowNumber;
  //
  //    private String locationId;
  //
  //    public Builder() {
  //      // Initialize all fields to default values.
  //      clear();
  //    }
  //
  //    @Override
  //    public int csvRowNumber() {
  //      return csvRowNumber;
  //    }
  //
  //    @Override
  //    public GtfsJson.Builder setCsvRowNumber(int value) {
  //      csvRowNumber = value;
  //      return this;
  //    }
  //
  //    @Nonnull
  //    public String locationId() {
  //      return locationId;
  //    }
  //
  //    @Nonnull
  //    public GtfsJson.Builder setLocationId(@Nullable String value) {
  //      if (value == null) {
  //        return clearLocationId();
  //      }
  //      locationId = value;
  //      return this;
  //    }
  //
  //    @Nonnull
  //    public GtfsJson.Builder clearLocationId() {
  //      locationId = DEFAULT_LOCATION_ID;
  //      return this;
  //    }
  //
  //    @Override
  //    public GtfsJson build() {
  //      GtfsJson entity = new GtfsJson();
  //
  //      return entity;
  //    }
  //
  //    @Override
  //    public void clear() {
  //      csvRowNumber = 0;
  //      locationId = DEFAULT_LOCATION_ID;
  //    }
  //  }
}
