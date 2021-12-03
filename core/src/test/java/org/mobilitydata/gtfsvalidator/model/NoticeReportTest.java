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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.units.qual.A;
import org.junit.Rule;
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

  private static List<LinkedTreeMap<String, Object>> createNoticeMaps(){
    List<LinkedTreeMap<String, Object>> toReturn = new ArrayList<>();
    LinkedTreeMap<String, Object> firstMap = new LinkedTreeMap();
    firstMap.put("filename", "stops.txt");
    firstMap.put("csvRowNumber", 163);
    firstMap.put("fieldName", "stop_url");
    firstMap.put("fieldValue", "erroneous url");
    toReturn.add(firstMap);

    LinkedTreeMap<String, Object> secondMap = new LinkedTreeMap();
    secondMap.put("filename", "stops.txt");
    secondMap.put("csvRowNumber", 163);
    secondMap.put("fieldName", "stop_url");
    secondMap.put("fieldValue", "erroneous url");
    toReturn.add(secondMap);

    LinkedTreeMap<String, Object> thirdMap = new LinkedTreeMap();
    thirdMap.put("filename", "stops.txt");
    thirdMap.put("csvRowNumber", 163);
    thirdMap.put("fieldName", "other_url_field");
    thirdMap.put("fieldValue", "erroneous url");
    toReturn.add(thirdMap);

    return toReturn;
  }

  private static final List<LinkedTreeMap<String, Object>> noticeMaps = createNoticeMaps();

  @Test
  public void equals_sameNotices_true() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(0))))
        .isEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(1))));
  }

  @Test
  public void equals_sameNotices_differentOrder_true() {
    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(0))))
        .isEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(1))));
  }

  @Test
  public void equals_differentNotices_false() {
    assertThat(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(0))))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(noticeMaps.get(2))));
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
            createNoticeReport("invalid_url", SeverityLevel.INFO, 2, ImmutableList.of(noticeMaps.get(0))))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of(noticeMaps.get(0))));
  }

  @Test
  public void equals_differentTotalNotices_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }
}
