package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * A string field has a value that does not contain mixed case.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 *
 * @see org.mobilitydata.gtfsvalidator.annotation.MixedCase
 */
@GtfsValidationNotice(
    severity = WARNING,
    urls = {
      @UrlRef(
          label = "Best Practices for All Files",
          url =
              "https://gtfs.org/schedule/best-practices/#practice-recommendations-organized-by-file")
    })
public class MixedCaseRecommendedFieldNotice extends ValidationNotice {

  private final String filename;

  private final String fieldName;

  private final String fieldValue;
  private final int csvRowNumber;

  public MixedCaseRecommendedFieldNotice(
      String filename, String fieldName, String fieldValue, int csvRowNumber) {
    super(SeverityLevel.WARNING);
    this.filename = filename;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
    this.csvRowNumber = csvRowNumber;
  }
}
