/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SameNameAndDescriptionForRouteNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

/**
 * Validates that a Route description is different than the Route names (short name and/or long name) for a single
 * entity.
 * Notice generated:
 *  * @code{SameNameAndDescriptionForRouteNotice}
 */
@GtfsValidator
public class DifferentRouteNameAndDescriptionValidator extends SingleEntityValidator<GtfsRoute> {

    @Override
    public void validate(GtfsRoute route, NoticeContainer noticeContainer) {
        if (route.hasRouteDesc()) {
            String routeDesc = route.routeDesc();
            String routeId = route.routeId();
            if (route.hasRouteShortName() && !isValidRouteDesc(routeDesc, route.routeShortName())) {
                noticeContainer.addNotice(
                        new SameNameAndDescriptionForRouteNotice(
                                route.csvRowNumber(),
                                routeId,
                                routeDesc,
                                "route_short_name")
                );
                return;
            }
            if (route.hasRouteLongName() && !isValidRouteDesc(routeDesc, route.routeLongName())) {
                noticeContainer.addNotice(
                        new SameNameAndDescriptionForRouteNotice(
                                route.csvRowNumber(),
                                routeId,
                                routeDesc,
                                "route_long_name")
                );
            }
        }
    }

    private boolean isValidRouteDesc(String routeDesc, String routeShorOrLongName) {
        return !routeDesc.equals(routeShorOrLongName);
    }
}
