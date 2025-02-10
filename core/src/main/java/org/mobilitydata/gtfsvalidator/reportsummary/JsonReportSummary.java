package org.mobilitydata.gtfsvalidator.reportsummary;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.annotation.GtfsReportSummary;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsage;

/**
 * Summary of the validation report. Each field in this class needs to be documented with Javadoc.
 */
@GtfsReportSummary
public class JsonReportSummary {
  /**
   * The version of the validator used to produce the report, e.g <a
   * href="https://github.com/MobilityData/gtfs-validator/releases/tag/v6.0.0">6.0</a>.
   */
  public final String validatorVersion;

  /** Datetime of the validation. */
  public final String validatedAt;

  /** File or URL used for the validation. */
  public final String gtfsInput;

  /** Number of threads used for the validation. */
  public final int threads;

  /** Output directory used to save the validation report. */
  public final String outputDirectory;

  /** Filename of the report containing system errors generated during validation. */
  public final String systemErrorsReportName;

  /** Filename of the JSON validation report. */
  public final String validationReportName;

  /** Filename of the HTML validation report */
  public final String htmlReportName;

  /**
   * The <a href="https://www.iso.org/iso-3166-country-codes.html">ISO 3166-1 Alpha 2</a> country
   * code for the region input by a validator user before the report is generated. Specifying the
   * region is optional and used to check the `invalid_phone_number` rule.
   */
  public final String countryCode;

  /** Date of the validation. */
  public final String dateForValidation;

  /** Information about the dataset. */
  public final JsonReportFeedInfo feedInfo;

  /** List of agencies in the feed based on `agency.txt`. */
  public final List<JsonReportAgencyMetadata> agencies;

  /** List of GTFS files in the feed. */
  public final Set<String> files;

  /**
   * The time it took to run the validation in seconds. This value is used internally for
   * performance metrics, and it can change in future versions. <a
   * href="https://github.com/MobilityData/gtfs-validator/pull/1963#issuecomment-2635037575"
   * target="_blank">Example of internal use</a>.
   */
  public final Double validationTimeSeconds;

  /**
   * List of details for the memory usage of the validation. These values are used internally for
   * performance metrics, and it can change in future versions. <a
   * href="https://github.com/MobilityData/gtfs-validator/pull/1963#issuecomment-2635037575"
   * target="_blank">Example of internal use</a>.
   */
  public final List<MemoryUsage> memoryUsageRecords;

  /** Number of entities in the feed. */
  @SerializedName("counts")
  public final JsonReportCounts jsonReportCounts;

  /**
   * List of features in the dataset based on https://gtfs.org/getting-started/features/overview/.
   * You can review how the validator detects features <a
   * href="https://github.com/MobilityData/gtfs-validator/blob/577c2ceba1defea965620dc34af4fc9b26a32450/docs/FEATURES.md">here</a>.
   */
  public final List<String> gtfsFeatures;

  public JsonReportSummary(
      String validatorVersion,
      String validatedAt,
      String gtfsInput,
      int threads,
      String outputDirectory,
      String systemErrorsReportName,
      String validationReportName,
      String htmlReportName,
      String countryCode,
      String dateForValidation,
      JsonReportFeedInfo feedInfo,
      List<JsonReportAgencyMetadata> agencies,
      Set<String> files,
      Double validationTimeSeconds,
      List<MemoryUsage> memoryUsageRecords,
      JsonReportCounts jsonReportCounts,
      List<String> gtfsFeatures) {
    this.validatorVersion = validatorVersion;
    this.validatedAt = validatedAt;
    this.gtfsInput = gtfsInput;
    this.threads = threads;
    this.outputDirectory = outputDirectory;
    this.systemErrorsReportName = systemErrorsReportName;
    this.validationReportName = validationReportName;
    this.htmlReportName = htmlReportName;
    this.countryCode = countryCode;
    this.dateForValidation = dateForValidation;
    this.feedInfo = feedInfo;
    this.agencies = agencies;
    this.files = files;
    this.validationTimeSeconds = validationTimeSeconds;
    this.memoryUsageRecords = memoryUsageRecords;
    this.jsonReportCounts = jsonReportCounts;
    this.gtfsFeatures = gtfsFeatures;
  }
}
