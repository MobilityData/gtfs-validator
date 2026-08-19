package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class TripShapeIdConditionalValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsRoute> routes, List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripShapeIdConditionalValidator(
            GtfsRouteTableContainer.forEntities(routes, noticeContainer),
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsRoute.Builder route() {
    return new GtfsRoute.Builder().setCsvRowNumber(2).setRouteId("route1");
  }

  private static GtfsTrip.Builder trip() {
    return new GtfsTrip.Builder().setCsvRowNumber(3).setTripId("trip1").setRouteId("route1");
  }

  private static GtfsStopTime.Builder stopTime(int csvRowNumber) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId("trip1")
        .setStopSequence(csvRowNumber);
  }

  private static MissingRequiredFieldNotice expectedNotice() {
    return new MissingRequiredFieldNotice("trips.txt", 3, "shape_id");
  }

  @Test
  public void continuousPickupOnRouteWithoutShapeIdShouldGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousPickup(0).build()),
            List.of(trip().build()),
            List.of(stopTime(4).build()));
    assertThat(notices).containsExactly(expectedNotice());
  }

  @Test
  public void continuousDropOffOnRouteWithoutShapeIdShouldGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousDropOff(2).build()),
            List.of(trip().build()),
            List.of(stopTime(4).build()));
    assertThat(notices).containsExactly(expectedNotice());
  }

  @Test
  public void continuousPickupOnStopTimeWithoutShapeIdShouldGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().build()),
            List.of(trip().build()),
            List.of(stopTime(4).build(), stopTime(5).setContinuousPickup(3).build()));
    assertThat(notices).containsExactly(expectedNotice());
  }

  @Test
  public void continuousBehaviorWithShapeIdShouldNotGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousPickup(0).build()),
            List.of(trip().setShapeId("shape1").build()),
            List.of(stopTime(4).build()));
    assertThat(notices).isEmpty();
  }

  @Test
  public void notAvailableContinuousBehaviorShouldNotGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousPickup(1).setContinuousDropOff(1).build()),
            List.of(trip().build()),
            List.of(stopTime(4).setContinuousPickup(1).setContinuousDropOff(1).build()));
    assertThat(notices).isEmpty();
  }

  @Test
  public void stopTimeShouldOverrideContinuousBehaviorOnRoute() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousPickup(0).build()),
            List.of(trip().build()),
            List.of(stopTime(4).setContinuousPickup(1).build()));
    assertThat(notices).isEmpty();
  }

  @Test
  public void noContinuousBehaviorShouldNotGenerateNotice() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().build()), List.of(trip().build()), List.of(stopTime(4).build()));
    assertThat(notices).isEmpty();
  }

  @Test
  public void tripWithoutStopTimesShouldStillUseRouteContinuousBehavior() {
    List<ValidationNotice> notices =
        generateNotices(
            List.of(route().setContinuousPickup(2).build()), List.of(trip().build()), List.of());
    assertThat(notices).containsExactly(expectedNotice());
  }
}
