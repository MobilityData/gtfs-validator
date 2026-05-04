package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroups;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.TableStatus;

public class MissingShapesFileValidatorTest {

  private static List<GtfsShape> createShapeTable(int rows) {
    ArrayList<GtfsShape> shapes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      shapes.add(new GtfsShape.Builder().setCsvRowNumber(i + 1).setShapeId("s" + i).build());
    }
    return shapes;
  }

  private static List<GtfsStopTime> createStopTimesTable(
      int rows, String locationGroupId, String locationId) {
    ArrayList<GtfsStopTime> stopTimes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      stopTimes.add(
          new GtfsStopTime.Builder()
              .setCsvRowNumber(i + 1)
              .setLocationGroupId(locationGroupId)
              .setLocationId(locationId)
              .setTripId(locationGroupId)
              .setStopSequence(i + 1)
              .build());
    }
    return stopTimes;
  }

  private static List<GtfsLocationGroups> createLocationGroupsTable(
      int rows, String groupId, String groupName) {
    ArrayList<GtfsLocationGroups> locationGroups = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      locationGroups.add(
          new GtfsLocationGroups.Builder()
              .setCsvRowNumber(i + 1)
              .setLocationGroupId(groupId)
              .setLocationGroupName(groupName)
              .build());
    }
    return locationGroups;
  }

  @Test
  public void testShapesFileAndFixedDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(1),
            GtfsShapeTableContainer.forStatus(null),
            createStopTimesTable(1, "a", null),
            createLocationGroupsTable(1, "b", "testgroup"));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice instanceof MissingRecommendedFileNotice);
    assertThat(found).isFalse();
  }

  @Test
  public void testShapesFileAndZoneBasedDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(1),
            GtfsShapeTableContainer.forStatus(null),
            createStopTimesTable(1, null, "c"),
            createLocationGroupsTable(1, "d", "t3stgroup"));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice instanceof MissingRecommendedFileNotice);
    assertThat(found).isFalse();
  }

  @Test
  public void testNoShapesFileAndNoDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createStopTimesTable(1, null, null),
            createLocationGroupsTable(0, null, null));
    long missingRecommendedFileNoticesCount =
        notices.stream()
            .filter(
                notice ->
                    notice instanceof MissingRecommendedFileNotice)
            .count();
    assertThat(missingRecommendedFileNoticesCount).isAtLeast(1);
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsShape> shapes,
      GtfsShapeTableContainer shapeContainer,
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
