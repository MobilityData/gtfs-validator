package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.FeedServiceDateValidator.MissingFeedInfoDateNotice;

public class FeedServiceDateValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsFeedInfo feedInfo) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FeedServiceDateValidator().validate(feedInfo, noticeContainer);
    return noticeContainer.getValidationNotices();
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
  public void noStartDateShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "url value", Locale.CANADA, null, GtfsDate.fromEpochDay(450))))
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_start_date"));
  }

  @Test
  public void noEndDateShouldGenerateNotice() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "url value", Locale.CANADA, GtfsDate.fromEpochDay(450), null)))
        .containsExactly(new MissingFeedInfoDateNotice(1, "feed_end_date"));
  }

  @Test
  public void bothDatesCanBeBlank() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1, "name value", "https://www.mobilitydata.org", Locale.CANADA, null, null)))
        .isEmpty();
  }

  @Test
  public void bothDatesCanBeProvided() {
    assertThat(
            generateNotices(
                createFeedInfo(
                    1,
                    "name value",
                    "https://www.mobilitydata.org",
                    Locale.CANADA,
                    GtfsDate.fromEpochDay(450),
                    GtfsDate.fromEpochDay(555))))
        .isEmpty();
  }
}
