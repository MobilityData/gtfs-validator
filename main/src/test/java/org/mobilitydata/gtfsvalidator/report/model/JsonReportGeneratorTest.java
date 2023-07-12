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

package org.mobilitydata.gtfsvalidator.report.model;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;
import org.mobilitydata.gtfsvalidator.report.JsonReport;
import org.mobilitydata.gtfsvalidator.report.JsonReportGenerator;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class JsonReportGeneratorTest {
  private static final String MISSING_REQUIRED_FIELD_NOTICE_CODE = "missing_required_field";
  private static final String NON_ASCII_OR_NON_PRINTABLE_CHAR_CODE =
      "non_ascii_or_non_printable_char";
  private static final String UNKNOWN_COLUMN_NOTICE_CODE = "unknown_column";

  private static JsonReport generateJsonReport() throws IOException {
    JsonReportGenerator reportGenerator = new JsonReportGenerator();

    NoticeContainer noticeContainer = new NoticeContainer();
    noticeContainer.addValidationNotice(new MissingRequiredFieldNotice("test.txt", 1, "field"));
    noticeContainer.addValidationNotice(
        new MissingRequiredFieldNotice("test.txt", 3, "anotherField"));
    noticeContainer.addValidationNotice(
        new NonAsciiOrNonPrintableCharNotice("test.txt", 1, "column", "value"));
    noticeContainer.addValidationNotice(new UnknownColumnNotice("test.txt", "unknown", 2));

    ValidationRunnerConfig config = ValidationRunnerConfig.builder().build();

    JsonReport report =
        reportGenerator.generateReport(null, noticeContainer, config, VersionInfo.empty(), "");

    return report;
  }

  @Test
  public void noticeReportsTest() throws IOException {
    //   JsonReport report = generateJsonReport();
  }

  //  @Test
  //  public void totalNoticesCountTest() {
  //    assertEquals(generateJsonReport().getNoticeCount(), 4);
  //  }
  //
  //  @Test
  //  public void errorNoticesCountTest() {
  //    assertEquals(generateJsonReport().getErrorCount(), 2);
  //  }
  //
  //  @Test
  //  public void warningNoticesCountTest() {
  //    assertEquals(generateJsonReport().getWarningCount(), 1);
  //  }
  //
  //  @Test
  //  public void infoNoticesCountTest() {
  //    assertEquals(generateJsonReport().getInfoCount(), 1);
  //  }
  //
  //  @Test
  //  public void noticesMapTest() {
  //    assertEquals(generateJsonReport().getNoticesMap().size(), 3);
  //    assertEquals(generateJsonReport().getNoticesMap().get(SeverityLevel.ERROR).size(), 1);
  //    assertEquals(
  //        generateJsonReport()
  //            .getNoticesMap()
  //            .get(SeverityLevel.ERROR)
  //            .get(MISSING_REQUIRED_FIELD_NOTICE_CODE)
  //            .size(),
  //        2);
  //    assertEquals(generateJsonReport().getNoticesMap().get(SeverityLevel.WARNING).size(), 1);
  //    assertEquals(
  //        generateJsonReport()
  //            .getNoticesMap()
  //            .get(SeverityLevel.WARNING)
  //            .get(NON_ASCII_OR_NON_PRINTABLE_CHAR_CODE)
  //            .size(),
  //        1);
  //    assertEquals(generateJsonReport().getNoticesMap().get(SeverityLevel.INFO).size(), 1);
  //    assertEquals(
  //        generateJsonReport()
  //            .getNoticesMap()
  //            .get(SeverityLevel.INFO)
  //            .get(UNKNOWN_COLUMN_NOTICE_CODE)
  //            .size(),
  //        1);
  //  }
  //
  //  @Test
  //  public void testVersionPresent() {
  //    VersionInfo versionInfo = VersionInfo.create(Optional.of("1.2.3"), Optional.of("1.2.4"));
  //    ReportSummary reportSummary = new ReportSummary(new NoticeContainer(), versionInfo);
  //
  //    assertEquals("1.2.3", reportSummary.getVersion());
  //    assertTrue(reportSummary.isNewVersionOfValidatorAvailable());
  //  }
  //
  //  @Test
  //  public void testVersionMissing() {
  //    VersionInfo versionInfo = VersionInfo.empty();
  //    ReportSummary reportSummary = new ReportSummary(new NoticeContainer(), versionInfo);
  //
  //    assertNull(reportSummary.getVersion());
  //    assertFalse(reportSummary.isNewVersionOfValidatorAvailable());
  //  }
}
