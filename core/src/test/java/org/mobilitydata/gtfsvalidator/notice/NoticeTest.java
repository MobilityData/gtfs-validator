package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NoticeTest {

  @Test
  public void noticeEquals() {
    assertThat(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR))
        .isEqualTo(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR));

    // Different notices
    assertThat(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR))
        .isNotEqualTo(
            new OtherTestValidationNotice(ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR));

    // Different context.
    assertThat(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR))
        .isNotEqualTo(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value2"), SeverityLevel.ERROR));

    // Different severity.
    assertThat(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR))
        .isNotEqualTo(
            new TestValidationNotice(
                "code1", ImmutableMap.of("key1", "value1"), SeverityLevel.INFO));
  }

  @Test
  public void getCode() {
    assertThat(
            new OtherTestValidationNotice(ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR)
                .getCode())
        .matches("other_test_validation");
  }

  private static class OtherTestValidationNotice extends ValidationNotice {

    public OtherTestValidationNotice(Map<String, Object> context, SeverityLevel severityLevel) {
      super(context, severityLevel);
    }
  }
}
