package org.mobilitydata.gtfsvalidator.reportsummary;

import java.util.Map;

/**
 * This class is used as a container of data to be converted to json using the builtin object
 * serialization of GSON. We use this class instead of the Map in FeedMetadata directly because the
 * schema of the resulting json file has to be stable and controlled as it becomes part of the
 * exported API.
 */
public class JsonReportFeedInfo {
  /*
   * Use these strings as keys in the FeedInfo map. Also used to specify the info that will appear
   * in the json report. Adding elements to feedInfo will not automatically be included in the json
   * report and should be explicitly handled in the json report code.
   */
  public static final String FEED_INFO_PUBLISHER_NAME = "Publisher Name";
  public static final String FEED_INFO_PUBLISHER_URL = "Publisher URL";
  public static final String FEED_INFO_FEED_CONTACT_EMAIL = "Feed Email";
  public static final String FEED_INFO_FEED_LANGUAGE = "Feed Language";
  public static final String FEED_INFO_FEED_START_DATE = "Feed Start Date";
  public static final String FEED_INFO_FEED_END_DATE = "Feed End Date";
  public static final String FEED_INFO_SERVICE_WINDOW = "Service Window";
  public static final String FEED_INFO_SERVICE_WINDOW_START = "Service Window Start";
  public static final String FEED_INFO_SERVICE_WINDOW_END = "Service Window End";

  public JsonReportFeedInfo(Map<String, String> feedInfo) {
    publisherName = feedInfo.get(FEED_INFO_PUBLISHER_NAME);
    publisherUrl = feedInfo.get(FEED_INFO_PUBLISHER_URL);
    feedEmail = feedInfo.get(FEED_INFO_FEED_CONTACT_EMAIL);
    feedLanguage = feedInfo.get(FEED_INFO_FEED_LANGUAGE);
    feedStartDate = feedInfo.get(FEED_INFO_FEED_START_DATE);
    feedEndDate = feedInfo.get(FEED_INFO_FEED_END_DATE);
    feedServiceWindowStart = feedInfo.get(FEED_INFO_SERVICE_WINDOW_START);
    feedServiceWindowEnd = feedInfo.get(FEED_INFO_SERVICE_WINDOW_END);
  }

  /**
   * `feed_publisher_name` from `feed_info.txt. Full name of the organization that publishes the
   * dataset.
   */
  String publisherName;

  /**
   * `feed_publisher_url` from `feed_info.txt`. URL of the dataset publishing organization's
   * website.
   */
  String publisherUrl;

  /** `feed_lang` from `feed_info.txt`. Default language used for the text in this dataset. */
  String feedLanguage;

  /**
   * `feed_start_date` from `feed_info.txt`. The dataset provides complete and reliable schedule
   * information for service in the period from the beginning of the `feed_start_date` day to the
   * end of the `feed_end_date` day.
   */
  String feedStartDate;

  /** `feed_end_date` from `feed_info.txt`. See above. */
  String feedEndDate;

  /**
   * `feed_contact_email` from `feed_info.txt`. Email address for communication regarding the GTFS
   * dataset and data publishing practices.
   */
  String feedEmail;

  /**
   * The start date of the service, based on the earliest date referenced in `calendar.txt` or
   * `calendar_dates.txt` that is used by a `trip_id` in `trips.txt`.
   */
  String feedServiceWindowStart;

  /**
   * The end date of the service, based on the latest date referenced in `calendar.txt` or
   * `calendar_dates.txt` that is used by a `trip_id` in `trips.txt`.
   */
  String feedServiceWindowEnd;
}
