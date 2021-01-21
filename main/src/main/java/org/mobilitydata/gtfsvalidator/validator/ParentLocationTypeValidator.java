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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.WrongParentLocationTypeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates `location_type` of the referenced `parent_station`.
 * <p>
 * Generated notice: {@link WrongParentLocationTypeNotice}.
 */
@GtfsValidator
public class ParentLocationTypeValidator extends FileValidator {
    @Inject
    GtfsStopTableContainer stopTable;

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
            GtfsStop parentLocation = stopTable.byStopId(location.parentStation());
            GtfsLocationType expected = expectedParentLocationType(location.locationType());
            if (expected != GtfsLocationType.UNRECOGNIZED && parentLocation.locationType() != expected) {
                noticeContainer.addValidationNotice(
                    new WrongParentLocationTypeNotice(
                        location.csvRowNumber(), location.stopId(),
                        location.stopName(), location.locationTypeValue(),
                        parentLocation.csvRowNumber(), location.parentStation(),
                        parentLocation.stopName(),
                        parentLocation.locationTypeValue(),
                        expected.getNumber()));
            }
        }
    }
}
