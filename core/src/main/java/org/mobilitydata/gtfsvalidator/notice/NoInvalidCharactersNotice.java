package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * This field contains invalid characters such as the replacement character (\uFFFD).
 *
 * <p>Fields with customer-facing text should not contain invalid characters to ensure good
 * readability and accessibility.
 *
 * <table style="table-layout:auto; width:auto;">
 *   <caption>Examples:</caption>
 *   <tr>
 *     <th><code>Field Text</code></th>
 *     <th><code>Dataset</code></th>
 *   </tr>
 *   <tr>
 *     <td>"Invalid�Character"</td>
 *     <td><a href="http://example.com">Example Dataset</a></td>
 *   </tr>
 * </table>
 */
@GtfsValidationNotice(
    severity = WARNING,
    urls = {
      @UrlRef(
          label = "Best Practices for All Files",
          url = "https://gtfs.org/schedule/reference/#file-requirements")
    })
public class NoInvalidCharactersNotice extends ValidationNotice {

  /** Name of the faulty file. */
  private final String filename;

  /** Name of the faulty field. */
  private final String fieldName;

  /** Faulty value. */
  private final String fieldValue;

  /** The row number of the faulty record. */
  private final int csvRowNumber;

  public NoInvalidCharactersNotice(
      String filename, String fieldName, String fieldValue, int csvRowNumber) {
    this.filename = filename;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
    this.csvRowNumber = csvRowNumber;
  }
}
