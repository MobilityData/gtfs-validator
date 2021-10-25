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

package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DoubleFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.StringFieldNotice;

@RunWith(JUnit4.class)
public class NoticeContainerTest {

  @Test
  public void exportNotices() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    container.addValidationNotice(new MissingRequiredFileNotice("agency.txt"));
    container.addSystemError(
        new RuntimeExceptionInValidatorError(
            "FaultyValidator", new IndexOutOfBoundsException("Index 0 out of bounds")));

    assertThat(new Gson().toJson(container.exportValidationNotices()))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"missing_required_file\",\"severity\":\"ERROR\","
                + "\"totalNotices\":2,\"sampleNotices\":[{\"filename\":\"stops.txt"
                + "\"},{\"filename\":\"agency.txt\"}]}]}");
    assertThat(new Gson().toJson(container.exportSystemErrors()))
        .isEqualTo(
            ""
                + "{\"notices\":[{\"code\":\"runtime_exception_in_validator_error\",\"severity\":"
                + "\"ERROR\",\"totalNotices\":1,\"sampleNotices\":[{\"validator\":"
                + "\"FaultyValidator\",\"exception\":\"java.lang.IndexOutOfBoundsException\","
                + "\"message\":\"Index 0 out of bounds\"}]}]}");
  }

  @Test
  public void exportInfinityInContext() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(
        new DoubleFieldNotice(Double.POSITIVE_INFINITY, SeverityLevel.ERROR));
    assertThat(new Gson().toJson(container.exportValidationNotices()))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"double_field\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"sampleNotices\":[{\"doubleField\":Infinity}]}]}");
  }

  @Test
  public void exportSeverities() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(new StringFieldNotice("1", SeverityLevel.ERROR));
    container.addValidationNotice(new DoubleFieldNotice(2.0, SeverityLevel.ERROR));
    container.addValidationNotice(new StringFieldNotice("3", SeverityLevel.INFO));

    assertThat(new Gson().toJson(container.exportValidationNotices()))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"double_field\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"sampleNotices\":[{\"doubleField\":2.0}]},{\"code\":"
                + "\"string_field\",\"severity\":\"INFO\",\"totalNotices\":1,\"sampleNotices\":[{"
                + "\"someField\":\"3\"}]},{\"code\":\"string_field\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"sampleNotices\":[{\"someField\":\"1\"}]}]}");
  }

  @Test
  public void addAll() {
    ValidationNotice n1 = new MissingRequiredFileNotice("stops.txt");
    ValidationNotice n2 = new UnknownFileNotice("unknown.txt");
    SystemError e1 =
        new RuntimeExceptionInValidatorError(
            "Validator1", new IndexOutOfBoundsException("Index 0 out of bounds"));
    SystemError e2 =
        new RuntimeExceptionInValidatorError(
            "Validator2", new NegativeArraySizeException("Index -1 out of bounds"));
    NoticeContainer c1 = new NoticeContainer();
    c1.addValidationNotice(n1);
    c1.addSystemError(e1);
    NoticeContainer c2 = new NoticeContainer();
    c2.addValidationNotice(n2);
    c2.addSystemError(e2);
    c1.addAll(c2);

    Map<String, Integer> noticeCount = new HashMap<>();
    noticeCount.put(n1.getMappingKey(), 1);
    noticeCount.put(n2.getMappingKey(), 1);
    noticeCount.put(e1.getMappingKey(), 1);
    noticeCount.put(e2.getMappingKey(), 1);
    assertThat(c1.getValidationNotices()).containsExactly(n1, n2);
    assertThat(c1.getSystemErrors()).containsExactly(e1, e2);
    assertThat(c1.getNoticesCountPerTypeAndSeverity()).containsExactlyEntriesIn(noticeCount);
  }

  @Test
  public void addValidationNotice_setMax_perNoticeTypeAndSeverity() {
    ValidationNotice n1 = new DoubleFieldNotice(2.0, SeverityLevel.WARNING);
    ValidationNotice n2 = new DoubleFieldNotice(2.0, SeverityLevel.ERROR);
    int MAX_TOTAL_VALIDATION_NOTICES = 50;
    int MAX_PER_NOTICE_TYPE_AND_SEVERITY = 15;
    int MAX_EXPORT_PER_NOTICE_TYPE_AND_SEVERITY = 15;
    NoticeContainer noticeContainer =
        new NoticeContainer(
            MAX_TOTAL_VALIDATION_NOTICES,
            MAX_PER_NOTICE_TYPE_AND_SEVERITY,
            MAX_EXPORT_PER_NOTICE_TYPE_AND_SEVERITY);
    for (int i = 0; i < MAX_TOTAL_VALIDATION_NOTICES + 5; i++) {
      noticeContainer.addValidationNotice(n1);
      noticeContainer.addValidationNotice(n2);
    }
    assertThat(noticeContainer.getValidationNotices().size())
        .isEqualTo(2 * MAX_PER_NOTICE_TYPE_AND_SEVERITY);
    assertThat(
            NoticeContainer.groupNoticesByTypeAndSeverity(noticeContainer.getValidationNotices())
                .get(n1.getMappingKey())
                .size())
        .isEqualTo(MAX_PER_NOTICE_TYPE_AND_SEVERITY);
    assertThat(
            NoticeContainer.groupNoticesByTypeAndSeverity(noticeContainer.getValidationNotices())
                .get(n2.getMappingKey())
                .size())
        .isEqualTo(MAX_PER_NOTICE_TYPE_AND_SEVERITY);
  }

  @Test
  public void addValidationNotice_setMaxTotalValidationNotices() {
    ValidationNotice n1 = new MissingRequiredFileNotice("stops.txt");
    ValidationNotice n2 = new UnknownFileNotice("unknown.txt");
    int MAX_TOTAL_VALIDATION_NOTICES = 30;
    int MAX_VALIDATION_NOTICE_PER_TYPE_AND_SEVERITY = 16;
    int MAX_EXPORT_PER_NOTICE_TYPE_AND_SEVERITY = 16;
    NoticeContainer noticeContainer =
        new NoticeContainer(
            MAX_TOTAL_VALIDATION_NOTICES,
            MAX_VALIDATION_NOTICE_PER_TYPE_AND_SEVERITY,
            MAX_EXPORT_PER_NOTICE_TYPE_AND_SEVERITY);
    for (int i = 0; i < MAX_TOTAL_VALIDATION_NOTICES + 5; i++) {
      noticeContainer.addValidationNotice(n1);
      noticeContainer.addValidationNotice(n2);
    }
    assertThat(noticeContainer.getValidationNotices().size())
        .isEqualTo(MAX_TOTAL_VALIDATION_NOTICES);
  }

  @Test
  public void exportNotices_shouldReflectTheTotalNumberOfNoticesAndSampleNotices() {
    NoticeContainer container = new NoticeContainer(26, 8, 3);
    for (int i = 0; i < 55; i++) {
      container.addValidationNotice(new StringFieldNotice("1", SeverityLevel.ERROR));
      container.addValidationNotice(new DoubleFieldNotice(2.0, SeverityLevel.ERROR));
      container.addValidationNotice(new StringFieldNotice("3", SeverityLevel.INFO));
    }
    assertThat(new Gson().toJson(container.exportValidationNotices()))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"double_field\",\"severity\":\"ERROR\","
                + "\"totalNotices\":55,\"sampleNotices\":[{\"doubleField\":2.0},{\"doubleField"
                + "\":2.0},{\"doubleField\":2.0}]},{\"code\":\"string_field\",\"severity\":\"INFO"
                + "\",\"totalNotices\":55,\"sampleNotices\":[{\"someField\":\"3\"},{\"someField\":"
                + "\"3\"},{\"someField\":\"3\"}]},{\"code\":\"string_field\",\"severity\":\"ERROR"
                + "\",\"totalNotices\":55,\"sampleNotices\":[{\"someField\":\"1\"},{\"someField"
                + "\":\"1\"},{\"someField\":\"1\"}]}]}");
  }
}
