/*
 * Copyright 2021 Google LLC
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

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

@RunWith(JUnit4.class)
public class DefaultFieldValidatorTest {

  private DefaultFieldValidator DEFAULT_FIELD_VALIDATOR =
      new DefaultFieldValidator(CountryCode.forStringOrUnknown("AU"));
  private GtfsCellContext CELL_CONTEXT = GtfsCellContext.create("stops.txt", 2, "stop_id");

  @Test
  public void hasOnlyPrintableAscii() {
    assertThat(DefaultFieldValidator.hasOnlyPrintableAscii("abc")).isTrue();
    assertThat(DefaultFieldValidator.hasOnlyPrintableAscii("a bc")).isTrue();
    assertThat(DefaultFieldValidator.hasOnlyPrintableAscii("@<>&*()!")).isTrue();
    // Cyrillic - not ASCII.
    assertThat(DefaultFieldValidator.hasOnlyPrintableAscii("Привет!")).isFalse();
    // Non-printable.
    assertThat(DefaultFieldValidator.hasOnlyPrintableAscii("\01\23")).isFalse();
  }

  private static void assertValid(Predicate<NoticeContainer> validate) {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(validate.test(noticeContainer)).isTrue();
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  private static void assertInvalid(
      Predicate<NoticeContainer> validate, ValidationNotice... validationNotices) {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(validate.test(noticeContainer)).isFalse();
    assertThat(noticeContainer.getValidationNotices()).containsExactlyElementsIn(validationNotices);
  }

  @Test
  public void validateId_valid() {
    assertValid(nc -> DEFAULT_FIELD_VALIDATOR.validateId("32tgklu34y3k", CELL_CONTEXT, nc));
  }

  @Test
  public void validateId_invalid() {
    assertInvalid(
        nc -> DEFAULT_FIELD_VALIDATOR.validateId("קום", CELL_CONTEXT, nc),
        new NonAsciiOrNonPrintableCharNotice(
            CELL_CONTEXT.filename(), CELL_CONTEXT.csvRowNumber(), CELL_CONTEXT.fieldName(), "קום"));
  }

  @Test
  public void validateEmail_valid() {
    assertValid(
        nc -> DEFAULT_FIELD_VALIDATOR.validateEmail("no-reply@google.com", CELL_CONTEXT, nc));
  }

  @Test
  public void validateEmail_invalid() {
    assertInvalid(
        nc -> DEFAULT_FIELD_VALIDATOR.validateEmail("invalid", CELL_CONTEXT, nc),
        new InvalidEmailNotice(
            CELL_CONTEXT.filename(),
            CELL_CONTEXT.csvRowNumber(),
            CELL_CONTEXT.fieldName(),
            "invalid"));
  }

  @Test
  public void validatePhoneNumber_valid() {
    assertValid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("US"))
                .validatePhoneNumber("(650) 253-0000", CELL_CONTEXT, nc));

    assertValid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("ZZ"))
                .validatePhoneNumber("+1 (650) 253-0000", CELL_CONTEXT, nc));

    assertValid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("CH"))
                .validatePhoneNumber("044 668 18 00", CELL_CONTEXT, nc));

    assertValid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("NL"))
                .validatePhoneNumber("+49 341 913 540 42", CELL_CONTEXT, nc));

    assertValid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("NL"))
                .validatePhoneNumber("004980038762246", CELL_CONTEXT, nc));
  }

  @Test
  public void validatePhoneNumber_invalid() {
    assertInvalid(
        nc -> DEFAULT_FIELD_VALIDATOR.validatePhoneNumber("invalid", CELL_CONTEXT, nc),
        new InvalidPhoneNumberNotice(
            CELL_CONTEXT.filename(),
            CELL_CONTEXT.csvRowNumber(),
            CELL_CONTEXT.fieldName(),
            "invalid"));

    assertInvalid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("NL"))
                .validatePhoneNumber("003280038762246", CELL_CONTEXT, nc),
        new InvalidPhoneNumberNotice(
            CELL_CONTEXT.filename(),
            CELL_CONTEXT.csvRowNumber(),
            CELL_CONTEXT.fieldName(),
            "003280038762246"));

    assertInvalid(
        nc ->
            new DefaultFieldValidator(CountryCode.forStringOrUnknown("ZZ"))
                .validatePhoneNumber("003280038762246", CELL_CONTEXT, nc),
        new InvalidPhoneNumberNotice(
            CELL_CONTEXT.filename(),
            CELL_CONTEXT.csvRowNumber(),
            CELL_CONTEXT.fieldName(),
            "003280038762246"));
  }

  @Test
  public void whitespaceInValue_stripped() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(DEFAULT_FIELD_VALIDATOR.validateField(" 1\t", CELL_CONTEXT, noticeContainer))
        .isEqualTo("1");
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new LeadingOrTrailingWhitespacesNotice(
                CELL_CONTEXT.filename(),
                CELL_CONTEXT.csvRowNumber(),
                CELL_CONTEXT.fieldName(),
                " 1\t"));
  }

  @Test
  public void newLineInValue() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(DEFAULT_FIELD_VALIDATOR.validateField("a\nb", CELL_CONTEXT, noticeContainer))
        .isEqualTo("a\nb");
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new NewLineInValueNotice(
                CELL_CONTEXT.filename(),
                CELL_CONTEXT.csvRowNumber(),
                CELL_CONTEXT.fieldName(),
                "a\nb"));
  }

  @Test
  public void carriageReturnInValue() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(DEFAULT_FIELD_VALIDATOR.validateField("a\rb", CELL_CONTEXT, noticeContainer))
        .isEqualTo("a\rb");
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new NewLineInValueNotice(
                CELL_CONTEXT.filename(),
                CELL_CONTEXT.csvRowNumber(),
                CELL_CONTEXT.fieldName(),
                "a\rb"));
  }
}
