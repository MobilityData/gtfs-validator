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
import java.util.Objects;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteNameNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates unicity of short and long name for all routes.
 *
 * <p>When a {@code GtfsRoute} short and/or long names are found to be duplicate a {@code
 * DuplicateRouteNameNotice} is generated and added to the {@code NoticeContainer} except if routes
 * are from the same agency (values for `route.agency_id` are case-sensitive) or routes have
 * different `routes.route_type`.
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
    final Map<Integer, GtfsRoute> routeByLongName = new HashMap<>(routeTable.entityCount());
    final Map<Integer, GtfsRoute> routeByShortName = new HashMap<>(routeTable.entityCount());
    final Map<Integer, GtfsRoute> routeByShortAndLongName = new HashMap<>(routeTable.entityCount());
    routeTable
        .getEntities()
        .forEach(
            route -> {
              if (route.hasRouteShortName() && route.hasRouteLongName()) {
                if (routeByShortAndLongName.containsKey(getShortAndLongNameRouteTypeHash(route))) {
                  noticeContainer.addValidationNotice(
                      new DuplicateRouteNameNotice(
                          "route_short_name and route_long_name",
                          route.csvRowNumber(),
                          route.routeId()));
                  return;
                } else {
                  routeByShortAndLongName.put(getShortAndLongNameRouteTypeHash(route), route);
                }
              }
              if (route.hasRouteLongName()) {
                if (routeByLongName.containsKey(getRouteLongNameRouteTypeHash(route))) {
                  if (areRoutesFromSameAgency(
                      route.agencyId(),
                      routeByLongName.get(getRouteLongNameRouteTypeHash(route)).agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_long_name", route.csvRowNumber(), route.routeId()));
                  }
                  return;
                } else {
                  routeByLongName.put(getRouteLongNameRouteTypeHash(route), route);
                }
              }
              if (route.hasRouteShortName()) {
                if (routeByShortName.containsKey(getRouteShortNameRouteTypeHash(route))) {
                  if (areRoutesFromSameAgency(
                      route.agencyId(),
                      routeByShortName.get(getRouteShortNameRouteTypeHash(route)).agencyId())) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_short_name", route.csvRowNumber(), route.routeId()));
                  }
                } else {
                  routeByShortName.put(getRouteShortNameRouteTypeHash(route), route);
                }
              }
            });
  }

  /**
   * Determines if two routes are from the same agency: ids are case-sensitive.
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
   * Generate an hash associated to `routes.route_long_name` and `routes.route_type`. This hash is
   * used to interact with routeByLongName (variable defined in this class' validate method) to
   * store and retrieve routes by short name.
   *
   * @param route the {@code GtfsRoute} to generate the hash from
   * @return the hash associated to `routes.route_long_name` and `routes.route_type`.
   */
  private int getRouteLongNameRouteTypeHash(GtfsRoute route) {
    return Objects.hash(route.routeLongName(), route.routeType());
  }

  /**
   * Generate an hash associated to `routes.route_short_name` and `routes.route_type`. This hash is
   * used to interact with routeByShortName (variable defined in this class' validate method) to
   * store and retrieve routes by short name.
   *
   * @param route the {@code GtfsRoute} to generate the hash from
   * @return the hash associated to `routes.route_short_name` and `routes.route_type`.
   */
  private int getRouteShortNameRouteTypeHash(GtfsRoute route) {
    return Objects.hash(route.routeShortName(), route.routeType());
  }

  /**
   * Generate an hash associated to `routes.route_long_name`, `routes.route_short_name` and
   * `routes.route_type`. This hash is used to interact with routeByShortAndLongName (variable
   * defined in this class' validate method) to store and retrieve routes by short and long name.
   *
   * @param route the {@code GtfsRoute} to generate the hash from
   * @return the hash associated to `routes.route_long_name`, `routes.route_short_name` and
   *     `routes.route_type`.
   */
  private int getShortAndLongNameRouteTypeHash(GtfsRoute route) {
    return Objects.hash(route.routeShortName(), route.routeLongName(), route.routeType());
  }
}
