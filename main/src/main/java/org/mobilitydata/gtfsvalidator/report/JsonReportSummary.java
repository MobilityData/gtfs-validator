package org.mobilitydata.gtfsvalidator.report;

import java.text.SimpleDateFormat;
import java.util.Date;
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

  private final String validatorVersion;
  private final String validatedAt;
  private final String gtfsInput;

  private final int threads;

  private final String outputDirectory;

  private final String systemErrorsReportName;
  private final String validationReportName;
  private final String htmlReportName;
  private final String countryCode;

  private Map<String, String> feedInfo;

  private List<AgencyMetadata> agencies;

  private Set<String> files;

  private Map<String, Integer> counts;

  private List<String> gtfsComponents;

  public JsonReportSummary(
      FeedMetadata feedMetadata, ValidationRunnerConfig config, VersionInfo versionInfo) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    Date now = new Date(System.currentTimeMillis());
    this.validatedAt = formatter.format(now);

    this.validatorVersion = versionInfo.currentVersion().orElse(null);

    this.gtfsInput = config.gtfsSource().toString();
    this.threads = config.numThreads();
    this.outputDirectory = config.outputDirectory().toString();
    this.systemErrorsReportName = config.systemErrorsReportFileName();
    this.validationReportName = config.validationReportFileName();
    this.htmlReportName = config.htmlReportFileName();
    this.countryCode = config.countryCode().getCountryCode();

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
