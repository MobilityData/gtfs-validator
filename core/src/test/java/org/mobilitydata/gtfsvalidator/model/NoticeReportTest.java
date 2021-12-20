package org.mobilitydata.gtfsvalidator.model;

/**
 * Copyright 2021 MobilityData IO
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.gson.internal.LinkedTreeMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

@RunWith(JUnit4.class)
public class NoticeReportTest {

  private static NoticeReport createNoticeReport(
      String code,
      SeverityLevel severityLevel,
      int totalNotices,
      List<LinkedTreeMap<String, Object>> notices) {
    return new NoticeReport(code, severityLevel, totalNotices, notices);
  }

  private static LinkedTreeMap<String, Object> invalidUrlNoticeContext = new LinkedTreeMap<>();
  private static LinkedTreeMap<String, Object> otherInvalidUrlNoticeContext = new LinkedTreeMap<>();

  static {
    invalidUrlNoticeContext.put("filename", "stops.txt");
    invalidUrlNoticeContext.put("csvRowNumber", 16);
    invalidUrlNoticeContext.put("fieldName", "stop_url");
    invalidUrlNoticeContext.put("fieldValue", "erroneous url");

    otherInvalidUrlNoticeContext.put("filename", "stops.txt");
    otherInvalidUrlNoticeContext.put("csvRowNumber", 163);
    otherInvalidUrlNoticeContext.put("fieldName", "other_url_field");
    otherInvalidUrlNoticeContext.put("fieldValue", "erroneous url");
  }

  @Test
  public void equals_sameNotices_true() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(invalidUrlNoticeContext)))
        .isEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(invalidUrlNoticeContext)));
  }

  @Test
  public void equals_differentNotices_false() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(invalidUrlNoticeContext)))
        .isNotEqualTo(
            createNoticeReport(
                "invalid_url",
                SeverityLevel.ERROR,
                1,
                ImmutableList.of(otherInvalidUrlNoticeContext)));
  }

  @Test
  public void equals_differentCode_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_phone_number", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }

  @Test
  public void equals_differentSeverity_false() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.INFO, 2, ImmutableList.of(invalidUrlNoticeContext)))
        .isNotEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of(invalidUrlNoticeContext)));
  }

  @Test
  public void equals_differentTotalNotices_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }
}
