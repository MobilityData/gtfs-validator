/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.report.model.ReportSummary;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/** HtmlReportGenerator is the class generating the HTML report. */
public class HtmlReportGenerator {

  /** Generate the HTML report using the class ReportSummary and the notice container. */
  public void generateReport(NoticeContainer noticeContainer, Path reportPath) throws IOException {
    TemplateEngine templateEngine = new TemplateEngine();
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateEngine.setTemplateResolver(templateResolver);

    ReportSummary summary = new ReportSummary(noticeContainer);

    Context context = new Context();
    context.setVariable("summary", summary);

    try (FileWriter writer = new FileWriter(reportPath.toFile())) {
      templateEngine.process("report.html", context, writer);
    }
  }
}
