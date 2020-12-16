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
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;

/**
 * Validates short and long name for a single route.
 * <p>
 * Generated notices:
 * * RouteBothShortAndLongNameMissingNotice
 * * RouteShortAndLongNameEqualNotice
 * * RouteShortNameTooLongNotice
 * * SameNameAndDescriptionForRouteNotice
 */
@GtfsValidator
public class RouteNameValidator extends SingleEntityValidator<GtfsRoute> {
    private static final int MAX_SHORT_NAME_LENGTH = 12;

    @Override
    public void validate(GtfsRoute entity, NoticeContainer noticeContainer) {
        final boolean hasLongName = entity.hasRouteLongName();
        final boolean hasShortName = entity.hasRouteShortName();

        if (!hasLongName && !hasShortName) {
            noticeContainer.addNotice(
                    new RouteBothShortAndLongNameMissingNotice(
                            entity.routeId(),
                            entity.csvRowNumber()
                    )
            );
        }

        if (hasShortName && hasLongName &&
            entity.routeShortName().equalsIgnoreCase(entity.routeLongName())) {
            noticeContainer.addNotice(
                    new RouteShortAndLongNameEqualNotice(
                            entity.routeId(),
                            entity.csvRowNumber(),
                            entity.routeShortName(),
                            entity.routeLongName()
                    )
            );
        }

        if (hasShortName && entity.routeShortName().length() > MAX_SHORT_NAME_LENGTH) {
            noticeContainer.addNotice(
                    new RouteShortNameTooLongNotice(
                            entity.routeId(),
                            entity.csvRowNumber(),
                            entity.routeShortName()
                    )
            );
        }
        if (entity.hasRouteDesc()) {
            String routeDesc = entity.routeDesc();
            String routeId = entity.routeId();
            if (hasShortName && !isValidRouteDesc(routeDesc, entity.routeShortName())) {
                noticeContainer.addNotice(
                        new SameNameAndDescriptionForRouteNotice(
                                entity.csvRowNumber(),
                                routeId,
                                routeDesc,
                                "route_short_name")
                );
                return;
            }
            if (hasLongName && !isValidRouteDesc(routeDesc, entity.routeLongName())) {
                noticeContainer.addNotice(
                        new SameNameAndDescriptionForRouteNotice(
                                entity.csvRowNumber(),
                                routeId,
                                routeDesc,
                                "route_long_name")
                );
            }
        }
    }

    private boolean isValidRouteDesc(String routeDesc, String routeShortOrLongName) {
        // ignore lower case and upper case difference
        return !routeDesc.equalsIgnoreCase(routeShortOrLongName);
    }
}


