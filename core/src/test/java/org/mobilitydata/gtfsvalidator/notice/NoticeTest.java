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
            new OtherTestValidationNotice(
                "code2", ImmutableMap.of("key1", "value1"), SeverityLevel.ERROR));

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

  private static class OtherTestValidationNotice extends ValidationNotice {

    private final String code;

    public OtherTestValidationNotice(
        String code, Map<String, Object> context, SeverityLevel severityLevel) {
      super(context, severityLevel);
      this.code = code;
    }

    @Override
    public String getCode() {
      return code;
    }
  }
}
