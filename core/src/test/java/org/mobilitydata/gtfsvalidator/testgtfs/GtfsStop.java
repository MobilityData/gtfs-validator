/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.testgtfs;

import com.google.common.geometry.S2LatLng;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityBuilder;

/** Test class to avoid dependency on the real GtfsStop and annotation processor. */
public class GtfsStop implements GtfsEntity {
  public static final String FILENAME = "stops.txt";

  public static final String STOP_ID_FIELD_NAME = "stop_id";

  public static final String STOP_CODE_FIELD_NAME = "stop_code";

  public static final String STOP_NAME_FIELD_NAME = "stop_name";

  public static final String TTS_STOP_NAME_FIELD_NAME = "tts_stop_name";

  public static final String STOP_DESC_FIELD_NAME = "stop_desc";

  public static final String STOP_LAT_FIELD_NAME = "stop_lat";

  public static final String STOP_LON_FIELD_NAME = "stop_lon";

  public static final String DEFAULT_STOP_ID = "";

  public static final String DEFAULT_STOP_CODE = "";

  public static final String DEFAULT_STOP_NAME = "";

  public static final String DEFAULT_TTS_STOP_NAME = "";

  public static final String DEFAULT_STOP_DESC = "";

  public static final double DEFAULT_STOP_LAT = 0;

  public static final double DEFAULT_STOP_LON = 0;

  private int csvRowNumber;

  private String stopId;

  private String stopCode;

  private String stopName;

  private String ttsStopName;

  private String stopDesc;

  private double stopLat;

  private double stopLon;

  private short bitField0_;

  /** Use {@link Builder} class to construct an object. */
  private GtfsStop() {}

  @Override
  public int csvRowNumber() {
    return csvRowNumber;
  }

  @Nonnull
  public String stopId() {
    return stopId;
  }

  public boolean hasStopId() {
    return (bitField0_ & 0x1) != 0;
  }

  public boolean hasStopCode() {
    return (bitField0_ & 0x2) != 0;
  }

  public boolean hasStopName() {
    return (bitField0_ & 0x4) != 0;
  }

  public boolean hasTtsStopName() {
    return (bitField0_ & 0x8) != 0;
  }

  public boolean hasStopDesc() {
    return (bitField0_ & 0x10) != 0;
  }

  public boolean hasStopLat() {
    return (bitField0_ & 0x20) != 0;
  }

  public boolean hasStopLon() {
    return (bitField0_ & 0x40) != 0;
  }

  public static final class Builder implements GtfsEntityBuilder<GtfsStop> {
    private int csvRowNumber;

    private String stopId;

    private String stopCode;

    private String stopName;

    private String stopDesc;

    private double stopLat;

    private double stopLon;

    private short bitField0_;

    public Builder() {
      // Initialize all fields to default values.
      clear();
    }

    @Override
    public int csvRowNumber() {
      return csvRowNumber;
    }

    @Override
    public GtfsStop.Builder setCsvRowNumber(int value) {
      csvRowNumber = value;
      return this;
    }

    @Nonnull
    public String stopId() {
      return stopId;
    }

    @Nonnull
    public GtfsStop.Builder setStopId(@Nullable String value) {
      if (value == null) {
        return clearStopId();
      }
      stopId = value;
      bitField0_ |= 0x1;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopId() {
      stopId = DEFAULT_STOP_ID;
      bitField0_ &= ~0x1;
      return this;
    }

    @Nonnull
    public String stopCode() {
      return stopCode;
    }

    @Nonnull
    public GtfsStop.Builder setStopCode(@Nullable String value) {
      if (value == null) {
        return clearStopCode();
      }
      stopCode = value;
      bitField0_ |= 0x2;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopCode() {
      stopCode = DEFAULT_STOP_CODE;
      bitField0_ &= ~0x2;
      return this;
    }

    @Nonnull
    public String stopName() {
      return stopName;
    }

    @Nonnull
    public GtfsStop.Builder setStopName(@Nullable String value) {
      if (value == null) {
        return clearStopName();
      }
      stopName = value;
      bitField0_ |= 0x4;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopName() {
      stopName = DEFAULT_STOP_NAME;
      bitField0_ &= ~0x4;
      return this;
    }

    @Nonnull
    public String stopDesc() {
      return stopDesc;
    }

    @Nonnull
    public GtfsStop.Builder setStopDesc(@Nullable String value) {
      if (value == null) {
        return clearStopDesc();
      }
      stopDesc = value;
      bitField0_ |= 0x10;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopDesc() {
      stopDesc = DEFAULT_STOP_DESC;
      bitField0_ &= ~0x10;
      return this;
    }

    @Nonnull
    public double stopLat() {
      return stopLat;
    }

    @Nonnull
    public GtfsStop.Builder setStopLat(@Nullable Double value) {
      if (value == null) {
        return clearStopLat();
      }
      stopLat = value;
      bitField0_ |= 0x20;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopLat() {
      stopLat = DEFAULT_STOP_LAT;
      bitField0_ &= ~0x20;
      return this;
    }

    @Nonnull
    public double stopLon() {
      return stopLon;
    }

    @Nonnull
    public GtfsStop.Builder setStopLon(@Nullable Double value) {
      if (value == null) {
        return clearStopLon();
      }
      stopLon = value;
      bitField0_ |= 0x40;
      return this;
    }

    @Nonnull
    public GtfsStop.Builder clearStopLon() {
      stopLon = DEFAULT_STOP_LON;
      bitField0_ &= ~0x40;
      return this;
    }

    public S2LatLng stopLatLon() {
      return S2LatLng.fromDegrees(stopLat, stopLon);
    }

    @Override
    public GtfsStop build() {
      GtfsStop entity = new GtfsStop();
      entity.csvRowNumber = this.csvRowNumber;
      entity.bitField0_ = this.bitField0_;
      entity.stopId = this.stopId;
      entity.stopCode = this.stopCode;
      entity.stopName = this.stopName;
      entity.stopDesc = this.stopDesc;
      entity.stopLat = this.stopLat;
      entity.stopLon = this.stopLon;
      return entity;
    }

    @Override
    public void clear() {
      csvRowNumber = 0;
      bitField0_ = 0;
      stopId = DEFAULT_STOP_ID;
      stopCode = DEFAULT_STOP_CODE;
      stopName = DEFAULT_STOP_NAME;
      stopDesc = DEFAULT_STOP_DESC;
      stopLat = DEFAULT_STOP_LAT;
      stopLon = DEFAULT_STOP_LON;
    }
  }

  //  @Override
  //  public int csvRowNumber() {
  //    return 0;
  //  }
}
