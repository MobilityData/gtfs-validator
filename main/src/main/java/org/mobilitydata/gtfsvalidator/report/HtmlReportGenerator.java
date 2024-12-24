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

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.report.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.report.model.NoticeView;
import org.mobilitydata.gtfsvalidator.report.model.ReportSummary;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/** HtmlReportGenerator is the class generating the HTML report. */
public class HtmlReportGenerator {

  /** Generate the HTML report using the class ReportSummary and the notice container. */
  public void generateReport(
      FeedMetadata feedMetadata,
      NoticeContainer noticeContainer,
      ValidationRunnerConfig config,
      VersionInfo versionInfo,
      Path reportPath,
      String date,
      boolean is_different_date)
      throws IOException {
    TemplateEngine templateEngine = new TemplateEngine();
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateEngine.setTemplateResolver(templateResolver);

    ReportSummary summary = new ReportSummary(noticeContainer, versionInfo);

    Context context = new Context();
    // TODO: handle null metadata
    context.setVariable("metadata", feedMetadata);
    context.setVariable("summary", summary);
    context.setVariable("config", config);
    context.setVariable("date", date);
    context.setVariable("is_different_date", is_different_date);
    context.setVariable(
        "uniqueFieldsByCode",
        getUniqueFieldsForCodes(
            summary.getNoticesMap().values().stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));

    try (FileWriter writer = new FileWriter(reportPath.toFile())) {
      templateEngine.process("report.html", context, writer);
    }
  }

  private Map<String, List<String>> getUniqueFieldsForCodes(
      Map<String, List<NoticeView>> noticesByCode) {
    return noticesByCode.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, // Notice code
                entry -> {
                  // Find the notice with the most fields
                  return entry.getValue().stream()
                      .max(Comparator.comparingInt(notice -> notice.getFields().size()))
                      .map(NoticeView::getFields) // Extract fields from that notice
                      .orElse(List.of()); // Default to an empty list if no notices
                }));
  }
}
