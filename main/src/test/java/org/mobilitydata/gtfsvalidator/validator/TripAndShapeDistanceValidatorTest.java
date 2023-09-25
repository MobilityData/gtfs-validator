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
public class TripAndShapeDistanceValidatorTest {

  private static List<GtfsTrip> createTripTable(int rows) {
    ArrayList<GtfsTrip> trips = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      trips.add(
          new GtfsTrip.Builder()
              .setCsvRowNumber(i + 1)
              .setTripId("t" + i)
              .setServiceId("sr" + i)
              .setRouteId("r" + i)
              .setShapeId("s" + i)
              .build());
    }
    return trips;
  }

  private static List<GtfsShape> createShapeTable(int rows, double shapeDistTraveled) {
    ArrayList<GtfsShape> shapes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      shapes.add(
          new GtfsShape.Builder()
              .setCsvRowNumber(i + 1)
              .setShapeId("s" + i)
              .setShapePtLat(1.0)
              .setShapePtLon(1.0)
              .setShapePtSequence(0)
              .setShapeDistTraveled(shapeDistTraveled + i)
              .build());
    }
    return shapes;
  }

  private static List<GtfsStopTime> createStopTimesTable(int rows, double shapeDistTraveled) {
    ArrayList<GtfsStopTime> stopTimes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(i + 1)
              .setTripId("t" + i)
              .setStopSequence(0)
              .setStopId("st" + i)
              .setShapeDistTraveled(shapeDistTraveled + i)
              .build());
    }
    return stopTimes;
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes, List<GtfsShape> shapes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripAndShapeDistanceValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testTripDistanceExceedsShapeDistance() {
    assertThat(
            generateNotices(
                createTripTable(1), createStopTimesTable(1, 10.0), createShapeTable(1, 9.0)))
        .isNotEmpty();
  }

  @Test
  public void testValidTripVsShapeDistance1() {
    assertThat(
            generateNotices(
                createTripTable(1), createStopTimesTable(1, 10.0), createShapeTable(1, 10.0)))
        .isEmpty();
  }

  @Test
  public void testValidTripVsShapeDistance2() {
    assertThat(
            generateNotices(
                createTripTable(1), createStopTimesTable(1, 9.0), createShapeTable(1, 10.0)))
        .isEmpty();
  }
}
