package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NoticeTest {
  @Test
  public void equalsOtherNotice() {
    assertThat(new TestValidationNotice("code_one", ImmutableMap.of("field", "value")))
        .isEqualTo(new TestValidationNotice("code_one", ImmutableMap.of("field", "value")));
    assertThat(new TestValidationNotice("code_one", ImmutableMap.of("field", "value")))
        .isNotEqualTo(new TestValidationNotice("code_two", ImmutableMap.of("field", "value")));
    assertThat(new TestValidationNotice("code_one", ImmutableMap.of("field", "value")))
        .isNotEqualTo(new TestValidationNotice("code_one", ImmutableMap.of("field2", "value2")));
  }

  @Test
  public void nullInContext() {
    // Use HashMap because ImmutableMap does not support nulls.
    Map<String, Object> context = new HashMap<>();
    context.put("nullField", null);
    Notice notice = new TestValidationNotice("test_notice", context);
    // toString() must not crash.
    assertThat(notice.toString()).isEqualTo("test_notice nullField=null");
  }

  private static class TestValidationNotice extends ValidationNotice {
    private final String code;

    public TestValidationNotice(String code, Map<String, Object> context) {
      super(context);
      this.code = code;
    }

    @Override
    public String getCode() {
      return code;
    }
  }
}
