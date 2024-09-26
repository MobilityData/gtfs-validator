package org.mobilitydata.gtfsvalidator.report;

import com.google.common.flogger.FluentLogger;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.report.model.AgencyMetadata;
import org.mobilitydata.gtfsvalidator.report.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

/**
 * Used to generate the summary of json report using the built-in object serialization of GSON.
 * Fields names found here will be present in the generated json file except if renamed with the
 * {@code @SerializedName} annotation.
 */
@SuppressWarnings("unused") // The fields of this class are only read by Gson when serializing.
public class JsonReportSummary {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private String validatorVersion;
  private String validatedAt;
  private String gtfsInput;
  private int threads;
  private String outputDirectory;
  private String systemErrorsReportName;
  private String validationReportName;
  private String htmlReportName;
  private String countryCode;
  private String dateForValidation;
  private JsonReportFeedInfo feedInfo;
  private List<JsonReportAgencyMetadata> agencies;
  private Set<String> files;
  private Double validationTimeSeconds;

  @SerializedName("counts")
  private JsonReportCounts jsonReportCounts;

  private List<String> gtfsFeatures;

  public JsonReportSummary(
      FeedMetadata feedMetadata,
      ValidationRunnerConfig config,
      VersionInfo versionInfo,
      String date) {
    this.validatedAt = date;

    this.validatorVersion = versionInfo.currentVersion().orElse(null);

    if (config != null) {
      this.gtfsInput = config.gtfsSource().toString();
      this.threads = config.numThreads();
      this.outputDirectory = config.outputDirectory().toString();
      this.systemErrorsReportName = config.systemErrorsReportFileName();
      this.validationReportName = config.validationReportFileName();
      this.htmlReportName = config.htmlReportFileName();
      this.countryCode = config.countryCode().getCountryCode();
      this.dateForValidation = config.dateForValidation().toString();
    } else {
      logger.atSevere().log(
          "No validation configuration for JSON report, there will be missing data in the report.");
    }

    if (feedMetadata != null) {
      if (feedMetadata.feedInfo != null) {
        this.feedInfo = new JsonReportFeedInfo(feedMetadata.feedInfo);
        this.validationTimeSeconds = feedMetadata.validationTimeSeconds;
      } else {
        logger.atSevere().log(
            "No feed info for feed "
                + this.gtfsInput
                + ", there will be missing data in the report.");
      }

      if (feedMetadata.agencies != null) {
        this.agencies =
            feedMetadata.agencies.stream()
                .map(JsonReportAgencyMetadata::new)
                .collect(Collectors.toList());
      }

      this.files = feedMetadata.getFilenames();
      if (feedMetadata.counts != null) {
        jsonReportCounts = new JsonReportCounts(feedMetadata.counts);
      } else {
        logger.atSevere().log(
            "Counts are not available for "
                + this.gtfsInput
                + ", there will be missing data in the report.");
      }

      this.gtfsFeatures =
          feedMetadata.specFeatures == null
              ? null
              : feedMetadata.specFeatures.entrySet().stream()
                  .filter(Map.Entry::getValue)
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toList());
    } else {
      logger.atSevere().log(
          "No feed metadata for " + this.gtfsInput + ", there will be missing data in the report.");
    }
  }

  /**
   * This class is used as a container of data to be converted to json using the builtin object
   * serialization of GSON. We use this class instead of the Map in FeedMetadata directly because
   * the schema of the resulting json file has to be stable and controlled as it becomes part of the
   * exported API.
   */
  private static class JsonReportFeedInfo {
    public JsonReportFeedInfo(Map<String, String> feedInfo) {
      publisherName = feedInfo.get(FeedMetadata.FEED_INFO_PUBLISHER_NAME);
      publisherUrl = feedInfo.get(FeedMetadata.FEED_INFO_PUBLISHER_URL);
      feedEmail = feedInfo.get(FeedMetadata.FEED_INFO_FEED_CONTACT_EMAIL);
      feedLanguage = feedInfo.get(FeedMetadata.FEED_INFO_FEED_LANGUAGE);
      feedStartDate = feedInfo.get(FeedMetadata.FEED_INFO_FEED_START_DATE);
      feedEndDate = feedInfo.get(FeedMetadata.FEED_INFO_FEED_END_DATE);
      feedServiceWindow = feedInfo.get(FeedMetadata.FEED_INFO_SERVICE_WINDOW);
    }

    String publisherName;
    String publisherUrl;
    String feedLanguage;
    String feedStartDate;
    String feedEndDate;
    String feedEmail;
    String feedServiceWindow;
  }

  private static class JsonReportAgencyMetadata {
    private final String name;
    private final String url;
    private final String phone;
    private final String email;

    public JsonReportAgencyMetadata(AgencyMetadata agencyMetadata) {
      name = agencyMetadata.name;
      url = agencyMetadata.url;
      phone = agencyMetadata.phone;
      email = agencyMetadata.email;
    }
  }

  private static class JsonReportCounts {
    public JsonReportCounts(Map<String, Integer> counts) {
      shapes = counts.get(FeedMetadata.COUNTS_SHAPES);
      stops = counts.get(FeedMetadata.COUNTS_STOPS);
      routes = counts.get(FeedMetadata.COUNTS_ROUTES);
      trips = counts.get(FeedMetadata.COUNTS_TRIPS);
      agencies = counts.get(FeedMetadata.COUNTS_AGENCIES);
      blocks = counts.get(FeedMetadata.COUNTS_BLOCKS);
    }

    @SerializedName("Shapes")
    Integer shapes;

    @SerializedName("Stops")
    Integer stops;

    @SerializedName("Routes")
    Integer routes;

    @SerializedName("Trips")
    Integer trips;

    @SerializedName("Agencies")
    Integer agencies;

    @SerializedName("Blocks")
    Integer blocks;
  }
}
