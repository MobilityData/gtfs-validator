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

  @Test
  public void equals_sameNotices_true() {
    LinkedTreeMap<String, Object> noticeMap = new LinkedTreeMap();
    noticeMap.put("filename", "stops.txt");
    noticeMap.put("csvRowNumber", 163);
    noticeMap.put("fieldName", "stop_url");
    noticeMap.put("fieldValue", "erroneous url");

    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMap)))
        .isEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMap)));
  }

  @Test
  public void equals_sameNotices_differentOrder_true() {
    LinkedTreeMap<String, Object> firstMap = new LinkedTreeMap();
    firstMap.put("filename", "stops.txt");
    firstMap.put("csvRowNumber", 163);
    firstMap.put("fieldName", "stop_url");
    firstMap.put("fieldValue", "erroneous url");

    LinkedTreeMap<String, Object> otherMap = new LinkedTreeMap();
    otherMap.put("filename", "stops.txt");
    otherMap.put("csvRowNumber", 163);
    otherMap.put("fieldValue", "erroneous url");
    otherMap.put("fieldName", "stop_url");

    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(firstMap)))
        .isEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(otherMap)));
  }

  @Test
  public void equals_differentNotices_false() {
    LinkedTreeMap<String, Object> firstMap = new LinkedTreeMap();
    firstMap.put("filename", "stops.txt");
    firstMap.put("csvRowNumber", 163);
    firstMap.put("fieldName", "stop_url");
    firstMap.put("fieldValue", "erroneous url");

    LinkedTreeMap<String, Object> otherMap = new LinkedTreeMap();
    otherMap.put("filename", "stops.txt");
    otherMap.put("csvRowNumber", 163);
    otherMap.put("fieldValue", "erroneous url");
    otherMap.put("fieldName", "other_url_field");
    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(firstMap)))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(otherMap)));
  }

  @Test
  public void equals_differentCode_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_phone_number", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }

  @Test
  public void equals_differentSeverity_false() {
    LinkedTreeMap<String, Object> noticeMap = new LinkedTreeMap();
    noticeMap.put("filename", "stops.txt");
    noticeMap.put("csvRowNumber", 163);
    noticeMap.put("fieldName", "stop_url");
    noticeMap.put("fieldValue", "erroneous url");

    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.INFO, 2, ImmutableList.of(noticeMap)))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of(noticeMap)));
  }

  @Test
  public void equals_differentTotalNotices_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }
}
