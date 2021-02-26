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
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteNameNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates unicity of short and long name for all routes. A {@code GtfsRoute}'s `route_short_name`
 * and `route_long_name` should be unique.
 *
 * <p>When a {@code GtfsRoute} names are found to be duplicate a {@code DuplicateRouteNameNotice} is
 * generated and added to the {@code NoticeContainer} except if routes are from the same agency
 * (values for `route.agency_id` are case-sensitive) or routes have different `routes.route_type`.
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
    final Map<String, GtfsRoute> routeByLongName = new HashMap<>(routeTable.entityCount());
    final Map<String, GtfsRoute> routeByShortName = new HashMap<>(routeTable.entityCount());
    final Map<String, GtfsRoute> routeByShortAndLongName = new HashMap<>(routeTable.entityCount());
    routeTable
        .getEntities()
        .forEach(
            route -> {
              if (route.hasRouteShortName() && route.hasRouteLongName()) {
                if (routeByShortAndLongName.containsKey(getShortAndLongNameKey(route))) {
                  noticeContainer.addValidationNotice(
                      new DuplicateRouteNameNotice(
                          "route_short_name and route_long_name",
                          route.csvRowNumber(),
                          route.routeId()));
                  return;
                } else {
                  routeByShortAndLongName.put(getShortAndLongNameKey(route), route);
                }
              }
              if (route.hasRouteLongName()) {
                if (routeByLongName.containsKey(getRouteLongNameKey(route))) {
                  if (areRoutesFromSameAgency(
                      route.agencyId(),
                      routeByLongName.get(route.routeLongName() + route.routeType()).agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_long_name", route.csvRowNumber(), route.routeId()));
                  }
                  return;
                } else {
                  routeByLongName.put(getRouteLongNameKey(route), route);
                }
              }
              if (route.hasRouteShortName()) {
                if (routeByShortName.containsKey(getRouteShortNameKey(route))) {
                  if (areRoutesFromSameAgency(
                      route.agencyId(),
                      routeByShortName
                          .get(route.routeShortName() + route.routeType())
                          .agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_short_name", route.csvRowNumber(), route.routeId()));
                  }
                } else {
                  routeByShortName.put(getRouteShortNameKey(route), route);
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
  private boolean areRoutesFromSameAgency(
      final String routeAgencyId, final String otherRouteAgencyId) {
    return routeAgencyId.equals(otherRouteAgencyId);
  }

  /**
   * Generate a key used to store {@code GtfsRoute} by `routes.route_long_name`
   *
   * @param route the {@code GtfsRoute} to generate the key from
   * @return `routes.route_long_name`+`route.routeType`
   */
  private String getRouteLongNameKey(GtfsRoute route) {
    return route.routeLongName() + route.routeType();
  }

  /**
   * Generate a key used to store {@code GtfsRoute} by `routes.route_short_name`
   *
   * @param route the {@code GtfsRoute} to generate the key from
   * @return `routes.route_short_name`+`route.routeType`
   */
  private String getRouteShortNameKey(GtfsRoute route) {
    return route.routeShortName() + route.routeType();
  }

  /**
   * Generate a key used to store {@code GtfsRoute} by both `routes.route_short_name` and
   * `routes.route_long_name`
   *
   * @param route the {@code GtfsRoute} to generate the key from
   * @return `routes.route_short_name`+`routes.route_long_name`+`route.routeType`
   */
  private String getShortAndLongNameKey(GtfsRoute route) {
    return route.routeShortName() + route.routeLongName() + route.routeType();
  }
}
