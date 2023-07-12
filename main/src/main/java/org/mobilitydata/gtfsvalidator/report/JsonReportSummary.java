package org.mobilitydata.gtfsvalidator.report;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.report.model.AgencyMetadata;
import org.mobilitydata.gtfsvalidator.report.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

/**
 * Used to generate the summary of json report using the built-in class serialization of GSON.
 * Fields names used here will be the same as in the generated json file.
 */
@SuppressWarnings("unused") // The fields of this class are only read by Gson when serializing.
public class JsonReportSummary {

  private String validatorVersion;
  private String validatedAt;
  private String gtfsInput;

  private int threads;

  private String outputDirectory;

  private String systemErrorsReportName;
  private String validationReportName;
  private String htmlReportName;
  private String countryCode;

  private Map<String, String> feedInfo;

  private List<AgencyMetadata> agencies;

  private Set<String> files;

  private Map<String, Integer> counts;

  private List<String> gtfsComponents;

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
    }

    if (feedMetadata != null) {
      this.feedInfo = feedMetadata.feedInfo;
      this.agencies = feedMetadata.agencies;
      this.files = feedMetadata.getFilenames();
      this.counts = feedMetadata.counts;
      this.gtfsComponents =
          feedMetadata.specFeatures == null
              ? null
              : feedMetadata.specFeatures.entrySet().stream()
                  .filter(Map.Entry::getValue)
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toList());
    }
  }
}
