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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsEntityBuilder;

/** Test class to avoid dependency on the real GtfsTestEntity and annotation processor. */
public class GtfsTestEntity implements GtfsEntity {
  public static final String FILENAME = "filename.txt";

  public static final String ID_FIELD_NAME = "id";

  public static final String CODE_FIELD_NAME = "code";

  public static final String DEFAULT_ID = "";

  public static final String DEFAULT_CODE = "";

  private int csvRowNumber;

  private String stopId;

  private String stopCode;

  private short bitField0_;

  /** Use {@link Builder} class to construct an object. */
  private GtfsTestEntity() {}

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

  public static final class Builder implements GtfsEntityBuilder<GtfsTestEntity> {
    private int csvRowNumber;

    private String stopId;

    private String stopCode;

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
    public GtfsTestEntity.Builder setCsvRowNumber(int value) {
      csvRowNumber = value;
      return this;
    }

    @Nonnull
    public String stopId() {
      return stopId;
    }

    @Nonnull
    public GtfsTestEntity.Builder setStopId(@Nullable String value) {
      if (value == null) {
        return clearStopId();
      }
      stopId = value;
      bitField0_ |= 0x1;
      return this;
    }

    @Nonnull
    public GtfsTestEntity.Builder clearStopId() {
      stopId = DEFAULT_ID;
      bitField0_ &= ~0x1;
      return this;
    }

    @Nonnull
    public String stopCode() {
      return stopCode;
    }

    @Nonnull
    public GtfsTestEntity.Builder setStopCode(@Nullable String value) {
      if (value == null) {
        return clearStopCode();
      }
      stopCode = value;
      bitField0_ |= 0x2;
      return this;
    }

    @Nonnull
    public GtfsTestEntity.Builder clearStopCode() {
      stopCode = DEFAULT_CODE;
      bitField0_ &= ~0x2;
      return this;
    }

    @Override
    public GtfsTestEntity build() {
      GtfsTestEntity entity = new GtfsTestEntity();
      entity.csvRowNumber = this.csvRowNumber;
      entity.bitField0_ = this.bitField0_;
      entity.stopId = this.stopId;
      entity.stopCode = this.stopCode;
      return entity;
    }

    @Override
    public void clear() {
      csvRowNumber = 0;
      bitField0_ = 0;
      stopId = DEFAULT_ID;
      stopCode = DEFAULT_CODE;
    }
  }
}
