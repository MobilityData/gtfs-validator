/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.parsing;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidFloatNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.NumberOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.DefaultFieldValidator;
import org.mobilitydata.gtfsvalidator.validator.GtfsFieldValidator;

@RunWith(JUnit4.class)
public class RowParserTest {

  private static CountryCode TEST_COUNTRY_CODE = CountryCode.forStringOrUnknown("AU");
  private static String TEST_FILENAME = "stops.txt";

  private static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  private static GtfsFieldValidator FIELD_VALIDATOR = new DefaultFieldValidator(TEST_COUNTRY_CODE);

  private static RowParser createParser(String cellValue) {
    NoticeContainer noticeContainer = new NoticeContainer();
    RowParser parser =
        new RowParser(TEST_FILENAME, new CsvHeader(new String[] {"column name"}), FIELD_VALIDATOR);
    parser.setRow(new CsvRow(8, new String[] {cellValue}), noticeContainer);
    return parser;
  }

  private static <T> void assertValid(String cellValue, Function<RowParser, T> parse, T expected) {
    RowParser parser = createParser(cellValue);
    assertThat(parse.apply(parser)).isEqualTo(expected);
    assertThat(parser.getNoticeContainer().getValidationNotices()).isEmpty();
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isFalse();
  }

  private static <T> void assertInvalid(
      String cellValue,
      Function<RowParser, T> parse,
      T expected,
      ValidationNotice... validationNotices) {
    RowParser parser = createParser(cellValue);
    assertThat(parse.apply(parser)).isEqualTo(expected);
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactlyElementsIn(validationNotices);
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isTrue();
  }

  @Test
  public void asUrl_valid() {
    assertValid("http://google.com", p -> p.asUrl(0, FieldLevelEnum.REQUIRED), "http://google.com");
  }

  @Test
  public void asUrl_invalid() {
    assertInvalid(
        "invalid",
        p -> p.asUrl(0, FieldLevelEnum.REQUIRED),
        "invalid",
        new InvalidUrlNotice("stops.txt", 8, "column name", "invalid"));
  }

  @Test
  public void asString_recommended_valid() {
    RowParser parser = createParser("ABCDE");

    assertThat(parser.asString(0, FieldLevelEnum.RECOMMENDED)).isEqualTo("ABCDE");
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();
  }

  @Test
  public void asString_required_valid() {
    RowParser parser = createParser("ABCDE");

    assertThat(parser.asString(0, FieldLevelEnum.REQUIRED)).isEqualTo("ABCDE");
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();
  }

  @Test
  public void asString_recommended_missing() {
    RowParser parser = createParser(null);

    assertThat(parser.asString(0, FieldLevelEnum.RECOMMENDED)).isEqualTo(null);
    MissingRecommendedFieldNotice notice =
        new MissingRecommendedFieldNotice(TEST_FILENAME, 8, "column name");
    assertThat(parser.getNoticeContainer().getValidationNotices()).containsExactly(notice);
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isFalse();
  }

  @Test
  public void asString_required_missing() {
    RowParser parser = createParser(null);

    assertThat(parser.asString(0, FieldLevelEnum.REQUIRED)).isEqualTo(null);
    MissingRequiredFieldNotice notice =
        new MissingRequiredFieldNotice(TEST_FILENAME, 8, "column name");
    assertThat(parser.getNoticeContainer().getValidationNotices()).containsExactly(notice);
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isTrue();
  }

  @Test
  public void asInteger() {
    RowParser parser = createParser("12345");

    assertThat(parser.asInteger(0, FieldLevelEnum.REQUIRED)).isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_NEGATIVE))
        .isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_ZERO))
        .isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.POSITIVE))
        .isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asInteger(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asDecimal() {
    RowParser parser = createParser("123.45");

    assertThat(parser.asDecimal(0, FieldLevelEnum.REQUIRED)).isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_NEGATIVE))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_ZERO))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.POSITIVE))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asDecimal(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asFloat() {
    RowParser parser = createParser("123.45");

    assertThat(parser.asFloat(0, FieldLevelEnum.REQUIRED)).isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_NEGATIVE))
        .isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.NON_ZERO))
        .isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, FieldLevelEnum.REQUIRED, RowParser.NumberBounds.POSITIVE))
        .isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asFloat(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asEmail_valid() {
    assertValid(
        "no-reply@google.com", p -> p.asEmail(0, FieldLevelEnum.REQUIRED), "no-reply@google.com");
  }

  @Test
  public void asEmail_invalid() {
    assertInvalid(
        "invalid",
        p -> p.asEmail(0, FieldLevelEnum.REQUIRED),
        "invalid",
        new InvalidEmailNotice("stops.txt", 8, "column name", "invalid"));
  }

  @Test
  public void asColor() {
    assertThat(createParser("FFFFFF").asColor(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsColor.fromInt(0xffffff));
    assertThat(createParser("abcdef").asColor(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsColor.fromInt(0xabcdef));
    assertThat(createParser("123456").asColor(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsColor.fromInt(0x123456));

    assertThat(createParser("invalid").asColor(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asCurrencyCode() {
    assertThat(createParser("USD").asCurrencyCode(0, FieldLevelEnum.REQUIRED).getCurrencyCode())
        .isEqualTo("USD");
    assertThat(createParser("AUD").asCurrencyCode(0, FieldLevelEnum.REQUIRED).getCurrencyCode())
        .isEqualTo("AUD");
    assertThat(createParser("CAD").asCurrencyCode(0, FieldLevelEnum.REQUIRED).getCurrencyCode())
        .isEqualTo("CAD");

    assertThat(createParser("invalid").asCurrencyCode(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asLanguageCode_invalid() {
    // Underscore is invalid delimiter.
    assertThat(createParser("en_EN").asLanguageCode(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asLanguageCode() {
    // Russian of Russia.
    assertThat(createParser("ru-RU").asLanguageCode(0, FieldLevelEnum.REQUIRED).toLanguageTag())
        .isEqualTo("ru-RU");
    // Zürich German.
    assertThat(
            createParser("gsw-u-sd-chzh")
                .asLanguageCode(0, FieldLevelEnum.REQUIRED)
                .toLanguageTag())
        .isEqualTo("gsw-u-sd-chzh");
    // Latin American Spanish.
    assertThat(createParser("es-419").asLanguageCode(0, FieldLevelEnum.REQUIRED).toLanguageTag())
        .isEqualTo("es-419");
  }

  @Test
  public void asPhoneNumber_valid() {
    assertValid(
        "(650) 253-0000", p -> p.asPhoneNumber(0, FieldLevelEnum.REQUIRED), "(650) 253-0000");
  }

  @Test
  public void asPhoneNumber_invalid() {
    assertInvalid(
        "invalid",
        p -> p.asPhoneNumber(0, FieldLevelEnum.REQUIRED),
        "invalid",
        new InvalidPhoneNumberNotice("stops.txt", 8, "column name", "invalid"));
  }

  @Test
  public void asDate() {
    assertThat(createParser("20200901").asDate(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsDate.fromLocalDate(LocalDate.of(2020, 9, 1)));

    assertThat(createParser("invalid").asDate(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asTime() {
    assertThat(createParser("12:20:30").asTime(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsTime.fromHourMinuteSecond(12, 20, 30));
    assertThat(createParser("24:20:30").asTime(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(GtfsTime.fromHourMinuteSecond(24, 20, 30));

    assertThat(createParser("invalid").asTime(0, FieldLevelEnum.REQUIRED)).isNull();
  }

  @Test
  public void asTimezone_valid() {
    assertThat(createParser("America/Toronto").asTimezone(0, FieldLevelEnum.REQUIRED))
        .isEqualTo(ZoneId.of("America/Toronto"));
  }

  @Test
  public void asTimezone_invalid() {
    // ZoneId.of("invalid") throws ZoneRulesException.
    assertThat(createParser("invalid").asTimezone(0, FieldLevelEnum.REQUIRED)).isNull();

    // ZoneId.of("Latinoamerica/ Argentina") throws DateTimeException.
    assertThat(createParser("Latinoamerica/ Argentina").asTimezone(0, FieldLevelEnum.REQUIRED))
        .isNull();
  }

  @Test
  public void asId() {
    assertThat(createParser("32tgklu34y3k").asId(0, FieldLevelEnum.REQUIRED))
        .isEqualTo("32tgklu34y3k");
    RowParser parser = createParser("קום");
    // .קום :the .COM equivalent in Hebrew
    parser.asId(0, FieldLevelEnum.REQUIRED);
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(
            new NonAsciiOrNonPrintableCharNotice(TEST_FILENAME, 8, "column name", "קום"));
    // Non-ASCII characters in ID are not an error. Validation may continue.
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isFalse();
  }

  @Test
  public void asLatitude_valid() {
    assertValid("32.5", p -> p.asLatitude(0, FieldLevelEnum.REQUIRED), 32.5);
  }

  @Test
  public void asLatitude_outOfRange() {
    assertInvalid(
        "-91",
        p -> p.asLatitude(0, FieldLevelEnum.REQUIRED),
        -91.0,
        new NumberOutOfRangeNotice(
            "stops.txt", 8, "column name", "latitude within [-90, 90]", -91.0));
    assertInvalid(
        "91",
        p -> p.asLatitude(0, FieldLevelEnum.REQUIRED),
        91.0,
        new NumberOutOfRangeNotice(
            "stops.txt", 8, "column name", "latitude within [-90, 90]", 91.0));
  }

  @Test
  public void asLatitude_nonParsable() {
    assertInvalid(
        "invalid",
        p -> p.asLatitude(0, FieldLevelEnum.REQUIRED),
        null,
        new InvalidFloatNotice("stops.txt", 8, "column name", "invalid"));
  }

  @Test
  public void asLongitude_valid() {
    assertValid("-32.5", p -> p.asLongitude(0, FieldLevelEnum.REQUIRED), -32.5);
    assertValid("-91", p -> p.asLongitude(0, FieldLevelEnum.REQUIRED), -91);
    assertValid("91", p -> p.asLongitude(0, FieldLevelEnum.REQUIRED), 91);
  }

  @Test
  public void asLongitude_outOfRange() {
    assertInvalid(
        "-181",
        p -> p.asLongitude(0, FieldLevelEnum.REQUIRED),
        -181.0,
        new NumberOutOfRangeNotice(
            "stops.txt", 8, "column name", "longitude within [-180, 180]", -181.0));
    assertInvalid(
        "181",
        p -> p.asLongitude(0, FieldLevelEnum.REQUIRED),
        181.0,
        new NumberOutOfRangeNotice(
            "stops.txt", 8, "column name", "longitude within [-180, 180]", 181.0));
  }

  @Test
  public void asLongitude_nonParsable() {
    assertInvalid(
        "invalid",
        p -> p.asLongitude(0, FieldLevelEnum.REQUIRED),
        null,
        new InvalidFloatNotice("stops.txt", 8, "column name", "invalid"));
  }

  @Test
  public void whitespaceInValue() {
    // Protected whitespaces are stripped.
    RowParser parser = createParser(" 1\t");
    assertThat(parser.asInteger(0, FieldLevelEnum.REQUIRED)).isEqualTo(1);
    LeadingOrTrailingWhitespacesNotice notice =
        new LeadingOrTrailingWhitespacesNotice(TEST_FILENAME, 8, "column name", " 1\t");
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isFalse();
    assertThat(parser.getNoticeContainer().getValidationNotices()).containsExactly(notice);
  }

  @Test
  public void newLineInValue() {
    RowParser parser = createParser("a\nb");
    assertThat(parser.asText(0, FieldLevelEnum.REQUIRED)).isEqualTo("a\nb");
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new NewLineInValueNotice(TEST_FILENAME, 8, "column name", "a\nb"));
  }

  @Test
  public void carriageReturnInValue() {
    RowParser parser = createParser("a\rb");
    assertThat(parser.asText(0, FieldLevelEnum.REQUIRED)).isEqualTo("a\rb");
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new NewLineInValueNotice(TEST_FILENAME, 8, "column name", "a\rb"));
  }

  @Test
  public void checkRowLengthEmptyRow() throws IOException {
    // The final row in this test contains only spaces and does not end with a new line. Univocity
    // parser treats it as a non-empty row that contains a single column which holds null.
    InputStream inputStream = toInputStream("stop_id,stop_name\n  ");
    CsvFile csvFile = new CsvFile(inputStream, TEST_FILENAME);

    assertThat(csvFile.isEmpty()).isFalse();
    assertThat(csvFile.getHeader().getColumnCount()).isEqualTo(2);

    CsvRow csvRow = csvFile.iterator().next();
    RowParser parser = new RowParser(csvFile.getFileName(), csvFile.getHeader(), FIELD_VALIDATOR);
    parser.setRow(csvRow, new NoticeContainer());

    assertThat(parser.checkRowLength()).isFalse();
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isFalse();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new EmptyRowNotice(TEST_FILENAME, 2));

    inputStream.close();
  }

  @Test
  public void checkRowLengthInvalidRowLength() {
    RowParser parser =
        new RowParser(
            TEST_FILENAME, new CsvHeader(new String[] {"stop_id", "stop_name"}), FIELD_VALIDATOR);
    parser.setRow(new CsvRow(2, new String[] {"s1"}), new NoticeContainer());

    assertThat(parser.checkRowLength()).isFalse();
    assertThat(parser.getNoticeContainer().hasValidationErrors()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new InvalidRowLengthNotice(TEST_FILENAME, 2, 1, 2));
  }
}
