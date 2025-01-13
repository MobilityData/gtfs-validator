package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class ContinuousPickupDropOffValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsRoute> routes, List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsRouteTableContainer routeTable =
        GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    GtfsTripTableContainer tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    GtfsStopTimeTableContainer stopTimeTable =
        GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    new ContinuousPickupDropOffValidator(routeTable, tripTable, stopTimeTable)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void continuousPickupWithPickupDropOffWindowShouldGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setRouteId("route1")
                    .setContinuousPickup(1)
                    .build()),
            List.of(
                new GtfsTrip.Builder()
                    .setCsvRowNumber(2)
                    .setTripId("trip1")
                    .setRouteId("route1")
                    .build()),
            List.of(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(3)
                    .setTripId("trip1")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("09:00:00"))
                    .build()));
    assertThat(notices)
        .containsExactly(
            new ContinuousPickupDropOffValidator.ContinuousPickupDropOffNotice(
                1, "trip1", GtfsTime.fromString("08:00:00"), GtfsTime.fromString("09:00:00")));
  }

  @Test
  public void continuousDropOffWithPickupDropOffWindowShouldGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setRouteId("route1")
                    .setContinuousDropOff(2)
                    .build()),
            List.of(
                new GtfsTrip.Builder()
                    .setCsvRowNumber(2)
                    .setTripId("trip1")
                    .setRouteId("route1")
                    .build()),
            List.of(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(3)
                    .setTripId("trip1")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .setEndPickupDropOffWindow(GtfsTime.fromString("09:00:00"))
                    .build()));
    assertThat(notices)
        .containsExactly(
            new ContinuousPickupDropOffValidator.ContinuousPickupDropOffNotice(
                1, "trip1", GtfsTime.fromString("08:00:00"), GtfsTime.fromString("09:00:00")));
  }

  @Test
  public void noContinuousPickupOrDropOffShouldNotGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(new GtfsRoute.Builder().setCsvRowNumber(1).setRouteId("route1").build()),
            List.of(
                new GtfsTrip.Builder()
                    .setCsvRowNumber(2)
                    .setTripId("trip1")
                    .setRouteId("route1")
                    .build()),
            List.of(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(3)
                    .setTripId("trip1")
                    .setStartPickupDropOffWindow(GtfsTime.fromString("08:00:00"))
                    .build()));
    assertThat(notices).isEmpty();
  }

  @Test
  public void continuousPickupAndDropOffWithoutPickupDropOffWindowShouldNotGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(
                new GtfsRoute.Builder()
                    .setCsvRowNumber(1)
                    .setRouteId("route1")
                    .setContinuousPickup(1)
                    .setContinuousDropOff(1)
                    .build()),
            List.of(
                new GtfsTrip.Builder()
                    .setCsvRowNumber(2)
                    .setTripId("trip1")
                    .setRouteId("route1")
                    .build()),
            List.of(new GtfsStopTime.Builder().setCsvRowNumber(3).setTripId("trip1").build()));
    assertThat(notices).isEmpty();
  }
}
