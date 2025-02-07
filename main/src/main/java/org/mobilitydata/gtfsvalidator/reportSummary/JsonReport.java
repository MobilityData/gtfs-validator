package org.mobilitydata.gtfsvalidator.reportSummary;

import java.util.Set;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;

/**
 * Used to generate the json report using the built-in class serialization of GSON. Fields names
 * used here will be the same as in the generated json file.
 */
@SuppressWarnings("unused") // The fields of this class are only read by Gson when serializing.
public class JsonReport {

  private final JsonReportSummary summary;

  private final Set<NoticeReport> notices;

  public JsonReport(JsonReportSummaryGenerator generator, Set<NoticeReport> notices) {
    this.summary = generator.summary;
    this.notices = notices;
  }
}
