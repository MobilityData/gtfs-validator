package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@RunWith(JUnit4.class)
public class BikeAllowanceInfoValidatorTest {

  private static List<GtfsTrip> createTripTable(
      String routeId, String serviceId, GtfsBikesAllowed bikesAllowed, int rows) {
    ArrayList<GtfsTrip> trips = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      trips.add(
          new GtfsTrip.Builder()
              .setCsvRowNumber(i + 1)
              .setTripId("t" + i)
              .setServiceId(serviceId)
              .setRouteId(routeId)
              .setBikesAllowed(bikesAllowed)
              .build());
    }
    return trips;
  }

  private static List<GtfsRoute> createRouteTable(String routeId, GtfsRouteType routeType) {
    ArrayList<GtfsRoute> routes = new ArrayList<>();
    routes.add(
        new GtfsRoute.Builder()
            .setCsvRowNumber(1)
            .setRouteId(routeId)
            .setRouteType(routeType)
            .build());
    return routes;
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsRoute> routes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new BikesAllowanceInfoValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsRouteTableContainer.forEntities(routes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testValidFeedForFerryTrips() {
    assertThat(
            generateNotices(
                createTripTable("route1", "service1", GtfsBikesAllowed.ALLOWED, 2),
                createRouteTable("route1", GtfsRouteType.FERRY)))
        .isEmpty();
  }

  @Test
  public void testValidFeedForNonFerryTrips() {
    assertThat(
            generateNotices(
                createTripTable("route1", "service1", GtfsBikesAllowed.UNRECOGNIZED, 2),
                createRouteTable("route1", GtfsRouteType.BUS)))
        .isEmpty();
    assertThat(
            generateNotices(
                createTripTable("route1", "service1", null, 2),
                createRouteTable("route1", GtfsRouteType.BUS)))
        .isEmpty();
  }

  @Test
  public void testInvalidBikesAllowedValueForFerryTrips() {
    assertThat(
            generateNotices(
                createTripTable("route1", "service1", GtfsBikesAllowed.UNRECOGNIZED, 2),
                createRouteTable("route1", GtfsRouteType.FERRY)))
        .hasSize(2);
    assertThat(
            generateNotices(
                createTripTable("route1", "service1", null, 2),
                createRouteTable("route1", GtfsRouteType.FERRY)))
        .hasSize(2);
  }
}
