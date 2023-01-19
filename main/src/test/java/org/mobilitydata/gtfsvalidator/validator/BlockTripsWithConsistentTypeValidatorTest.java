/*
 * Copyright 2023 Google LLC
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
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.BlockTripsWithConsistentTypeValidator.BlockTripsWithInconsistentRouteTypesNotice;

@RunWith(JUnit4.class)
public final class BlockTripsWithConsistentTypeValidatorTest {
  private static String tripIdForRow(int csvRowNumber) {
    return "trip" + csvRowNumber;
  }

  private static GtfsTrip createTrip(int csvRowNumber, String routeId, String blockId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripIdForRow(csvRowNumber))
        .setRouteId(routeId)
        .setBlockId(blockId)
        .build();
  }

  private static GtfsTrip createTrip(int csvRowNumber, String routeId) {
    return createTrip(csvRowNumber, routeId, null);
  }

  private static GtfsRoute createRoute(int csvRowNumber, String routeId, GtfsRouteType routeType) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setRouteType(routeType.getNumber())
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsRoute> routes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new BlockTripsWithConsistentTypeValidator(
        GtfsTripTableContainer.forEntities(trips, noticeContainer),
        GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void validate_noBlock_yieldsNoNotice() {
    assertThat(
        generateNotices(ImmutableList.of(createTrip(2, "busRoute"), createTrip(3, "railRoute")),
            ImmutableList.of(createRoute(2, "busRoute", GtfsRouteType.BUS),
                createRoute(3, "railRoute", GtfsRouteType.RAIL))))
        .isEmpty();
  }

  @Test
  public void validate_sameRouteType_yieldsNoNotice() {
    assertThat(generateNotices(ImmutableList.of(createTrip(2, "busRoute", "busBlock"),
                                   createTrip(3, "busRoute", "busBlock"),
                                   createTrip(4, "railRoute", "railBlock"),
                                   createTrip(5, "railRoute", "railBlock"),
                                   createTrip(6, "anotherRailRoute", "railBlock")),
                   ImmutableList.of(createRoute(2, "busRoute", GtfsRouteType.BUS),
                       createRoute(3, "railRoute", GtfsRouteType.RAIL),
                       createRoute(4, "anotherRailRoute", GtfsRouteType.RAIL))))
        .isEmpty();
  }

  @Test
  public void validate_incompatibleBusAndRail_yieldsNotice() {
    ImmutableList<GtfsTrip> trips = ImmutableList.of(
        createTrip(2, "busRoute", "busBlock"), createTrip(3, "railRoute", "busBlock"));
    ImmutableList<GtfsRoute> routes =
        ImmutableList.of(createRoute(2, "busRoute", GtfsRouteType.BUS),
            createRoute(3, "railRoute", GtfsRouteType.RAIL));
    assertThat(generateNotices(trips, routes))
        .containsExactly(new BlockTripsWithInconsistentRouteTypesNotice(
            trips.get(0), routes.get(0), trips.get(1), routes.get(1)));
  }

  @Test
  public void validate_compatibleRailAndSubway_yieldsNoNotice() {
    assertThat(generateNotices(ImmutableList.of(createTrip(2, "subwayRoute", "railBlock"),
                                   createTrip(3, "railRoute", "railBlock")),
                   ImmutableList.of(createRoute(2, "subwayRoute", GtfsRouteType.SUBWAY),
                       createRoute(3, "railRoute", GtfsRouteType.RAIL))))
        .isEmpty();
  }
}
