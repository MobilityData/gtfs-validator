/*
 * Copyright 2021 MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * If {@code fare_rules.txt} is provided, checks that all stops and platforms (location_type = 0)
 * have {@code stops.zone_id} defined if the stop is defined as part of a {@code trip_id} in {@code
 * stop_times.txt} whose {@code route_id} defines an {@code origin_id}, {@code destination_id}, or
 * {@code contains_id} in {@code fare_rules.txt}.
 *
 * <p>Generated notice: {@link StopWithoutZoneIdNotice}.
 */
@GtfsValidator
public class StopZoneIdValidator extends FileValidator {

  private final GtfsStopTableContainer stopTable;
  private final GtfsFareRuleTableContainer fareRuleTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsTripTableContainer tripTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  StopZoneIdValidator(
      GtfsStopTableContainer stopTable,
      GtfsFareRuleTableContainer fareRuleTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsTripTableContainer tripTable,
      GtfsRouteTableContainer routeTable) {
    this.stopTable = stopTable;
    this.fareRuleTable = fareRuleTable;
    this.stopTimeTable = stopTimeTable;
    this.tripTable = tripTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (fareRuleTable.getEntities().isEmpty()) {
      return;
    }
    if (!hasFareZoneStructure(fareRuleTable)) {
      return;
    }

    Multimap<String, GtfsFareRule> routesWithZoneFieldsDefined =
        Multimaps.filterValues(
            fareRuleTable.byRouteIdMap(),
            fareRule ->
                fareRule.hasOriginId() || fareRule.hasDestinationId() || fareRule.hasContainsId());
    for (GtfsStop stop : stopTable.getEntities()) {
      if (!stop.locationType().equals(GtfsLocationType.STOP)) {
        continue;
      }
      if (stop.hasZoneId()) {
        continue;
      }

      // check that a stop without zone_id does not have a route_id in a fare_rule with
      // zone-dependent fields
      for (GtfsRoute route : getRoutesIncludingStop(stop)) {
        if (routesWithZoneFieldsDefined.containsKey(route.routeId())) {
          noticeContainer.addValidationNotice(new StopWithoutZoneIdNotice(stop));
          break;
        }
      }
    }
  }

  /**
   * Checks if the {@code GtfsFareRuleTableContainer} provided as parameter has a fare structure
   * that uses zones.
   *
   * @param fareRuleTable the {@code GtfsFareRuleTableContainer} to be checked
   * @return true if the {@code GtfsFareRuleTableContainer} provided as parameter has a fare
   *     structure that uses zones; false otherwise.
   */
  private static boolean hasFareZoneStructure(GtfsFareRuleTableContainer fareRuleTable) {
    for (GtfsFareRule fareRule : fareRuleTable.getEntities()) {
      if (fareRule.hasContainsId() || fareRule.hasDestinationId() || fareRule.hasOriginId()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets a deduplicated set of all trips which contain a stop. A trip "contains" a stop if an entry
   * in {@code stop_times.txt} defines the stop and the trip.
   *
   * @param stop the {@code GtfsStop} for which to get containing {@code GtfsTrip}s
   * @return a {@code Set} of {@code GtfsTrip}s containing {@code stop}
   */
  private Set<GtfsTrip> getTripsIncludingStop(GtfsStop stop) {
    Set<GtfsTrip> trips = new HashSet<>();
    for (GtfsStopTime stopTime : stopTimeTable.byStopId(stop.stopId())) {
      tripTable.byTripId(stopTime.tripId()).ifPresent(trips::add);
    }
    return trips;
  }

  /**
   * Gets a deduplicated set of all routes which contain a stop. A route "contains" a stop if an
   * entry in {@code trips.txt} defines the route and a trip which contains the stop.
   *
   * @param stop the {@code GtfsStop} for which to get containing {@code GtfsRoute}s
   * @return a {@code Set} of {@code GtfsRoute}s containing {@code stop}
   */
  private Set<GtfsRoute> getRoutesIncludingStop(GtfsStop stop) {
    Set<GtfsRoute> routes = new HashSet<>();
    for (GtfsTrip trip : getTripsIncludingStop(stop)) {
      routeTable.byRouteId(trip.routeId()).ifPresent(routes::add);
    }
    return routes;
  }

  /**
   * Stop without value for `stops.zone_id` contained in a route with a zone-dependent fare rule.
   *
   * <p>If `fare_rules.txt` is provided, and `fare_rules.txt` uses at least one column among
   * `origin_id`, `destination_id`, and `contains_id`, then all stops and platforms (location_type =
   * 0) must have `stops.zone_id` assigned if they are defined in a trip defined in a route defined
   * in a fare rule which also defines at least one of `origin_id`, `destination_id`, or
   * `contains_id`.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsStopSchema.class, GtfsFareRuleSchema.class}))
  static class StopWithoutZoneIdNotice extends ValidationNotice {

    /** The faulty record's id. */
    private final String stopId;

    /** The faulty record's `stops.stop_name`. */
    private final String stopName;

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    StopWithoutZoneIdNotice(GtfsStop stop) {
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
      this.csvRowNumber = stop.csvRowNumber();
    }
  }
}
