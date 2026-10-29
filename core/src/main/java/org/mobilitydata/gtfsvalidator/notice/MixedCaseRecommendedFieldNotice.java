package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * This field has customer-facing text and should use Mixed Case (should contain upper and lower
 * case letters).
 *
 * <p>This field contains customer-facing text and should use Mixed Case (upper and lower case
 * letters) to ensure good readability when displayed to riders. The validator tokenizes the text
 * into alphabetical (letter-only) chunks, treating spaces, digits and punctuation as word breaks.
 * If there are chunks present, the notice is raised when no chunks are Mixed Case.
 *
 * <p>Avoid the use of abbreviations throughout the feed (e.g. St. for Street) unless a location is
 * called by its abbreviated name (e.g. “JFK Airport”). Abbreviations may be problematic for
 * accessibility by screen reader software and voice user interfaces. Single all uppercased values
 * are acceptable eg: MAIN while single all lowercased values are not eg: main.
 *
 * <table style="table-layout:auto; width:auto;">
 *   <caption>Good examples:</caption>
 *   <tr>
 *     <th><code>Field Text</code></th>
 *     <th><code>Dataset</code></th>
 *   </tr>
 *   <tr>
 *     <td>"Schwerin, Hauptbahnhof"</td>
 *     <td><a href="http://vbb.de/vbbgtfs">Verkehrsverbund Berlin-Brandenburg</a></td>
 *   </tr>
 *    <tr>
 *     <td>"Red Hook/Atlantic Basin"</td>
 *     <td><a href="http://nycferry.connexionz.net/rtt/public/utility/gtfs.aspx">NYC Ferry</a></td>
 *   </tr>
 *   <tr>
 *     <td>"Campo Grande Norte"</td>
 *     <td><a href="https://gateway.carris.pt/gateway/gtfs/api/v2.8/GTFS">Carris</a></td>
 *   </tr>
 * </table>
 *
 * <table style="table-layout:auto; width:auto;">
 *   <caption>Bad examples:</caption>
 *   <tr>
 *     <th><code>Field Text</code></th>
 *     <th><code>Reason</code></th>
 *   </tr>
 *   <tr>
 *     <td>"GALLERIA mall"</td>
 *     <td>Tokens: [GALLERIA, mall]. Does not contain at least 1 mixed case token</td>
 *   </tr>
 *   <tr>
 *     <td>"3427 GG 17"</td>
 *     <td>Tokens: [GG]. Does not contain at least 1 mixed case token</td>
 *   </tr>
 *   <tr>
 *     <td>"main"</td>
 *     <td>Tokens: [main]. Does not contain at least 1 mixed case token</td>
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
