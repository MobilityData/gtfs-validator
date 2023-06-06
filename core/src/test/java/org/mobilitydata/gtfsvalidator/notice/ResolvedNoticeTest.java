package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.testnotices.StringFieldNotice;

@RunWith(JUnit4.class)
public class ResolvedNoticeTest {

  @Test
  public void equals_sameContext() {
    assertThat(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value1"), SeverityLevel.ERROR))
        .isEqualTo(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value1"), SeverityLevel.ERROR));
  }

  @Test
  public void equals_differentContext() {
    assertThat(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value1"), SeverityLevel.ERROR))
        .isNotEqualTo(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value2"), SeverityLevel.ERROR));
  }

  @Test
  public void equals_differentSeverity() {
    assertThat(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value1"), SeverityLevel.ERROR))
        .isNotEqualTo(
            new ResolvedNotice<ValidationNotice>(
                new StringFieldNotice("value1"), SeverityLevel.WARNING));
  }

  @Test
  public void toString_exportsContext() {
    assertThat(
            new ResolvedNotice<ValidationNotice>(
                    new StringFieldNotice("value1"), SeverityLevel.ERROR)
                .toString())
        .isEqualTo("string_field ERROR {\"someField\":\"value1\"}");
  }

  @Test
  public void getMappingKey_returnsFieldCombination() {
    ResolvedNotice<ValidationNotice> notice =
        new ResolvedNotice<>(new StringFieldNotice("some field"), SeverityLevel.ERROR);
    assertThat(notice.getMappingKey()).matches("string_field2");
  }
}
