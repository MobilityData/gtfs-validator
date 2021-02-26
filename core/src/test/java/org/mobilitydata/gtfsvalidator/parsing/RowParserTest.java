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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthError;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class RowParserTest {

  private static String TEST_FEED_NAME = "au-sydney-buses";
  private static String TEST_FILENAME = "stops.txt";

  private static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  private RowParser createParser(String feedName, String cellValue) {
    NoticeContainer noticeContainer = new NoticeContainer();
    CsvRow csvRow = Mockito.mock(CsvRow.class);
    Mockito.when(csvRow.asString(0)).thenReturn(cellValue);
    Mockito.when(csvRow.getFileName()).thenReturn(TEST_FILENAME);
    Mockito.when(csvRow.getRowNumber()).thenReturn(8L);
    Mockito.when(csvRow.getColumnName(0)).thenReturn("column name");
    RowParser parser = new RowParser(GtfsFeedName.parseString(feedName), noticeContainer);
    parser.setRow(csvRow);
    return parser;
  }

  private RowParser createParser(String cellValue) {
    return createParser(TEST_FEED_NAME, cellValue);
  }

  @Test
  public void asUrlValid() {
    assertThat(createParser("http://google.com").asUrl(0, true)).isEqualTo("http://google.com");
  }

  @Test
  public void asUrlInvalid() {
    RowParser parser = createParser("invalid");
    assertThat(parser.asUrl(0, true)).isNull();
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new InvalidUrlNotice(TEST_FILENAME, 8, "column name", "invalid"));
  }

  @Test
  public void asInteger() {
    RowParser parser = createParser("12345");

    assertThat(parser.asInteger(0, true)).isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, true, RowParser.NumberBounds.NON_NEGATIVE)).isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, true, RowParser.NumberBounds.NON_ZERO)).isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asInteger(0, true, RowParser.NumberBounds.POSITIVE)).isEqualTo(12345);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asInteger(0, true)).isNull();
  }

  @Test
  public void asDecimal() {
    RowParser parser = createParser("123.45");

    assertThat(parser.asDecimal(0, true)).isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.NON_NEGATIVE))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.NON_ZERO))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.POSITIVE))
        .isEqualTo(new BigDecimal("123.45"));
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asDecimal(0, true)).isNull();
  }

  @Test
  public void asFloat() {
    RowParser parser = createParser("123.45");

    assertThat(parser.asFloat(0, true)).isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, true, RowParser.NumberBounds.NON_NEGATIVE)).isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, true, RowParser.NumberBounds.NON_ZERO)).isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(parser.asFloat(0, true, RowParser.NumberBounds.POSITIVE)).isEqualTo(123.45);
    assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

    assertThat(createParser("abc").asFloat(0, true)).isNull();
  }

  @Test
  public void asEmailValid() {
    assertThat(createParser("no-reply@google.com").asEmail(0, true))
        .isEqualTo("no-reply@google.com");
  }

  @Test
  public void asEmailInvalid() {
    RowParser parser = createParser("invalid");
    assertThat(parser.asEmail(0, true)).isNull();
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new InvalidEmailNotice(TEST_FILENAME, 8, "column name", "invalid"));
  }

  @Test
  public void asColor() {
    assertThat(createParser("FFFFFF").asColor(0, true)).isEqualTo(GtfsColor.fromInt(0xffffff));
    assertThat(createParser("abcdef").asColor(0, true)).isEqualTo(GtfsColor.fromInt(0xabcdef));
    assertThat(createParser("123456").asColor(0, true)).isEqualTo(GtfsColor.fromInt(0x123456));

    assertThat(createParser("invalid").asColor(0, true)).isNull();
  }

  @Test
  public void asCurrencyCode() {
    assertThat(createParser("USD").asCurrencyCode(0, true).getCurrencyCode()).isEqualTo("USD");
    assertThat(createParser("AUD").asCurrencyCode(0, true).getCurrencyCode()).isEqualTo("AUD");
    assertThat(createParser("CAD").asCurrencyCode(0, true).getCurrencyCode()).isEqualTo("CAD");

    assertThat(createParser("invalid").asCurrencyCode(0, true)).isNull();
  }

  @Test
  public void asLanguageCode() {
    // Russian of Russia.
    assertThat(createParser("ru-RU").asLanguageCode(0, true).toLanguageTag()).isEqualTo("ru-RU");
    // Zürich German.
    assertThat(createParser("gsw-u-sd-chzh").asLanguageCode(0, true).toLanguageTag())
        .isEqualTo("gsw-u-sd-chzh");
    // Latin American Spanish.
    assertThat(createParser("es-419").asLanguageCode(0, true).toLanguageTag()).isEqualTo("es-419");
  }

  @Test
  public void asPhoneNumber() {
    assertThat(createParser("us-feed", "(650) 253-0000").asPhoneNumber(0, true))
        .isEqualTo("(650) 253-0000");
    assertThat(createParser("ch-feed", "044 668 18 00").asPhoneNumber(0, true))
        .isEqualTo("044 668 18 00");
    assertThat(createParser("nl-feed", "+49 341 913 540 42").asPhoneNumber(0, true))
        .isEqualTo("+49 341 913 540 42");
    assertThat(createParser("nl-feed", "004980038762246").asPhoneNumber(0, true))
        .isEqualTo("004980038762246");
  }

  @Test
  public void asPhoneInvalid() {
    RowParser parser = createParser("us-feed", "invalid");
    assertThat(parser.asPhoneNumber(0, true)).isNull();
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new InvalidPhoneNumberNotice(TEST_FILENAME, 8, "column name", "invalid"));

    parser = createParser("nl-feed", "003280038762246");
    assertThat(parser.asPhoneNumber(0, true)).isNull();
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(
            new InvalidPhoneNumberNotice(TEST_FILENAME, 8, "column name", "003280038762246"));
  }

  @Test
  public void asDate() {
    assertThat(createParser("20200901").asDate(0, true))
        .isEqualTo(GtfsDate.fromLocalDate(LocalDate.of(2020, 9, 1)));

    assertThat(createParser("invalid").asDate(0, true)).isNull();
  }

  @Test
  public void asTime() {
    assertThat(createParser("12:20:30").asTime(0, true))
        .isEqualTo(GtfsTime.fromHourMinuteSecond(12, 20, 30));
    assertThat(createParser("24:20:30").asTime(0, true))
        .isEqualTo(GtfsTime.fromHourMinuteSecond(24, 20, 30));

    assertThat(createParser("invalid").asTime(0, true)).isNull();
  }

  @Test
  public void asTimezone() {
    assertThat(createParser("America/Toronto").asTimezone(0, true))
        .isEqualTo(ZoneId.of("America/Toronto"));

    assertThat(createParser("invalid").asTimezone(0, true)).isNull();
  }

  @Test
  public void asId() {
    assertThat(createParser("32tgklu34y3k").asId(0, true)).isEqualTo("32tgklu34y3k");
    RowParser parser = createParser("קום");
    // .קום :the .COM equivalent in Hebrew
    parser.asId(0, true);
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(
            new NonAsciiOrNonPrintableCharNotice(TEST_FILENAME, 8L, "column name","קום"));
    // Non-ASCII characters in ID are not an error. Validation may continue.
    assertThat(parser.hasParseErrorsInRow()).isFalse();
  }

  @Test
  public void hasOnlyPrintableAscii() {
    assertThat(RowParser.hasOnlyPrintableAscii("abc")).isTrue();
    assertThat(RowParser.hasOnlyPrintableAscii("a bc")).isTrue();
    assertThat(RowParser.hasOnlyPrintableAscii("@<>&*()!")).isTrue();
    // Cyrillic - not ASCII.
    assertThat(RowParser.hasOnlyPrintableAscii("Привет!")).isFalse();
    // Non-printable.
    assertThat(RowParser.hasOnlyPrintableAscii("\01\23")).isFalse();
  }

  @Test
  public void asLatitude() {
    assertThat(createParser("32.5").asLatitude(0, true)).isEqualTo(32.5);

    assertThat(createParser("-91").asLatitude(0, true)).isNull();
    assertThat(createParser("91").asLatitude(0, true)).isNull();
    assertThat(createParser("invalid").asLatitude(0, true)).isNull();
  }

  @Test
  public void asLongitude() {
    assertThat(createParser("-32.5").asLongitude(0, true)).isEqualTo(-32.5);
    assertThat(createParser("-91").asLongitude(0, true)).isEqualTo(-91);
    assertThat(createParser("91").asLongitude(0, true)).isEqualTo(91);

    assertThat(createParser("-181").asLongitude(0, true)).isNull();
    assertThat(createParser("181").asLongitude(0, true)).isNull();
    assertThat(createParser("invalid").asLongitude(0, true)).isNull();
  }

  @Test
  public void whitespaceInValue() {
    // Protected whitespaces are stripped. This is an error but GTFS consumers may patch it to be a
    // warning.
    RowParser parser = createParser(" 1\t");
    assertThat(parser.asInteger(0, true)).isEqualTo(1);
    LeadingOrTrailingWhitespacesNotice notice =
        new LeadingOrTrailingWhitespacesNotice(TEST_FILENAME, 8, "column name", " 1\t");
    assertThat(parser.hasParseErrorsInRow())
        .isEqualTo(notice.getSeverityLevel() == SeverityLevel.ERROR);
    assertThat(parser.getNoticeContainer().getValidationNotices()).containsExactly(notice);
  }

  @Test
  public void newLineInValue() {
    RowParser parser = createParser("a\nb");
    assertThat(parser.asText(0, true)).isEqualTo("a\nb");
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new NewLineInValueNotice(TEST_FILENAME, 8, "column name", "a\nb"));
  }

  @Test
  public void carriageReturnInValue() {
    RowParser parser = createParser("a\rb");
    assertThat(parser.asText(0, true)).isEqualTo("a\rb");
    assertThat(parser.hasParseErrorsInRow()).isTrue();
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
    assertThat(csvFile.getColumnCount()).isEqualTo(2);

    CsvRow csvRow = csvFile.iterator().next();
    RowParser parser =
        new RowParser(GtfsFeedName.parseString(TEST_FEED_NAME), new NoticeContainer());
    parser.setRow(csvRow);

    assertThat(parser.checkRowLength()).isFalse();
    assertThat(parser.hasParseErrorsInRow()).isFalse();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new EmptyRowNotice(TEST_FILENAME, 2));

    inputStream.close();
  }

  @Test
  public void checkRowLengthInvalidRowLength() throws IOException {
    InputStream inputStream = toInputStream("stop_id,stop_name\n" + "s1");
    CsvFile csvFile = new CsvFile(inputStream, TEST_FILENAME);

    assertThat(csvFile.isEmpty()).isFalse();
    assertThat(csvFile.getColumnCount()).isEqualTo(2);

    CsvRow csvRow = csvFile.iterator().next();
    RowParser parser =
        new RowParser(GtfsFeedName.parseString(TEST_FEED_NAME), new NoticeContainer());
    parser.setRow(csvRow);

    assertThat(parser.checkRowLength()).isFalse();
    assertThat(parser.hasParseErrorsInRow()).isTrue();
    assertThat(parser.getNoticeContainer().getValidationNotices())
        .containsExactly(new InvalidRowLengthError(TEST_FILENAME, 2, 1, 2));

    inputStream.close();
  }
}
