/*
 * Copyright 2020 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

@RunWith(JUnit4.class)
public class NoticeAggregateTest {

  private static NoticeAggregate createNoticeAggregate(
      String code,
      SeverityLevel severityLevel,
      int totalNotices,
      List<Map<String, Object>> notices) {
    return new NoticeAggregate(code, severityLevel, totalNotices, notices);
  }

  private static boolean testEquality(
      NoticeAggregate firstNoticeAggregate, NoticeAggregate otherNoticeAggregate) {
    return firstNoticeAggregate.equals(otherNoticeAggregate);
  }

  @Test
  public void equals_sameNotices_true() {
    assertThat(
            testEquality(
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "stops.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build())),
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "stops.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build()))))
        .isTrue();
  }

  @Test
  public void equals_sameNotices_differentOrder_true() {
    assertThat(
            testEquality(
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "stops.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build())),
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("csvRowNumber", 163)
                            .put("filename", "stops.txt")
                            .put("fieldValue", "erroneous url")
                            .put("fieldName", "stop_url")
                            .build()))))
        .isTrue();
  }

  @Test
  public void equals_differentNotices_false() {
    assertThat(
            testEquality(
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "routes.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build())),
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    1,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("csvRowNumber", 163)
                            .put("filename", "stops.txt")
                            .put("fieldValue", "erroneous url")
                            .put("fieldName", "other_url_field")
                            .build()))))
        .isFalse();
  }

  @Test
  public void equals_differentCode_false() {
    assertThat(
            testEquality(
                createNoticeAggregate("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()),
                createNoticeAggregate(
                    "invalid_phone_number", SeverityLevel.ERROR, 2, ImmutableList.of())))
        .isFalse();
  }

  @Test
  public void equals_differentSeverity_false() {
    assertThat(
            testEquality(
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.INFO,
                    2,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "stops.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build())),
                createNoticeAggregate(
                    "invalid_url",
                    SeverityLevel.ERROR,
                    2,
                    ImmutableList.of(
                        new ImmutableMap.Builder()
                            .put("filename", "stops.txt")
                            .put("csvRowNumber", 163)
                            .put("fieldName", "stop_url")
                            .put("fieldValue", "erroneous url")
                            .build()))))
        .isFalse();
  }

  @Test
  public void equals_differentTotalNotices_false() {
    assertThat(
            testEquality(
                createNoticeAggregate("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()),
                createNoticeAggregate("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of())))
        .isFalse();
  }
}
