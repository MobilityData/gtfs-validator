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

import java.util.HashMap;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteNameNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates unicity of short and long name for all routes.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link DuplicateRouteNameNotice}
 * </ul>
 */
@GtfsValidator
public class DuplicateRouteNameValidator extends FileValidator {
  @Inject GtfsRouteTableContainer routeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<String, GtfsRoute> routeByRouteLongName = new HashMap<>();
    final Map<String, GtfsRoute> routeByShortName = new HashMap<>();
    routeTable
        .getEntities()
        .forEach(
            route -> {
              if (route.hasRouteLongName()) {
                if (routeByRouteLongName.containsKey(route.routeLongName())) {
                  if (areRouteFromSameAgency(
                      route.agencyId(),
                      routeByRouteLongName.get(route.routeLongName()).agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_long_name", route.csvRowNumber(), route.routeId()));
                  }
                } else {
                  routeByRouteLongName.put(route.routeLongName(), route);
                }
              }
              if (route.hasRouteShortName()) {
                if (routeByShortName.containsKey(route.routeShortName())) {
                  if (areRouteFromSameAgency(
                      route.agencyId(), routeByShortName.get(route.routeShortName()).agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_short_name", route.csvRowNumber(), route.routeId()));
                  }
                } else {
                  routeByShortName.put(route.routeShortName(), route);
                }
              }
            });
  }

  /**
   * Determines if two routes are from the same agency
   *
   * @param routeAgencyId first agency_id
   * @param otherRouteAgencyId second agency_id
   * @return true if both agency ids are equals returns false otherwise.
   */
  private boolean areRouteFromSameAgency(
      final String routeAgencyId, final String otherRouteAgencyId) {
    return routeAgencyId.equals(otherRouteAgencyId);
  }
}
