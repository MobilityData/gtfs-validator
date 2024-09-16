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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Checks that agency_id field in "routes.txt" is defined for every row if there is more than 1
 * agency in the feed, recommended if only 1 agency.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 *
 * <p>Generated notice: {@link MissingRecommendedFieldNotice}.
 */
@GtfsValidator
public class RouteAgencyIdValidator extends FileValidator {
  private final GtfsAgencyTableContainer agencyTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  RouteAgencyIdValidator(GtfsAgencyTableContainer agencyTable, GtfsRouteTableContainer routeTable) {
    this.agencyTable = agencyTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // routes.agency_id is required when there are multiple agencies
    int totalAgencies = agencyTable.entityCount();

    for (GtfsRoute route : routeTable.getEntities()) {
      if (!route.hasAgencyId()) {
        if (totalAgencies > 1) {
          // add error notice if more than one agency
          noticeContainer.addValidationNotice(
              new MissingRequiredFieldNotice(
                  routeTable.gtfsFilename(), route.csvRowNumber(), GtfsRoute.AGENCY_ID_FIELD_NAME));
        } else {
          // add warning notice if only one agency
          noticeContainer.addValidationNotice(
              new MissingRecommendedFieldNotice(
                  routeTable.gtfsFilename(), route.csvRowNumber(), GtfsRoute.AGENCY_ID_FIELD_NAME));
        }
      }
    }
    // No need to check reference integrity because it is done by a validator generated from
    // @ForeignKey annotation.
  }
}
