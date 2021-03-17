package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingFeedInfoDateNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

public class FeedServiceDateValidatorTest {

  private static GtfsFeedInfo createFeedInfo(
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
  public void noStartDateShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FeedServiceDateValidator underTest = new FeedServiceDateValidator();

    underTest.validate(
        createFeedInfo(
            1, "name value", "url value", Locale.CANADA, null, GtfsDate.fromEpochDay(450)),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_start_date"));
  }

  @Test
  public void noEndDateShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FeedServiceDateValidator underTest = new FeedServiceDateValidator();

    underTest.validate(
        createFeedInfo(
            1, "name value", "url value", Locale.CANADA, GtfsDate.fromEpochDay(450), null),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_end_date"));
  }

  @Test
  public void bothDatesCanBeBlank() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FeedServiceDateValidator underTest = new FeedServiceDateValidator();

    underTest.validate(
        createFeedInfo(1, "name value", "https://www.mobilitydata.org", Locale.CANADA, null, null),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }

  @Test
  public void bothDatesCanBeProvided() {
    NoticeContainer noticeContainer = new NoticeContainer();
    FeedServiceDateValidator underTest = new FeedServiceDateValidator();

    underTest.validate(
        createFeedInfo(
            1,
            "name value",
            "https://www.mobilitydata.org",
            Locale.CANADA,
            GtfsDate.fromEpochDay(450),
            GtfsDate.fromEpochDay(555)),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices().isEmpty());
  }
}
