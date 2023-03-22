/*
 * Copyright 2020 Google LLC
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
package org.mobilitydata.gtfsvalidator.outputcomparator.io;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.collect.ImmutableList;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.model.NoticeReport;

public class NoticeSetTest {

  private static final List<LinkedTreeMap<String, Object>> SAMPLES = Collections.emptyList();

  @Test
  public void hasSameErrorCodes_sameErrorsInReports_true() {
    NoticeSet lhs =
        new NoticeSet(
            ImmutableList.of(
                new NoticeReport("invalid_url", ERROR, 1, SAMPLES),
                new NoticeReport("unknown_column", ERROR, 1, SAMPLES)));
    NoticeSet rhs =
        new NoticeSet(
            ImmutableList.of(
                new NoticeReport("invalid_url", ERROR, 2, SAMPLES),
                new NoticeReport("unknown_column", ERROR, 3, SAMPLES)));
    assertThat(lhs.hasSameNoticeCodes(rhs)).isTrue();
  }

  @Test
  public void hasSameErrorCodes_differentErrorsInReport_false() {
    NoticeSet lhs =
        new NoticeSet(
            ImmutableList.of(
                new NoticeReport("invalid_url", ERROR, 1, SAMPLES),
                new NoticeReport("unknown_column", ERROR, 1, SAMPLES)));
    NoticeSet rhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("invalid_url", ERROR, 2, SAMPLES)));
    assertThat(lhs.hasSameNoticeCodes(rhs)).isFalse();
  }

  @Test
  public void newErrorCount_sameErrorsInReports_zero() {
    NoticeSet lhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("invalid_url", ERROR, 2, SAMPLES)));
    NoticeSet rhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("invalid_url", ERROR, 1, SAMPLES)));
    assertThat(lhs.getNewNotices(rhs)).isEmpty();
  }

  @Test
  public void newErrorCount_noNewErrorInReport_zero() {
    NoticeSet lhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("invalid_url", ERROR, 2, SAMPLES)));
    NoticeSet rhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("some_error_code", ERROR, 1, SAMPLES)));
    assertThat(lhs.getNewNotices(rhs)).containsExactly("some_error_code");
  }

  @Test
  public void newErrorCount_twoNewErrorsInNewReport_two() {
    NoticeSet lhs =
        new NoticeSet(ImmutableList.of(new NoticeReport("invalid_url", ERROR, 2, SAMPLES)));
    NoticeSet rhs =
        new NoticeSet(
            ImmutableList.of(
                new NoticeReport("invalid_url", ERROR, 1, SAMPLES),
                new NoticeReport("invalid_url", ERROR, 1, SAMPLES),
                new NoticeReport("some_error_code", ERROR, 1, SAMPLES),
                new NoticeReport("another_error_code", ERROR, 1, SAMPLES)));

    assertThat(lhs.getNewNotices(rhs)).containsExactly("some_error_code", "another_error_code");
  }
}
