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

  private static List<GtfsShape> createShapeTable(
      int rows, double shapeDistTraveled, double lonLat) {
    ArrayList<GtfsShape> shapes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      shapes.add(
          new GtfsShape.Builder()
              .setCsvRowNumber(i + 1)
              .setShapeId("s" + i)
              .setShapePtLat(lonLat)
              .setShapePtLon(lonLat)
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

  private static List<GtfsStop> createStopTable(int rows) {
    ArrayList<GtfsStop> stops = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      stops.add(
          new GtfsStop.Builder()
              .setCsvRowNumber(i + 1)
              .setStopId("st" + i)
              .setStopLat(0.0)
              .setStopLon(0.0)
              .build());
    }
    return stops;
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips,
      List<GtfsStopTime> stopTimes,
      List<GtfsShape> shapes,
      List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripAndShapeDistanceValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testTripDistanceExceedsShapeDistance() {
    List<ValidationNotice> notices =
        generateNotices(
            createTripTable(2),
            createStopTimesTable(1, 10.0),
            createShapeTable(1, 9.0, 10.0),
            createStopTable(1));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice
                        instanceof
                        TripAndShapeDistanceValidator.TripDistanceExceedsShapeDistanceNotice);
    assertThat(found).isTrue();
  }

  @Test
  public void testTripDistanceExceedsShapeDistanceWarning() {
    List<ValidationNotice> notices =
        generateNotices(
            createTripTable(2),
            createStopTimesTable(1, 10.0),
            createShapeTable(1, 9.0, 0.000001),
            createStopTable(1));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice
                        instanceof
                        TripAndShapeDistanceValidator
                            .TripDistanceExceedsShapeDistanceBelowThresholdNotice);
    assertThat(found).isTrue();
  }
}
