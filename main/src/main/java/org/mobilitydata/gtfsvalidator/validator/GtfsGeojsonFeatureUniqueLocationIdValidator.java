/*
 * Copyright 2024 MobilityData
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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeature;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeaturesContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationsSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;

/**
 * Validates that the feature id from "locations.geojson" is not a duplicate of any stop_id from
 * "stops.txt" or location_group_id from "location_groups.txt"
 *
 * <p>Generated notice: {@link UniqueLocationIdViolationNotice}.
 */
@GtfsValidator
public class GtfsGeojsonFeatureUniqueLocationIdValidator extends FileValidator {
  private final GtfsStopTableContainer stopTableContainer;

  // Remove this comment when the location_group_stops.txt file is added to the GTFS schema
  // private final GtfsLocationGroupStopsTableContainer  locationGroupStopsTableContainer;

  private final GtfsGeojsonFeaturesContainer geojsonFeatureContainer;

  @Inject
  GtfsGeojsonFeatureUniqueLocationIdValidator(
      GtfsGeojsonFeaturesContainer geojsonFeatureContainer,
      GtfsStopTableContainer stopTableContainer
      //        , GtfsLocationGroupStopsTableContainer locationGroupStopsTableContainer
      ) {
    this.geojsonFeatureContainer = geojsonFeatureContainer;

    this.stopTableContainer = stopTableContainer;
    //    this.locationGroupStopsTableContainer = locationGroupStopsTableContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsGeojsonFeature json : geojsonFeatureContainer.getEntities()) {
      String locationId = json.locationId();
      if (locationId.isEmpty()) {
        continue;
      }

      Optional<GtfsStop> stop = stopTableContainer.byStopId(locationId);
      stop.ifPresent(
          gtfsStop ->
              noticeContainer.addValidationNotice(
                  new UniqueLocationIdViolationNotice(
                      locationId,
                      GtfsStop.FILENAME,
                      GtfsStop.STOP_ID_FIELD_NAME,
                      gtfsStop.csvRowNumber())));
    }
  }

  /**
   * Feature id from locations.geojson already used.
   *
   * <p>The id of one of the features of the locations.geojson file already exists in stops.txt or
   * location_groups.txt
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files =
          @GtfsValidationNotice.FileRefs({
            GtfsLocationsSchema.class,
            GtfsStopTimeSchema.class,
            GtfsLocationGroupsSchema.class
          }),
      sections = @GtfsValidationNotice.SectionRefs(FILE_REQUIREMENTS))
  public static class UniqueLocationIdViolationNotice extends ValidationNotice {

    /** The id that already exists. */
    private final String id;

    /** The name of the file that already has this id. */
    private final String fileWithIdAlreadyPresent;

    /** The name of the field that contains this id. */
    private final String fieldNameInFile;

    /** The row of the record in the file where the id is already present. */
    private final int csvRowNumber;

    public UniqueLocationIdViolationNotice(
        String id, String fileWithIdAlreadyPresent, String fieldNameInFile, int csvRowNumber) {

      this.id = id;
      this.fileWithIdAlreadyPresent = fileWithIdAlreadyPresent;
      this.fieldNameInFile = fieldNameInFile;
      this.csvRowNumber = csvRowNumber;
    }
  }
}
