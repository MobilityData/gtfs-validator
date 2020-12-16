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
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader;

/**
 * Checks that agency_id field in "routes.txt" is defined for every row if there is more than 1 agency in the feed.
 * <p>
 * Generated notices:
 * * MissingRequiredFieldError
 */
@GtfsValidator
public class RouteAgencyIdValidator extends FileValidator {
    @Inject
    GtfsAgencyTableContainer agencyTable;

    @Inject
    GtfsRouteTableContainer routeTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        if (agencyTable.entityCount() < 2) {
            // routes.agency_id is not required when there is a single agency.
            return;
        }
        for (GtfsRoute route : routeTable.getEntities()) {
            if (!route.hasAgencyId()) {
                noticeContainer.addNotice(
                        new MissingRequiredFieldError(
                                routeTable.gtfsFilename(),
                                route.csvRowNumber(),
                                GtfsRouteTableLoader.AGENCY_ID_FIELD_NAME));
            }
            // No need to check reference integrity because it is done by a validator generated from @ForeignKey
            // annotation.
        }
    }
}
