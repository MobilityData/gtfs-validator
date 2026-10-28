/*
 * Copyright 2026 Qian Jiang
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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableDescriptor;
import org.mobilitydata.gtfsvalidator.table.TableStatus;
import org.mobilitydata.gtfsvalidator.validator.StopServesConflictingModesValidator.StopServesConflictingModesNotice;

public class StopServesConflictingModesValidatorTest {
  private static final int[] MODE_NUMBERS = {0, 1, 2, 3, 4, 5, 6, 7, 11, 12};

  // Allowed distinct pairs agreed in MobilityData/gtfs-validator#2215.
  private static final Set<String> ALLOWED_PAIRS =
      Set.of(
          "0,1", "0,2", "0,3", "0,5", "0,11", "1,2", "1,5", "1,12", "2,5", "2,12", "3,5", "3,11",
          "5,11");

  @Test
  public void validatesEveryOrderedModePair() {
    for (int first : MODE_NUMBERS) {
      for (int second : MODE_NUMBERS) {
        Feed feed = new Feed();
        feed.addService("S", "route-z", first);
        feed.addService("S", "route-a", second);
        boolean allowed =
            first == second
                || ALLOWED_PAIRS.contains(Math.min(first, second) + "," + Math.max(first, second));

        assertWithMessage("route types %s and %s", first, second)
            .that(feed.tables().validate())
            .hasSize(allowed ? 0 : 1);
      }
    }
  }

  @Test
  public void monorailCanShareWithBothRailAndSubway() {
    Feed feed = new Feed();
    feed.addService("S", "monorail", 12);
    feed.addService("S", "rail", 2);
    feed.addService("S", "subway", 1);

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void tramDoesNotHideABusRailConflict() {
    Feed feed = new Feed();
    feed.addService("S", "tram", 0);
    feed.addService("S", "bus", 3);
    feed.addService("S", "rail", 2);

    assertThat(feed.tables().validate())
        .containsExactly(
            new StopServesConflictingModesNotice("S", "bus, rail, tram", "LIGHT_RAIL, RAIL, BUS"));
  }

  @Test
  public void emitsOneNoticeWithAllRoutesAndDistinctModesInStableOrder() {
    Feed feed = new Feed();
    for (int i = MODE_NUMBERS.length - 1; i >= 0; i--) {
      feed.addService("S", "route-" + MODE_NUMBERS[i], MODE_NUMBERS[i]);
    }
    feed.addService("S", "route-extra", 3);
    feed.stopTimes.add(stopTime("S", "trip-route-3", 20));

    assertThat(feed.tables().validate())
        .containsExactly(
            new StopServesConflictingModesNotice(
                "S",
                "route-0, route-1, route-11, route-12, route-2, route-3, route-4, route-5, route-6,"
                    + " route-7, route-extra",
                "LIGHT_RAIL, SUBWAY, RAIL, BUS, FERRY, CABLE_TRAM, AERIAL_LIFT, FUNICULAR,"
                    + " TROLLEYBUS, MONORAIL"));
  }

  @Test
  public void reportsEachConflictingStopOnce() {
    Feed feed = new Feed();
    feed.stops.add(stop("other"));
    feed.addService("S", "bus", 3);
    feed.addService("S", "rail", 2);
    feed.addService("other", "ferry", 4);
    feed.addService("other", "tram", 0);

    assertThat(feed.tables().validate())
        .containsExactly(
            new StopServesConflictingModesNotice("S", "bus, rail", "RAIL, BUS"),
            new StopServesConflictingModesNotice("other", "ferry, tram", "LIGHT_RAIL, FERRY"));
  }

  @Test
  public void doesNotCombineSiblingStopsThroughTheirParentStation() {
    Feed feed = new Feed();
    feed.stops.clear();
    feed.stops.add(
        new GtfsStop.Builder()
            .setStopId("station")
            .setLocationType(GtfsLocationType.STATION)
            .build());
    feed.stops.add(
        new GtfsStop.Builder().setStopId("bus-stop").setParentStation("station").build());
    feed.stops.add(
        new GtfsStop.Builder().setStopId("rail-stop").setParentStation("station").build());
    feed.addService("bus-stop", "bus", 3);
    feed.addService("rail-stop", "rail", 2);

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void skipsLocationsThatAreNotStops() {
    for (GtfsLocationType type : GtfsLocationType.values()) {
      if (type == GtfsLocationType.STOP) {
        continue;
      }
      Feed feed = new Feed();
      feed.stops.set(0, new GtfsStop.Builder().setStopId("S").setLocationType(type).build());
      feed.addService("S", "bus", 3);
      feed.addService("S", "rail", 2);

      assertWithMessage("location_type %s", type).that(feed.tables().validate()).isEmpty();
    }
  }

  @Test
  public void omittedLocationTypeDefaultsToStop() {
    Feed feed = conflictingFeed();

    assertThat(feed.tables().validate())
        .containsExactly(new StopServesConflictingModesNotice("S", "bus, rail", "RAIL, BUS"));
  }

  @Test
  public void skipsBlankStopIds() {
    Feed feed = new Feed();
    feed.stops.set(0, stop(""));
    feed.addService("", "bus", 3);
    feed.addService("", "rail", 2);

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void ignoresUnusedRoutesAndStopsWithoutService() {
    Feed feed = new Feed();
    feed.stops.add(stop("unused"));
    feed.addService("S", "bus", 3);
    feed.routes.add(route("unused-rail", 2));

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void skipsUnknownStopAndTripReferences() {
    Feed feed = new Feed();
    feed.addService("S", "bus", 3);
    feed.addService("missing-stop", "rail", 2);
    feed.stopTimes.add(stopTime("S", "missing-trip", 3));
    feed.stopTimes.add(stopTime("S", "", 4));
    feed.stopTimes.add(new GtfsStopTime.Builder().setStopId("S").setStopSequence(5).build());

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void skipsMissingAndBlankRouteReferences() {
    Feed feed = new Feed();
    feed.addService("S", "bus", 3);
    feed.trips.add(trip("missing-route-trip", "missing-route"));
    feed.trips.add(trip("blank-route-trip", ""));
    feed.trips.add(new GtfsTrip.Builder().setTripId("no-route-trip").build());
    feed.stopTimes.add(stopTime("S", "missing-route-trip", 2));
    feed.stopTimes.add(stopTime("S", "blank-route-trip", 3));
    feed.stopTimes.add(stopTime("S", "no-route-trip", 4));

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void missingRouteTypeIsNotTreatedAsTram() {
    Feed feed = new Feed();
    feed.addService("S", "monorail", 12);
    feed.routes.add(new GtfsRoute.Builder().setRouteId("no-type").build());
    feed.trips.add(trip("no-type-trip", "no-type"));
    feed.stopTimes.add(stopTime("S", "no-type-trip", 2));

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void skipsUnrecognizedRouteTypes() {
    Feed feed = new Feed();
    feed.addService("S", "monorail", 12);
    feed.routes.add(
        new GtfsRoute.Builder()
            .setRouteId("unknown")
            .setRouteType(GtfsRouteType.UNRECOGNIZED)
            .build());
    feed.trips.add(trip("unknown-trip", "unknown"));
    feed.stopTimes.add(stopTime("S", "unknown-trip", 2));

    assertThat(feed.tables().validate()).isEmpty();
  }

  @Test
  public void invalidReferencesDoNotSuppressAValidConflict() {
    Feed feed = conflictingFeed();
    feed.stopTimes.add(stopTime("S", "missing-trip", 3));
    feed.routes.add(new GtfsRoute.Builder().setRouteId("no-type").build());
    feed.trips.add(trip("no-type-trip", "no-type"));
    feed.stopTimes.add(stopTime("S", "no-type-trip", 4));

    assertThat(feed.tables().validate())
        .containsExactly(new StopServesConflictingModesNotice("S", "bus, rail", "RAIL, BUS"));
  }

  @Test
  public void acceptsEmptyTablesWithTheRequiredHeaders() {
    Feed feed = new Feed();
    feed.stops.clear();
    Tables tables = feed.tables();

    assertThat(tables.validator().shouldCallValidate()).isTrue();
    assertThat(tables.validate()).isEmpty();
  }

  @Test
  public void skipsAbsentDependencies() {
    Tables tables = conflictingFeed().tables();

    assertSkipped(new Tables(null, tables.stopTimes, tables.trips, tables.routes));
    assertSkipped(new Tables(tables.stops, null, tables.trips, tables.routes));
    assertSkipped(new Tables(tables.stops, tables.stopTimes, null, tables.routes));
    assertSkipped(new Tables(tables.stops, tables.stopTimes, tables.trips, null));
  }

  @Test
  public void skipsMissingEmptyAndUnparsableFiles() {
    Tables tables = conflictingFeed().tables();
    for (TableStatus status : TableStatus.values()) {
      if (status == TableStatus.PARSABLE_HEADERS_AND_ROWS) {
        continue;
      }
      assertSkipped(
          new Tables(
              GtfsStopTableContainer.forStatus(status),
              tables.stopTimes,
              tables.trips,
              tables.routes));
      assertSkipped(
          new Tables(
              tables.stops,
              GtfsStopTimeTableContainer.forStatus(status),
              tables.trips,
              tables.routes));
      assertSkipped(
          new Tables(
              tables.stops,
              tables.stopTimes,
              GtfsTripTableContainer.forStatus(status),
              tables.routes));
      assertSkipped(
          new Tables(
              tables.stops,
              tables.stopTimes,
              tables.trips,
              GtfsRouteTableContainer.forStatus(status)));
    }
  }

  @Test
  public void requiresEachJoinColumn() {
    Feed feed = conflictingFeed();
    for (String[] field :
        new String[][] {
          {"stops", "stop_id"},
          {"stop_times", "stop_id"},
          {"stop_times", "trip_id"},
          {"trips", "trip_id"},
          {"trips", "route_id"},
          {"routes", "route_id"},
          {"routes", "route_type"}
        }) {
      assertSkipped(feed.tablesWithoutColumn(field[0], field[1]));
    }
  }

  @Test
  public void repeatedRouteReferencesKeepOneDeterministicNotice() {
    Feed feed = conflictingFeed();
    for (int i = 0; i < 2000; i++) {
      feed.stopTimes.add(stopTime("S", "trip-bus", i + 2));
      feed.stopTimes.add(stopTime("S", "trip-rail", i + 2));
    }
    Tables tables = feed.tables();
    assertThat(tables.validate())
        .containsExactly(new StopServesConflictingModesNotice("S", "bus, rail", "RAIL, BUS"));
  }

  private static void assertSkipped(Tables tables) {
    assertThat(tables.validator().shouldCallValidate()).isFalse();
    assertThat(tables.validate()).isEmpty();
  }

  private static Feed conflictingFeed() {
    Feed feed = new Feed();
    feed.addService("S", "bus", 3);
    feed.addService("S", "rail", 2);
    return feed;
  }

  private static GtfsStop stop(String id) {
    return new GtfsStop.Builder().setStopId(id).build();
  }

  private static GtfsRoute route(String id, int type) {
    return new GtfsRoute.Builder()
        .setRouteId(id)
        .setRouteType(GtfsRouteType.forNumber(type))
        .build();
  }

  private static GtfsTrip trip(String id, String routeId) {
    return new GtfsTrip.Builder().setTripId(id).setRouteId(routeId).setServiceId("service").build();
  }

  private static GtfsStopTime stopTime(String stopId, String tripId, int sequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(sequence + 1)
        .setStopId(stopId)
        .setTripId(tripId)
        .setStopSequence(sequence)
        .build();
  }

  private record Tables(
      GtfsStopTableContainer stops,
      GtfsStopTimeTableContainer stopTimes,
      GtfsTripTableContainer trips,
      GtfsRouteTableContainer routes) {
    StopServesConflictingModesValidator validator() {
      return new StopServesConflictingModesValidator(stops, stopTimes, trips, routes);
    }

    List<ValidationNotice> validate() {
      NoticeContainer notices = new NoticeContainer();
      validator().validate(notices);
      return notices.getValidationNotices();
    }
  }

  private static final class Feed {
    final List<GtfsStop> stops = new ArrayList<>(List.of(stop("S")));
    final List<GtfsStopTime> stopTimes = new ArrayList<>();
    final List<GtfsTrip> trips = new ArrayList<>();
    final List<GtfsRoute> routes = new ArrayList<>();

    void addService(String stopId, String routeId, int mode) {
      routes.add(route(routeId, mode));
      trips.add(trip("trip-" + routeId, routeId));
      stopTimes.add(stopTime(stopId, "trip-" + routeId, 1));
    }

    Tables tables() {
      return tablesWithoutColumn("", "");
    }

    Tables tablesWithoutColumn(String table, String column) {
      NoticeContainer parsingNotices = new NoticeContainer();
      return new Tables(
          GtfsStopTableContainer.forHeaderAndEntities(
              new GtfsStopTableDescriptor(),
              header("stops", table, column, "stop_id", "location_type", "parent_station"),
              stops,
              parsingNotices),
          GtfsStopTimeTableContainer.forHeaderAndEntities(
              new GtfsStopTimeTableDescriptor(),
              header("stop_times", table, column, "stop_id", "trip_id", "stop_sequence"),
              stopTimes,
              parsingNotices),
          GtfsTripTableContainer.forHeaderAndEntities(
              new GtfsTripTableDescriptor(),
              header("trips", table, column, "trip_id", "route_id", "service_id"),
              trips,
              parsingNotices),
          GtfsRouteTableContainer.forHeaderAndEntities(
              new GtfsRouteTableDescriptor(),
              header("routes", table, column, "route_id", "route_type"),
              routes,
              parsingNotices));
    }

    private static CsvHeader header(
        String table, String omittedTable, String omittedColumn, String... fields) {
      return new CsvHeader(
          Arrays.stream(fields)
              .filter(field -> !table.equals(omittedTable) || !field.equals(omittedColumn))
              .toArray(String[]::new));
    }
  }
}
