package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;

/**
 * This field has customer-facing text and should use Mixed Case (should contain upper and lower
 * case letters).
 *
 * <p>This field contains customer-facing text and should use Mixed Case (upper and lower case
 * letters) to ensure good readability when displayed to riders. The validator splits the text into
 * letter-only tokens, treating spaces, digits and punctuation as separators, and ignores
 * single-letter tokens.
 *
 * <pre>
 * - If there is only one token, the notice is raised only if it is all lowercase (e.g. `main`).
 *   All uppercase single tokens are accepted (e.g. `MAIN`).
 * - If there are two or more tokens, the notice is raised if none of them are Mixed Case
 * </pre>
 *
 * <p>Avoid the use of abbreviations throughout the feed (e.g. St. for Street) unless a location is
 * called by its abbreviated name (e.g. “JFK Airport”). Abbreviations may be problematic for
 * accessibility by screen reader software and voice user interfaces.
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
 *     <td>"main"</td>
 *     <td>Tokens: [main]. Contains a single all-lowercase token</td>
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
