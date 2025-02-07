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

package org.mobilitydata.gtfsvalidator.reportSummary.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.UnknownColumnNotice;
import org.mobilitydata.gtfsvalidator.util.VersionInfo;

@RunWith(JUnit4.class)
public class ReportSummaryTest {
  private static final String MISSING_REQUIRED_FIELD_NOTICE_CODE = "missing_required_field";
  private static final String NON_ASCII_OR_NON_PRINTABLE_CHAR_CODE =
      "non_ascii_or_non_printable_char";
  private static final String UNKNOWN_COLUMN_NOTICE_CODE = "unknown_column";

  private static ReportSummary generateReportSummary() {
    NoticeContainer noticeContainer = new NoticeContainer();
    noticeContainer.addValidationNotice(new MissingRequiredFieldNotice("test.txt", 1, "field"));
    noticeContainer.addValidationNotice(
        new MissingRequiredFieldNotice("test.txt", 3, "anotherField"));
    noticeContainer.addValidationNotice(
        new NonAsciiOrNonPrintableCharNotice("test.txt", 1, "column", "value"));
    noticeContainer.addValidationNotice(new UnknownColumnNotice("test.txt", "unknown", 2));
    ReportSummary reportSummary = new ReportSummary(noticeContainer, VersionInfo.empty());
    return reportSummary;
  }

  @Test
  public void totalNoticesCountTest() {
    assertEquals(generateReportSummary().getNoticeCount(), 4);
  }

  @Test
  public void errorNoticesCountTest() {
    assertEquals(generateReportSummary().getErrorCount(), 2);
  }

  @Test
  public void warningNoticesCountTest() {
    assertEquals(generateReportSummary().getWarningCount(), 1);
  }

  @Test
  public void infoNoticesCountTest() {
    assertEquals(generateReportSummary().getInfoCount(), 1);
  }

  @Test
  public void noticesMapTest() {
    assertEquals(generateReportSummary().getNoticesMap().size(), 3);
    assertEquals(generateReportSummary().getNoticesMap().get(SeverityLevel.ERROR).size(), 1);
    assertEquals(
        generateReportSummary()
            .getNoticesMap()
            .get(SeverityLevel.ERROR)
            .get(MISSING_REQUIRED_FIELD_NOTICE_CODE)
            .size(),
        2);
    assertEquals(generateReportSummary().getNoticesMap().get(SeverityLevel.WARNING).size(), 1);
    assertEquals(
        generateReportSummary()
            .getNoticesMap()
            .get(SeverityLevel.WARNING)
            .get(NON_ASCII_OR_NON_PRINTABLE_CHAR_CODE)
            .size(),
        1);
    assertEquals(generateReportSummary().getNoticesMap().get(SeverityLevel.INFO).size(), 1);
    assertEquals(
        generateReportSummary()
            .getNoticesMap()
            .get(SeverityLevel.INFO)
            .get(UNKNOWN_COLUMN_NOTICE_CODE)
            .size(),
        1);
  }

  @Test
  public void testVersionPresent() {
    VersionInfo versionInfo = VersionInfo.create(Optional.of("1.2.3"), Optional.of("1.2.4"));
    ReportSummary reportSummary = new ReportSummary(new NoticeContainer(), versionInfo);

    assertEquals("1.2.3", reportSummary.getVersion());
    assertTrue(reportSummary.isNewVersionOfValidatorAvailable());
  }

  @Test
  public void testVersionMissing() {
    VersionInfo versionInfo = VersionInfo.empty();
    ReportSummary reportSummary = new ReportSummary(new NoticeContainer(), versionInfo);

    assertNull(reportSummary.getVersion());
    assertFalse(reportSummary.isNewVersionOfValidatorAvailable());
  }
}
