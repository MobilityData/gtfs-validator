/*
 * Copyright 2022 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.report;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;
import org.mobilitydata.gtfsvalidator.model.ValidationReport;
import org.mobilitydata.gtfsvalidator.report.model.AgencyMetadata;
import org.mobilitydata.gtfsvalidator.report.model.ReportData;

/** HtmlReportGenerator is the class generating the HTML report. */
public class JsonReportGenerator {

  /** Generate the HTML report using the class ReportSummary and the notice container. */
  public String generateReport(Gson gson, ReportData reportData) throws IOException {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    Date now = new Date(System.currentTimeMillis());
    String date = formatter.format(now);

    ValidationReport validationReport =
        reportData.noticeContainer.createValidationReport(
            reportData.noticeContainer.getResolvedValidationNotices());

    JsonReport jsonReport = new JsonReport();
    jsonReport.notices = validationReport.getNotices();
    jsonReport.summary =
        new Summary(
            reportData.reportSummary.getVersion(),
            date,
            reportData.config.gtfsSource().toString(),
            reportData.config.numThreads(), // Not sure how to get this info
            reportData.config.outputDirectory().toString(),
            reportData.config.systemErrorsReportFileName(),
            reportData.config.validationReportFileName(),
            reportData.config.htmlReportFileName(),
            reportData.config.countryCode().getCountryCode(),
            reportData.feedMetadata == null ? null : reportData.feedMetadata.feedInfo,
                reportData.feedMetadata == null ? null : reportData.feedMetadata.agencies,
                reportData.feedMetadata == null ? null : reportData.feedMetadata.getFilenames(),
                reportData.feedMetadata == null ? null : reportData.feedMetadata.counts,
                reportData.feedMetadata == null ? null : reportData.feedMetadata.specFeatures);

    return gson.toJson(jsonReport);
  }

  public static class JsonReport {
    public Summary summary;

    public Set<NoticeReport> notices;
  }

  public static class Summary {
    public Summary(
        String validatorVersion,
        String validatedAt,
        String gtfsInput,
        int threads,
        String outputDirectory,
        String systemErrorsReportName,
        String validationReportName,
        String htmlReportName,
        String countryCode,
        Map<String, String> feedInfo,
        List<AgencyMetadata> agencies,
        Set<String> files,
        Map<String, Integer> counts,
        Map<String, Boolean> gtfsComponentsMap) {
      this.validatorVersion = validatorVersion;
      this.validatedAt = validatedAt;
      this.gtfsInput = gtfsInput;
      this.threads = threads;
      this.outputDirectory = outputDirectory;
      this.systemErrorsReportName = systemErrorsReportName;
      this.validationReportName = validationReportName;
      this.htmlReportName = htmlReportName;
      this.countryCode = countryCode;
      this.feedInfo = feedInfo;
      this.agencies = agencies;
      this.files = files;
      this.counts = counts;
      this.gtfsComponents =
              gtfsComponentsMap == null ? null : gtfsComponentsMap.entrySet().stream()
              .filter(Map.Entry::getValue)
              .map(Map.Entry::getKey)
              .collect(Collectors.toList());
    }

    String validatorVersion;
    String validatedAt;
    String gtfsInput;

    int threads;

    String outputDirectory;

    String systemErrorsReportName;
    String validationReportName;
    String htmlReportName;
    String countryCode;

    Map<String, String> feedInfo;

    List<AgencyMetadata> agencies;

    Set<String> files;

    Map<String, Integer> counts;

    List<String> gtfsComponents;
  }
}
