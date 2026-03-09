package org.mobilitydata.gtfsvalidator.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroups;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

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
            createStopTimesTable(1, "a", null),
            createLocationGroupsTable(1, "b", "testgroup"));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice instanceof MissingShapesFileValidator.MissingRecommendedFileNotice);
    assertFalse(found);
  }

  @Test
    public void testShapesFileAndZoneBasedDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(1),
            createStopTimesTable(1, null, "c"),
            createLocationGroupsTable(1, "d", "t3stgroup"));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice instanceof MissingShapesFileValidator.MissingRecommendedFileNotice);
    assertFalse(found);
  }

  @Test
    public void testNoShapesFileAndNoDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(0),
            createStopTimesTable(1, null, null),
            createLocationGroupsTable(0, null, null));
    boolean found =
        notices.stream()
            .anyMatch(
                notice ->
                    notice instanceof MissingShapesFileValidator.MissingRecommendedFileNotice);
    assertThat(found).isTrue();
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
