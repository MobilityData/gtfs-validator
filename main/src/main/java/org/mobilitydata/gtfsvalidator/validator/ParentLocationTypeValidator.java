/*
 * Copyright 2020 Google LLC
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
package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;

/**
 * Validates `location_type` of the referenced `parent_station`.
 *
 * <p>Generated notice: {@link WrongParentLocationTypeNotice}.
 */
@GtfsValidator
public class ParentLocationTypeValidator extends FileValidator {

  private final GtfsStopTableContainer stopTable;

  @Inject
  ParentLocationTypeValidator(GtfsStopTableContainer stopTable) {
    this.stopTable = stopTable;
  }

  private GtfsLocationType expectedParentLocationType(GtfsLocationType locationType) {
    switch (locationType) {
      case STOP:
      case ENTRANCE:
      case GENERIC_NODE:
        return GtfsLocationType.STATION;
      case BOARDING_AREA:
        return GtfsLocationType.STOP;
      default:
        return GtfsLocationType.UNRECOGNIZED;
    }
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStop location : stopTable.getEntities()) {
      if (!location.hasParentStation()) {
        continue;
      }
      Optional<GtfsStop> optionalParentLocation = stopTable.byStopId(location.parentStation());
      if (optionalParentLocation.isEmpty()) {
        // Broken reference is reported in another rule.
        continue;
      }
      GtfsStop parentLocation = optionalParentLocation.get();
      GtfsLocationType expected = expectedParentLocationType(location.locationType());
      if (expected != GtfsLocationType.UNRECOGNIZED && parentLocation.locationType() != expected) {
        noticeContainer.addValidationNotice(
            new WrongParentLocationTypeNotice(
                location.csvRowNumber(),
                location.stopId(),
                location.stopName(),
                location.locationTypeValue(),
                parentLocation.csvRowNumber(),
                location.parentStation(),
                parentLocation.stopName(),
                parentLocation.locationTypeValue(),
                expected.getNumber()));
      }
    }
  }

  /**
   * Incorrect type of the parent location.
   *
   * <p>Value of field `location_type` of parent found in field `parent_station` is invalid.
   *
   * <p>According to spec
   *
   * <pre>
   * - _Stop/platform_ can only have _Station_ as parent
   * - _Station_ can NOT have a parent
   * - _Entrance/exit_ or _generic node_ can only have _Station_ as parent
   * - _Boarding Area_ can only have _Platform_ as parent
   * </pre>
   *
   * Any other combination raise this error.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsStopSchema.class, GtfsStopTimeSchema.class}))
  static class WrongParentLocationTypeNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The id of the faulty record. */
    private final String stopId;

    /** The faulty record's `stops.stop_name`. */
    private final String stopName;

    /** The faulty record's `stops.location_type`. */
    private final int locationType;

    /** The row number of the faulty record's parent. */
    private final long parentCsvRowNumber;

    /** The id of the faulty record's parent station. */
    private final String parentStation;

    /** The stop name of the faulty record's parent. */
    private final String parentStopName;

    /** The location type of the faulty record's parent. */
    private final int parentLocationType;

    /** The expected location type of the faulty record. */
    private final int expectedLocationType;

    WrongParentLocationTypeNotice(
        int csvRowNumber,
        String stopId,
        String stopName,
        int locationType,
        long parentCsvRowNumber,
        String parentStation,
        String parentStopName,
        int parentLocationType,
        int expectedLocationType) {
      super();
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.stopName = stopName;
      this.locationType = locationType;
      this.parentCsvRowNumber = parentCsvRowNumber;
      this.parentStation = parentStation;
      this.parentStopName = parentStopName;
      this.parentLocationType = parentLocationType;
      this.expectedLocationType = expectedLocationType;
    }
  }
}
