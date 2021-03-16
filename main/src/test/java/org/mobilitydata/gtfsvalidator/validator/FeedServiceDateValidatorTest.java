package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingFeedInfoDateNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

public class FeedServiceDateValidatorTest {

  private static GtfsFeedInfoTableContainer createFeedInfoTable(
      NoticeContainer noticeContainer, List<GtfsFeedInfo> entities) {
    return GtfsFeedInfoTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsFeedInfo createFeedInfo(
      long csvRowNumber,
      String feedPublisherName,
      String feedPublisherUrl,
      Locale feedLang,
      GtfsDate feedStartDate,
      GtfsDate feedEndDate) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFeedPublisherName(feedPublisherName)
        .setFeedPublisherUrl(feedPublisherUrl)
        .setFeedLang(feedLang)
        .setDefaultLang(null)
        .setFeedStartDate(feedStartDate)
        .setFeedEndDate(feedEndDate)
        .setFeedVersion(null)
        .setFeedContactEmail(null)
        .setFeedContactUrl(null)
        .build();
  }

  @Test
  public void startDateBeforeEndDateShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfoTableContainer gtfsFeedInfoTable =
        createFeedInfoTable(
            noticeContainer,
            ImmutableList.of(
                createFeedInfo(
                    1,
                    "name value",
                    "url value",
                    Locale.CANADA,
                    GtfsDate.fromEpochDay(340),
                    GtfsDate.fromEpochDay(450))));

    FeedServiceDateValidator underTest = new FeedServiceDateValidator();
    underTest.feedInfoTable = gtfsFeedInfoTable;

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }

  @Test
  public void noStartDateShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfoTableContainer gtfsFeedInfoTable =
        createFeedInfoTable(
            noticeContainer,
            ImmutableList.of(
                createFeedInfo(
                    1,
                    "name value",
                    "url value",
                    Locale.CANADA,
                    null,
                    GtfsDate.fromEpochDay(450))));

    FeedServiceDateValidator underTest = new FeedServiceDateValidator();
    underTest.feedInfoTable = gtfsFeedInfoTable;

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_start_date"));
  }

  @Test
  public void noEndDateShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfoTableContainer gtfsFeedInfoTable =
        createFeedInfoTable(
            noticeContainer,
            ImmutableList.of(
                createFeedInfo(
                    1,
                    "name value",
                    "url value",
                    Locale.CANADA,
                    GtfsDate.fromEpochDay(450),
                    null)));

    FeedServiceDateValidator underTest = new FeedServiceDateValidator();
    underTest.feedInfoTable = gtfsFeedInfoTable;

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_end_date"));
  }

  @Test
  public void bothDatesCanBeBlank() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfoTableContainer gtfsFeedInfoTable =
        createFeedInfoTable(
            noticeContainer,
            ImmutableList.of(
                createFeedInfo(
                    1, "name value", "https://www.mobilitydata.org", Locale.CANADA, null, null)));

    FeedServiceDateValidator underTest = new FeedServiceDateValidator();
    underTest.feedInfoTable = gtfsFeedInfoTable;

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }

  @Test
  public void bothDatesCanBeProvided() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfoTableContainer gtfsFeedInfoTable =
        createFeedInfoTable(
            noticeContainer,
            ImmutableList.of(
                createFeedInfo(
                    1,
                    "name value",
                    "https://www.mobilitydata.org",
                    Locale.CANADA,
                    GtfsDate.fromEpochDay(450),
                    GtfsDate.fromEpochDay(555))));

    FeedServiceDateValidator underTest = new FeedServiceDateValidator();
    underTest.feedInfoTable = gtfsFeedInfoTable;

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }
}
