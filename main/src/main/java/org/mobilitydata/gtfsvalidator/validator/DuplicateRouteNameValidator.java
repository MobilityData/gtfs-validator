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

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates unicity of short and long name for all routes.
 *
 * <p>When a {@code GtfsRoute} short and/or long names are found to be duplicate a {@code
 * DuplicateRouteNameNotice} is generated and added to the {@code NoticeContainer} except if routes
 * are from the same agency (values for "route.agency_id" are case-sensitive) or routes have
 * different "routes.route_type".
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link DuplicateRouteNameNotice}
 * </ul>
 */
@GtfsValidator
public class DuplicateRouteNameValidator extends FileValidator {
  private final GtfsRouteTableContainer routeTable;

  @Inject
  DuplicateRouteNameValidator(GtfsRouteTableContainer routeTable) {
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Integer, GtfsRoute> routeByLongName = new HashMap<>(routeTable.entityCount());
    final Map<Integer, GtfsRoute> routeByShortName = new HashMap<>(routeTable.entityCount());
    routeTable
        .getEntities()
        .forEach(
            route -> {
              GtfsRoute otherRoute;
              if (route.hasRouteLongName()) {
                otherRoute = routeByLongName.putIfAbsent(getLongNameAndTypeHash(route), route);
                if (otherRoute != null) {
                  if (areRoutesFromSameAgency(route, otherRoute)) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_long_name", route.csvRowNumber(), route.routeId()));
                  }
                }
              }
              if (route.hasRouteShortName()) {
                otherRoute = routeByShortName.putIfAbsent(getShortNameAndTypeHash(route), route);
                if (otherRoute != null) {
                  if (areRoutesFromSameAgency(route, otherRoute)) {
                    noticeContainer.addValidationNotice(
                        new DuplicateRouteNameNotice(
                            "route_short_name", route.csvRowNumber(), route.routeId()));
                  }
                }
              }
            });
  }

  /**
   * Determines if two routes are from the same agency: ids are case-sensitive.
   *
   * @param route first {@code GtfsRoute}
   * @param otherRoute second {@code GtfsRoute}
   * @return true if both agency ids are equals returns false otherwise.
   */
  private boolean areRoutesFromSameAgency(final GtfsRoute route, final GtfsRoute otherRoute) {
    return route.agencyId().equals(otherRoute.agencyId());
  }

  /**
   * Generate an hash associated to "routes.route_long_name" and "routes.route_type". This hash is
   * used to interact with routeByLongName (variable defined in this class' validate method) to
   * store and retrieve routes by short name.
   *
   * @param route the {@code GtfsRoute} to generate the hash from
   * @return the hash associated to "routes.route_long_name" and "routes.route_type".
   */
  private int getLongNameAndTypeHash(GtfsRoute route) {
    return Objects.hash(route.routeLongName(), route.routeType());
  }

  /**
   * Generate an hash associated to "routes.route_short_name" and "routes.route_type". This hash is
   * used to interact with routeByShortName (variable defined in this class' validate method) to
   * store and retrieve routes by short name.
   *
   * @param route the {@code GtfsRoute} to generate the hash from
   * @return the hash associated to "routes.route_short_name" and "routes.route_type".
   */
  private int getShortNameAndTypeHash(GtfsRoute route) {
    return Objects.hash(route.routeShortName(), route.routeType());
  }

  /**
   * All routes should have different `routes.route_long_name`. All routes should have different
   * `routes.route_short_name`.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class DuplicateRouteNameNotice extends ValidationNotice {
    DuplicateRouteNameNotice(String duplicatedField, long csvRowNumber, String routeId) {
      super(
          ImmutableMap.of(
              "duplicatedField", duplicatedField, "csvRowNumber", csvRowNumber, "routeId", routeId),
          SeverityLevel.WARNING);
    }
  }
}
