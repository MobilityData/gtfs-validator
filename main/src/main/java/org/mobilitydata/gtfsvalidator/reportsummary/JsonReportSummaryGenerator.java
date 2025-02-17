package org.mobilitydata.gtfsvalidator.reportsummary;

import com.google.common.flogger.FluentLogger;
import java.util.Map;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.reportsummary.model.FeatureMetadata;
import org.mobilitydata.gtfsvalidator.reportsummary.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

/**
 * Used to generate the summary of json report using the built-in object serialization of GSON.
 * Fields names found here will be present in the generated json file except if renamed with the
 * {@code @SerializedName} annotation.
 */
@SuppressWarnings("unused") // The fields of this class are only read by Gson when serializing.
public class JsonReportSummaryGenerator {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  public JsonReportSummary summary;
  public final String gtfsInput;

  public JsonReportSummaryGenerator(
      FeedMetadata feedMetadata,
      ValidationRunnerConfig config,
      VersionInfo versionInfo,
      String date) {
    this.gtfsInput = config != null ? config.gtfsSource().toString() : null;
    this.summary =
        new JsonReportSummary(
            versionInfo.currentVersion().orElse(null),
            date,
            config != null ? config.gtfsSource().toString() : null,
            config != null ? config.numThreads() : 0,
            config != null ? config.outputDirectory().toString() : null,
            config != null ? config.systemErrorsReportFileName() : null,
            config != null ? config.validationReportFileName() : null,
            config != null ? config.htmlReportFileName() : null,
            config != null ? config.countryCode().getCountryCode() : null,
            config != null ? config.dateForValidation().toString() : null,
            feedMetadata != null && feedMetadata.feedInfo != null
                ? new JsonReportFeedInfo(feedMetadata.feedInfo)
                : null,
            feedMetadata != null && feedMetadata.agencies != null
                ? feedMetadata.agencies.stream()
                    .map(JsonReportAgencyMetadata::new)
                    .collect(Collectors.toList())
                : null,
            feedMetadata != null ? feedMetadata.getFilenames() : null,
            feedMetadata != null ? feedMetadata.validationTimeSeconds : null,
            feedMetadata != null ? feedMetadata.memoryUsageRecords : null,
            feedMetadata != null && feedMetadata.counts != null
                ? new JsonReportCounts(feedMetadata.counts)
                : null,
            feedMetadata == null || feedMetadata.specFeatures == null
                ? null
                : feedMetadata.specFeatures.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .map(FeatureMetadata::getFeatureName)
                    .collect(Collectors.toList()));

    if (config == null) {
      logger.atSevere().log(
          "No validation configuration for JSON report, there will be missing data in the report.");
    }

    if (feedMetadata != null) {
      if (feedMetadata.feedInfo == null) {
        logger.atSevere().log(
            "No feed info for feed "
                + this.gtfsInput
                + ", there will be missing data in the report.");
      }
      if (feedMetadata.counts == null) {
        logger.atSevere().log(
            "Counts are not available for "
                + this.gtfsInput
                + ", there will be missing data in the report.");
      }
    } else {
      logger.atSevere().log(
          "No feed metadata for " + this.gtfsInput + ", there will be missing data in the report.");
    }
  }
}
