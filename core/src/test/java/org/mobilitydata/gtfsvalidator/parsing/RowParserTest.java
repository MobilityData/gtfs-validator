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

package org.mobilitydata.gtfsvalidator.parsing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class RowParserTest {

    private RowParser createParser(String feedName, String cellValue) {
        NoticeContainer noticeContainer = new NoticeContainer();
        CsvRow csvRow = Mockito.mock(CsvRow.class);
        Mockito.when(csvRow.asString(0)).thenReturn(cellValue);
        RowParser parser = new RowParser(GtfsFeedName.parseString(feedName), noticeContainer);
        parser.setRow(csvRow);
        return parser;
    }

    private RowParser createParser(String cellValue) {
        return createParser("au-sydney-buses", cellValue);
    }

    @Test
    public void asUrl() {
        assertThat(createParser("http://google.com").asUrl(0, true)).isEqualTo("http://google.com");

        assertThat(createParser("invalid").asUrl(0, true)).isNull();
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

        assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.NON_NEGATIVE)).isEqualTo(new BigDecimal("123.45"));
        assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

        assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.NON_ZERO)).isEqualTo(new BigDecimal("123.45"));
        assertThat(parser.getNoticeContainer().getValidationNotices().isEmpty()).isTrue();

        assertThat(parser.asDecimal(0, true, RowParser.NumberBounds.POSITIVE)).isEqualTo(new BigDecimal("123.45"));
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
    public void asEmail() {
        assertThat(createParser("no-reply@google.com").asEmail(0, true)).isEqualTo("no-reply@google.com");

        assertThat(createParser("invalid").asEmail(0, true)).isNull();
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
        assertThat(createParser("ru_RU").asLanguageCode(0, true).getLanguage()).isEqualTo(Locale.forLanguageTag("ru_RU").getLanguage());
    }

    @Test
    public void asPhoneNumber() {
        assertThat(createParser("us-feed", "(650) 253-0000").asPhoneNumber(0, true)).isEqualTo("(650) 253-0000");
        assertThat(createParser("ch-feed", "044 668 18 00").asPhoneNumber(0, true)).isEqualTo("044 668 18 00");

        assertThat(createParser("au-feed", "invalid").asPhoneNumber(0, true)).isNull();
    }

    @Test
    public void asDate() {
        assertThat(createParser("20200901").asDate(0, true)).isEqualTo(GtfsDate.fromLocalDate(LocalDate.of(2020, 9, 1)));

        assertThat(createParser("invalid").asDate(0, true)).isNull();
    }

    @Test
    public void asTime() {
        assertThat(createParser("12:20:30").asTime(0, true)).isEqualTo(GtfsTime.fromHourMinuteSecond(12, 20, 30));
        assertThat(createParser("24:20:30").asTime(0, true)).isEqualTo(GtfsTime.fromHourMinuteSecond(24, 20, 30));

        assertThat(createParser("invalid").asTime(0, true)).isNull();
    }

    @Test
    public void asTimezone() {
        assertThat(createParser("America/Toronto").asTimezone(0, true)).isEqualTo(ZoneId.of("America/Toronto"));

        assertThat(createParser("invalid").asTimezone(0, true)).isNull();
    }

    @Test
    public void asId() {
        assertThat(createParser("32tgklu34y3k").asId(0, true)).isEqualTo("32tgklu34y3k");
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
}
