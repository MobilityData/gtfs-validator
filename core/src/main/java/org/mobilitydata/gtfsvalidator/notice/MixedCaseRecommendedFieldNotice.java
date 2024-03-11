package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * This field has customer-facing text and should use Mixed Case (should contain upper and lower
 * case letters).
 *
 * <p>This field contains customer-facing text and should use Mixed Case (upper and lower case
 * letters) to ensure good readability when displayed to riders. Avoid the use of abbreviations
 * throughout the feed (e.g. St. for Street) unless a location is called by its abbreviated name
 * (e.g. “JFK Airport”). Abbreviations may be problematic for accessibility by screen reader
 * software and voice user interfaces.
 *
 * <p>Good examples:
 *    <pre>
 *      | `Field Text`/`Dataset`                                                                | Dataset                                                                                                                                                                |
 *      | ----------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
 *      | ["Schwerin, Hauptbahnhof"]               | [Verkehrsverbund Berlin-Brandenburg](http://vbb.de/vbbgtfs) |
 *      | ["Red Hook/Atlantic Basin"](http://nycferry.connexionz.net/rtt/public/utility/gtfs.aspx) |                                                                                                                           |
 *      | ["Campo Grande Norte"] | [Carris](https://gateway.carris.pt/gateway/gtfs/api/v2.8/GTFS) |
 *    </pre>
 *
 *    Bad examples:
 *    <pre>
 *      | `Field Text` |
 *      |-------------------------------------------|
 *      | "GALLERIA MALl"                           |
 *      | "3427 GG 17"                           |
 *      | "21 Clark Rd Est"      |
 *     </pre>
 */

@GtfsValidationNotice(
    severity = WARNING,
    urls = {
      @UrlRef(
          label = "Best Practices for All Files",
          url = "https://gtfs.org/schedule/reference/#file-requirements")
    })
public class MixedCaseRecommendedFieldNotice extends ValidationNotice {

  /** Name of the faulty file. */
  private final String filename;

  /** Name of the faulty field. */
  private final String fieldName;

  /** Faulty value. */
  private final String fieldValue;

  /** The row number of the faulty record. */
  private final int csvRowNumber;

  public MixedCaseRecommendedFieldNotice(
      String filename, String fieldName, String fieldValue, int csvRowNumber) {
    this.filename = filename;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
    this.csvRowNumber = csvRowNumber;
  }
}
