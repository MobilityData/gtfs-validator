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

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UniqueLocationIdViolationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeature;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeaturesContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeojsonFeatureDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsNetworkTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates that the feature id from "locations.geojson" is not a duplicate of any stop_id from
 * "stops.txt" or location_group_id from "location_groups.txt"
 *
 * <p>Generated notice: {@link UniqueLocationIdViolationNotice}.
 */
@GtfsValidator
public class GtfsGeojsonFeatureUniqueLocationIdValidator extends FileValidator {
  private final GtfsStopTableContainer stopTableContainer;

  // Remove comments when the location_group_stops.txt file is added to the GTFS schema
  // private final GtfsLocationGroupStopsTableContainer  locationGroupStopsTableContainer;

  private final GtfsGeojsonFeaturesContainer<GtfsGeojsonFeature, GtfsGeojsonFeatureDescriptor<GtfsGeojsonFeature>>
          geojsonFeatureContainer;

  @Inject
  GtfsGeojsonFeatureUniqueLocationIdValidator(
      GtfsGeojsonFeaturesContainer<
                    GtfsGeojsonFeature, GtfsGeojsonFeatureDescriptor<GtfsGeojsonFeature>>
              geojsonFeatureContainer,
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
      if (stop.isPresent()) {
        noticeContainer.addValidationNotice(
            new UniqueLocationIdViolationNotice(
                locationId,
                GtfsStop.FILENAME,
                GtfsStop.STOP_ID_FIELD_NAME,
                stop.get().csvRowNumber()));
      }
    }
  }

}
