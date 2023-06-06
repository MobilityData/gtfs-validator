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
import com.google.gson.JsonElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

@RunWith(JUnit4.class)
public class NoticeReportTest {

  private static NoticeReport createNoticeReport(
      String code, SeverityLevel severityLevel, int totalNotices, ImmutableList<Notice> notices) {
    ImmutableList<JsonElement> sampleNotices =
        notices.stream().map(Notice::toJsonTree).collect(ImmutableList.toImmutableList());
    return new NoticeReport(code, severityLevel, totalNotices, sampleNotices);
  }

  private static final Notice INVALID_URL_NOTICE =
      new InvalidUrlNotice("stops.txt", 16, "stop_url", "erroneous url");
  private static final Notice OTHER_INVALID_URL_NOTICE =
      new InvalidUrlNotice("stops.txt", 163, "other_url_field", "erroneous url");

  @Test
  public void equals_sameNotices_true() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(INVALID_URL_NOTICE)))
        .isEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(INVALID_URL_NOTICE)));
  }

  @Test
  public void equals_differentNotices_false() {
    assertThat(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(INVALID_URL_NOTICE)))
        .isNotEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of(OTHER_INVALID_URL_NOTICE)));
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
                "invalid_url", SeverityLevel.INFO, 2, ImmutableList.of(INVALID_URL_NOTICE)))
        .isNotEqualTo(
            createNoticeReport(
                "invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of(INVALID_URL_NOTICE)));
  }

  @Test
  public void equals_differentTotalNotices_false() {
    assertThat(createNoticeReport("invalid_url", SeverityLevel.ERROR, 1, ImmutableList.of()))
        .isNotEqualTo(
            createNoticeReport("invalid_url", SeverityLevel.ERROR, 2, ImmutableList.of()));
  }
}
