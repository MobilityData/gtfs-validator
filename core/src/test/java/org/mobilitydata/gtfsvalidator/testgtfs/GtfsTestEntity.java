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

public class GtfsTestEntity implements GtfsEntity {
  public static final String FILENAME = "filename.txt";

  public static final String ID_FIELD_NAME = "id";

  public static final String CODE_FIELD_NAME = "code";

  public static final String DEFAULT_ID = "";

  public static final String DEFAULT_CODE = "";

  private int csvRowNumber;

  private String id;

  private String code;

  private short bitField0_;

  /** Use {@link Builder} class to construct an object. */
  private GtfsTestEntity() {}

  @Override
  public int csvRowNumber() {
    return csvRowNumber;
  }

  @Nonnull
  public String id() {
    return id;
  }

  public boolean hasId() {
    return (bitField0_ & 0x1) != 0;
  }

  public boolean hasCode() {
    return (bitField0_ & 0x2) != 0;
  }

  public static final class Builder implements GtfsEntityBuilder<GtfsTestEntity> {
    private int csvRowNumber;

    private String id;

    private String code;

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
    public String id() {
      return id;
    }

    @Nonnull
    public GtfsTestEntity.Builder setId(@Nullable String value) {
      if (value == null) {
        return clearStopId();
      }
      id = value;
      bitField0_ |= 0x1;
      return this;
    }

    @Nonnull
    public GtfsTestEntity.Builder clearStopId() {
      id = DEFAULT_ID;
      bitField0_ &= ~0x1;
      return this;
    }

    @Nonnull
    public String stopCode() {
      return code;
    }

    @Nonnull
    public GtfsTestEntity.Builder setCode(@Nullable String value) {
      if (value == null) {
        return clearStopCode();
      }
      code = value;
      bitField0_ |= 0x2;
      return this;
    }

    @Nonnull
    public GtfsTestEntity.Builder clearStopCode() {
      code = DEFAULT_CODE;
      bitField0_ &= ~0x2;
      return this;
    }

    @Override
    public GtfsTestEntity build() {
      GtfsTestEntity entity = new GtfsTestEntity();
      entity.csvRowNumber = this.csvRowNumber;
      entity.bitField0_ = this.bitField0_;
      entity.id = this.id;
      entity.code = this.code;
      return entity;
    }

    @Override
    public void clear() {
      csvRowNumber = 0;
      bitField0_ = 0;
      id = DEFAULT_ID;
      code = DEFAULT_CODE;
    }
  }
}
