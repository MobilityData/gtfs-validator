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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.validator.StopZoneIdValidator.StopWithoutZoneIdNotice;

@RunWith(JUnit4.class)
public class StopZoneIdValidatorTest {

  private static GtfsStop createStop(
      int csvRowNumber, GtfsLocationType locationType, String zoneId) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(toLocationId(locationType, csvRowNumber))
        .setLocationType(locationType)
        .setZoneId(zoneId)
        .build();
  }

  /**
   * Generates a fare rule
   *
   * @param csvRowNumber row of rule in fare_rules.txt and disambiguating index for cell values
   * @param route whether to define a route_id in the fare rule
   * @param origin whether to define an origin_id in the fare rule
   * @param destination whether to define a destination_id in the fare rule
   * @param contains whether to define a contains_id in the fare rule
   * @return a `GtfsFareRule` with fare_rule_id and the indicated fields defined
   */
  private static GtfsFareRule createFareRule(
      int csvRowNumber, boolean route, boolean origin, boolean destination, boolean contains) {
    return new GtfsFareRule.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFareId(toFareRuleId(csvRowNumber))
        .setRouteId(route ? toRouteId(csvRowNumber) : null)
        .setOriginId(origin ? toZoneId(csvRowNumber) : null)
        .setDestinationId(destination ? toZoneId(csvRowNumber) : null)
        .setContainsId(contains ? toZoneId(csvRowNumber) : null)
        .build();
  }

  /**
   * Generates a zone-based fare rule
   *
   * @param csvRowNumber row of rule in fare_rules.txt and disambiguating index for cell values
   * @return a `GtfsFareRule` with only fare_rule_id and origin_id defined
   */
  private static GtfsFareRule createFareRuleWithZoneStructure(int csvRowNumber) {
    return createFareRule(csvRowNumber, false, true, false, false);
  }

  /**
   * Generates a route-based fare rule
   *
   * @param csvRowNumber row of rule in fare_rules.txt and disambiguating index for cell values
   * @return a `GtfsFareRule` with only fare_rule_id and route_id defined
   */
  private static GtfsFareRule createFareRuleWithoutZoneStructure(int csvRowNumber) {
    return createFareRule(csvRowNumber, true, false, false, false);
  }

  /**
   * Generates string id. Can maintain consistency across tables in tests when accurate rows and
   * field names are passed.
   *
   * @param item string description of the object; should be the name of the field, but can be
   *     arbitrary
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toId(String item, int csvRowNumber) {
    return String.format("%s id %d", item, csvRowNumber);
  }

  /**
   * Shortcut for toId for generating string id's for `GtfsLocationType`s
   *
   * @param locationType the `GtfsLocationType` to id
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toLocationId(GtfsLocationType locationType, int csvRowNumber) {
    return locationType.toString() + csvRowNumber;
  }

  /**
   * Shortcut for toId for generating string id's for `GtfsFareRule`s
   *
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toFareRuleId(int csvRowNumber) {
    return toId("fare_rule", csvRowNumber);
  }

  /**
   * Shortcut for toId for generating string id's for zones
   *
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toZoneId(int csvRowNumber) {
    return toId("zone", csvRowNumber);
  }

  /**
   * Shortcut for toId for generating string id's for `GtfsRoute`s
   *
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toRouteId(int csvRowNumber) {
    return toId("route", csvRowNumber);
  }

  /**
   * Shortcut for toId for generating string id's for `GtfsTrip`s
   *
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toTripId(int csvRowNumber) {
    return toId("trip", csvRowNumber);
  }

  /**
   * Shortcut for toId for generating string id's for `GtfsStop`s
   *
   * @param csvRowNumber number to disambiguate the id; should be the index of the row but can be
   *     arbitrary
   * @return a string id
   */
  private static String toStopId(int csvRowNumber) {
    return toId("stop", csvRowNumber);
  }

  /**
   * Generates StopZoneId validation notices for mock data
   *
   * @param stops a list of mock stops to test
   * @param fareRules a list of mock fare rules to test
   * @param stopTimes a list of mock stop times to test
   * @param trips a list of mock trips to test
   * @param routes a list of mock routes to test
   * @return a list of validation notices for the mock data
   */
  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops,
      List<GtfsFareRule> fareRules,
      List<GtfsStopTime> stopTimes,
      List<GtfsTrip> trips,
      List<GtfsRoute> routes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopZoneIdValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsFareRuleTableContainer.forEntities(fareRules, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  /**
   * Generates StopZoneId validation notices for mock data. Generates mock stopTime, trip, and route
   * data such that the trip and route contain the passed stop.
   *
   * @param csvRowNumber the index of the row and a disambiguator for the generated ids for
   *     stopTime, trip, and route. Must be the same as the csvRowNumber used to create stop and the
   *     fareRules in order to trigger tests.
   * @param stop a list of mock stops to test
   * @param fareRules a list of mock fare rules to test
   * @return a list of validation notices for the mock data
   */
  private static List<ValidationNotice> generateNoticesFromStopAndFareRules(
      int csvRowNumber, GtfsStop stop, List<GtfsFareRule> fareRules) {
    List<GtfsStop> stops = ImmutableList.of(stop);
    List<GtfsRoute> routes =
        ImmutableList.of(
            new GtfsRoute.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setRouteId(toRouteId(csvRowNumber))
                .build());
    List<GtfsTrip> trips =
        ImmutableList.of(
            new GtfsTrip.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setTripId(toTripId(csvRowNumber))
                .setRouteId(toRouteId(csvRowNumber))
                .build());
    List<GtfsStopTime> stopTimes =
        ImmutableList.of(
            new GtfsStopTime.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setStopId(stop.stopId())
                .setTripId(toTripId(csvRowNumber))
                .build());
    return generateNotices(stops, fareRules, stopTimes, trips, routes);
  }

  @Test
  public void stop_zoneIdNotProvided_routeNotInFareRules_noNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(
            createFareRule(csvRowNumber, false, false, false, false),
            createFareRule(csvRowNumber, false, true, false, false),
            createFareRule(csvRowNumber, false, false, true, false),
            createFareRule(csvRowNumber, false, false, false, true));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules)).isEmpty();
  }

  @Test
  public void stop_zoneIdNotProvided_routeInFareRulesWithoutZoneFields_noNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(createFareRule(csvRowNumber, true, false, false, false));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules)).isEmpty();
  }

  @Test
  public void stop_zoneIdNotProvided_routeInFareRulesWithOriginId_yieldsNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(createFareRule(csvRowNumber, true, true, false, false));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules))
        .containsExactly(new StopWithoutZoneIdNotice(stop));
  }

  @Test
  public void stop_zoneIdNotProvided_routeInFareRulesWithDestinationId_yieldsNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(createFareRule(csvRowNumber, true, false, true, false));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules))
        .containsExactly(new StopWithoutZoneIdNotice(stop));
  }

  @Test
  public void stop_zoneIdNotProvided_routeInFareRulesWithContainsId_yieldsNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(createFareRule(csvRowNumber, true, false, false, true));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules))
        .containsExactly(new StopWithoutZoneIdNotice(stop));
  }

  @Test
  public void stop_zoneIdNotProvided_routeInFareRulesWithAndWithoutZoneFields_yieldsNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(null)
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(
            createFareRule(csvRowNumber, true, false, false, false),
            createFareRule(csvRowNumber, true, true, false, false));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules))
        .containsExactly(new StopWithoutZoneIdNotice(stop));
  }

  @Test
  public void stop_zoneIdProvided_noNotice() {
    int csvRowNumber = 0;
    GtfsStop stop =
        new GtfsStop.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setLocationType(GtfsLocationType.STOP)
            .setZoneId(toZoneId(csvRowNumber))
            .build();
    List<GtfsFareRule> fareRules =
        ImmutableList.of(
            createFareRule(csvRowNumber, false, false, false, false),
            createFareRule(csvRowNumber, true, false, false, false),
            createFareRule(csvRowNumber, true, true, false, false),
            createFareRule(csvRowNumber, false, true, false, false),
            createFareRule(csvRowNumber, false, false, true, false),
            createFareRule(csvRowNumber, true, false, false, true));
    assertThat(generateNoticesFromStopAndFareRules(csvRowNumber, stop, fareRules)).isEmpty();
  }

  @Test
  public void allLocationTypesExceptStop_noNotice() {
    int csvRowNumber = 0;
    List<GtfsFareRule> fareRules =
        ImmutableList.of(
            createFareRule(csvRowNumber, false, false, false, false),
            createFareRule(csvRowNumber, true, false, false, false),
            createFareRule(csvRowNumber, true, true, false, false),
            createFareRule(csvRowNumber, false, true, false, false),
            createFareRule(csvRowNumber, false, false, true, false),
            createFareRule(csvRowNumber, true, false, false, true));
    for (GtfsLocationType locationType : GtfsLocationType.values()) {
      if (!locationType.equals(GtfsLocationType.STOP)) {
        assertThat(
                generateNoticesFromStopAndFareRules(
                    csvRowNumber,
                    new GtfsStop.Builder()
                        .setCsvRowNumber(csvRowNumber)
                        .setLocationType(locationType)
                        .setZoneId(null)
                        .build(),
                    fareRules))
            .isEmpty();
        assertThat(
                generateNoticesFromStopAndFareRules(
                    csvRowNumber,
                    new GtfsStop.Builder()
                        .setCsvRowNumber(csvRowNumber)
                        .setLocationType(locationType)
                        .setZoneId(toZoneId(csvRowNumber))
                        .build(),
                    fareRules))
            .isEmpty();
      }
    }
  }

  @Test
  public void emptyFareRule_allStopLocationTypes_noZoneId_noNotice() {
    int csvRowNumber = 0;
    Arrays.stream(GtfsLocationType.values())
        .forEach(
            gtfsLocationType ->
                assertThat(
                        generateNoticesFromStopAndFareRules(
                            csvRowNumber,
                            new GtfsStop.Builder()
                                .setCsvRowNumber(csvRowNumber)
                                .setLocationType(gtfsLocationType)
                                .setZoneId(null)
                                .build(),
                            ImmutableList.of()))
                    .isEmpty());
  }

  @Test
  public void emptyFareRule_allStopLocationTypes_zoneId_noNotice() {
    int csvRowNumber = 0;
    Arrays.stream(GtfsLocationType.values())
        .forEach(
            gtfsLocationType ->
                assertThat(
                        generateNoticesFromStopAndFareRules(
                            csvRowNumber,
                            new GtfsStop.Builder()
                                .setCsvRowNumber(csvRowNumber)
                                .setLocationType(gtfsLocationType)
                                .setZoneId(toStopId(csvRowNumber))
                                .build(),
                            List.of()))
                    .isEmpty());
  }
}
