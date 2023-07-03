package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

public class FeedContactValidatorTest {
  public static GtfsFeedInfo createFeedInfo(
      int csvRowNumber,
      String feedPublisherName,
      String feedPublisherUrl,
      Locale feedLang,
      Locale defaultLang,
      GtfsDate feedStartDate,
      GtfsDate feedEndDate,
      String feedVersion,
      String feedContactEmail,
      String feedContactUrl) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFeedPublisherName(feedPublisherName)
        .setFeedPublisherUrl(feedPublisherUrl)
        .setFeedLang(feedLang)
        .setDefaultLang(defaultLang)
        .setFeedStartDate(feedStartDate)
        .setFeedEndDate(feedEndDate)
        .setFeedVersion(feedVersion)
        .setFeedContactEmail(feedContactEmail)
        .setFeedContactUrl(feedContactUrl)
        .build();
  }

  /**
   * This method is to test the case that a feed has both email and url contact information No
   * FeedContactNotice should be generated
   */
  @Test
  public void hasFeedContactEmailAndUrlShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            "email@gmail.com",
            "https://github.com/MobilityData/gtfs-validator");

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * This method is to test the case that a feed has email contact information but no contact url
   * information No FeedContactNotice should be generated
   */
  @Test
  public void hasFeedContactEmailNoUrlShouldNotGenerateNotice1() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            "email@gmail.com",
            "");

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * This method is to test the case that a feed has email contact information but no contact url
   * information No FeedContactNotice should be generated
   */
  @Test
  public void hasFeedContactEmailNoUrlShouldNotGenerateNotice2() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            "email@gmail.com",
            null);

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * This method is to test the case that a feed has contact url but no contact email No
   * FeedContactNotice should be generated
   */
  @Test
  public void hasFeedContactUrlNoEmailShouldNotGenerateNotice1() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            "",
            "https://github.com/MobilityData/gtfs-validator");

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * This method is to test the case that a feed has contact url but no contact email No
   * FeedContactNotice should be generated
   */
  @Test
  public void hasFeedContactUrlNoEmailShouldNotGenerateNotice2() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            null,
            "https://github.com/MobilityData/gtfs-validator");

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  /**
   * This method is to test the case that a feed has neither contact url nor contact email
   * FeedContactNotice should be generated
   */
  @Test
  public void nonFeedContactEmailAndUrlShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo entity =
        createFeedInfo(
            2,
            "feed publisher name value",
            "feed publisher url value",
            GtfsFeedInfo.DEFAULT_FEED_LANG,
            GtfsFeedInfo.DEFAULT_DEFAULT_LANG,
            GtfsFeedInfo.DEFAULT_FEED_START_DATE,
            GtfsFeedInfo.DEFAULT_FEED_END_DATE,
            GtfsFeedInfo.DEFAULT_FEED_VERSION,
            "",
            "");

    FeedContactValidator underTest = new FeedContactValidator();
    underTest.validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new FeedContactValidator.MissingFeedContactEmailAndUrlNotice(2));
  }
}
