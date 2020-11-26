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
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that service_id field in "trips.txt" references a valid service_id in "calendar.txt" or "calendar_date.txt".
 *
 * Generated notices:
 * * ForeignKeyError
 */
@GtfsValidator
public class GtfsTripServiceIdForeignKeyValidator extends FileValidator {
    @Inject
    GtfsTripTableContainer tripContainer;
    @Inject
    GtfsCalendarTableContainer calendarContainer;
    @Inject
    GtfsCalendarDateTableContainer calendarDateContainer;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        for (GtfsTrip trip : tripContainer.getEntities()) {
            String childKey = trip.serviceId();
            if (!hasReferencedKey(childKey, calendarContainer, calendarDateContainer)) {
                noticeContainer.addNotice(new ForeignKeyError(GtfsCalendarDateTableLoader.FILENAME,
                        GtfsCalendarDateTableLoader.SERVICE_ID_FIELD_NAME,
                        GtfsCalendarTableLoader.FILENAME + " or " + GtfsCalendarDateTableLoader.FILENAME,
                        GtfsCalendarTableLoader.SERVICE_ID_FIELD_NAME,
                        childKey, trip.csvRowNumber()));
            }
        }

    }

    private boolean hasReferencedKey(String childKey,
                                     GtfsCalendarTableContainer calendarContainer,
                                     GtfsCalendarDateTableContainer calendarDateContainer) {
        return calendarContainer.byServiceId(childKey) != null
                || !calendarDateContainer.byServiceId(childKey).isEmpty();
    }

}

