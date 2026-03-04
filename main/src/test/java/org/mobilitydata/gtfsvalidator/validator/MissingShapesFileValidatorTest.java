package org.mobilitydata.gtfsvalidator.validator;

import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroups;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.validator.MissingShapesFileValidator.MissingRecommendedFileNotice;

public class MissingShapesFileValidatorTest {

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


  private static GtfsStop createLocationGroupsTable(int csvRowNumber, GtfsLocationGroups locationGroups) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .build();
  }

  @Test
  public void testTripDistanceExceedsShapeDistance() {
    List<ValidationNotice> notices =
        generateNotices(
            createStopTimesTable(1, 10.0),
            createShapeTable(1, 9.0, 10.0),
            createLocationGroupsTable());
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
  public void tripServingOneStopShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createTrip(1, "route id value", "service id value", "t0"),
                    createTrip(3, "route id value", "service id value", "t1")),
                ImmutableList.of(
                    createStopTime(0, "t0", "s0", 2),
                    createStopTime(0, "t1", "s3", 5),
                    createStopTime(2, "t1", "s4", 9))))
        .containsExactly(new MissingRecommendedFileNotice(1, "t0"));
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsShape> shapes,
      List<GtfsStopTime> stopTimes,
      List<GtfsLocationGroups> locationGroups) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingShapesFileValidator(
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsLocationGroupsTableContainer.forEntities(locationGroups, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
