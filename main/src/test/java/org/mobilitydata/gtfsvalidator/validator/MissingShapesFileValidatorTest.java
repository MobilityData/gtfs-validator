package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroups;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

public class MissingShapesFileValidatorTest {

  private static GtfsFeedContainer createFeedContainer(
      List<GtfsShape> shapes, List<GtfsLocationGroups> locationGroups) {
    NoticeContainer noticeContainer = new NoticeContainer();
    return new GtfsFeedContainer(
        ImmutableList.of(
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer),
            GtfsLocationGroupsTableContainer.forEntities(locationGroups, noticeContainer)));
  }

  private static List<GtfsShape> createShapeTable(int rows) {
    ArrayList<GtfsShape> shapes = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      if (rows == -1) {
        return null;
      }
      shapes.add(new GtfsShape.Builder().setCsvRowNumber(i + 1).setShapeId("s" + i).build());
    }
    return shapes;
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
            createLocationGroupsTable(1, "b", "testgroup"),
            createFeedContainer(
                createShapeTable(1), createLocationGroupsTable(1, "b", "testgroup")));
    boolean found =
        notices.stream().anyMatch(notice -> notice instanceof MissingRecommendedFileNotice);
    assertThat(found).isFalse();
  }

  @Test
  public void testShapesFileAndZoneBasedDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(1),
            createLocationGroupsTable(1, "d", "t3stgroup"),
            createFeedContainer(
                createShapeTable(1), createLocationGroupsTable(1, "d", "t3stgroup")));
    boolean found =
        notices.stream().anyMatch(notice -> notice instanceof MissingRecommendedFileNotice);
    assertThat(found).isFalse();
  }

  @Test
  public void testNoShapesFileAndNoDrtPresent() {
    List<ValidationNotice> notices =
        generateNotices(
            createShapeTable(-1),
            createLocationGroupsTable(0, null, null),
            createFeedContainer(createShapeTable(-1), createLocationGroupsTable(0, null, null)));
    long missingRecommendedFileNoticesCount =
        notices.stream().filter(notice -> notice instanceof MissingRecommendedFileNotice).count();
    assertThat(missingRecommendedFileNoticesCount).isAtLeast(1);
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsShape> shapes,
      List<GtfsLocationGroups> locationGroups,
      GtfsFeedContainer feedContainer) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingShapesFileValidator(
            GtfsShapeTableContainer.forEntities(shapes, noticeContainer),
            GtfsLocationGroupsTableContainer.forEntities(locationGroups, noticeContainer),
            feedContainer)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
