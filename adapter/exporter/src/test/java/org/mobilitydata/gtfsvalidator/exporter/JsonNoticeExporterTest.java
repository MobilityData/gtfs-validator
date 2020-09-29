/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.exporter;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class JsonNoticeExporterTest {
    private static final String FILENAME = "test.tst";

    @Test
    void exportBeginWriteStartObjectAndResultArray() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        underTest.exportBegin();

        InOrder inOrder = inOrder(mockGenerator);
        inOrder.verify(mockGenerator, times(1)).writeStartObject();
        inOrder.verify(mockGenerator, times(1)).writeFieldName(
                ArgumentMatchers.eq("results"));
        inOrder.verify(mockGenerator, times(1)).writeStartArray();
    }

    @Test
    void exportEndWriteEndArrayEndObjectAndFlush() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        underTest.exportEnd();

        InOrder inOrder = inOrder(mockGenerator);
        inOrder.verify(mockGenerator, times(1)).writeEndArray();
        inOrder.verify(mockGenerator, times(1)).writeEndObject();
        inOrder.verify(mockGenerator, times(1)).flush();
    }

    @Test
    void exportWithBeautifyOptionSetToTrueShouldUsePrettyPrinter() {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);
        new JsonNoticeExporter(mockGenerator, true);

        verify(mockGenerator, times(1)).useDefaultPrettyPrinter();
    }

    @Test
    void exportWithBeautifyOptionSetToTrueShouldNotUsePrettyPrinter() {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);
        new JsonNoticeExporter(mockGenerator, false);

        verifyNoInteractions(mockGenerator);
    }

    @Test
    void exportNonStandardHeaderNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        NonStandardHeaderNotice toExport = new NonStandardHeaderNotice(FILENAME, "extra");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInputZipContainsFolderNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InputZipContainsFolderNotice toExport = new InputZipContainsFolderNotice(FILENAME, "extraFolder");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportNonAsciiOrNonPrintableCharNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        NonAsciiOrNonPrintableCharNotice toExport = new NonAsciiOrNonPrintableCharNotice(
                FILENAME,
                "field_name",
                "entity_id",
                "entity_id_value"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCannotDownloadArchiveFromNetworkNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotDownloadArchiveFromNetworkNotice toExport = new CannotDownloadArchiveFromNetworkNotice(
                new URL("https://mobilitydata.org")
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCannotParseFloatNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotParseFloatNotice toExport = new CannotParseFloatNotice(
                FILENAME,
                "field_name",
                666,
                "abc");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCannotParseIntegerNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotParseIntegerNotice toExport = new CannotParseIntegerNotice(
                FILENAME,
                "field_name",
                666,
                "abc"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCannotUnzipInputArchiveNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotUnzipInputArchiveNotice toExport = new CannotUnzipInputArchiveNotice(FILENAME);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFloatFieldValueOutOfRangeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        FloatFieldValueOutOfRangeNotice toExport = new FloatFieldValueOutOfRangeNotice(
                FILENAME,
                "field_name",
                "entity_id",
                0, 66, 666
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportIntegerFieldValueOutOfRangeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        IntegerFieldValueOutOfRangeNotice toExport = new IntegerFieldValueOutOfRangeNotice(
                FILENAME,
                "field_name",
                "entity_id",
                0, 66, 666
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidRowLengthNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidRowLengthNotice toExport = new InvalidRowLengthNotice(
                FILENAME, 666, 10, 8
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidTimezoneNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidTimezoneNotice toExport = new InvalidTimezoneNotice(
                FILENAME,
                "field_name",
                "entity_id",
                "neverland"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidUrlNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidUrlNotice toExport = new InvalidUrlNotice(
                FILENAME,
                "field_name",
                "entity_id",
                "ftp://truc.bidule"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingHeaderNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingHeaderNotice toExport = new MissingHeaderNotice(FILENAME, "missing_header");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDuplicatedHeaderNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DuplicatedHeaderNotice toExport = new DuplicatedHeaderNotice(FILENAME, "duplicated_header");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingRequiredFileNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingRequiredFileNotice toExport = new MissingRequiredFileNotice(FILENAME);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingRequiredValueNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingRequiredValueNotice toExport = new MissingRequiredValueNotice(
                FILENAME,
                "field_name",
                "entity_id"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingTripEdgeStopTimeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingTripEdgeStopTimeNotice toExport = new MissingTripEdgeStopTimeNotice(
                "filed_name",
                "trip_id_XXX",
                1234
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidColorNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotParseColorNotice toExport = new CannotParseColorNotice(
                FILENAME,
                "field_name",
                0,
                "#zz"
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportExtraFileFoundNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        ExtraFileFoundNotice toExport = new ExtraFileFoundNotice(FILENAME);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidTimeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidTimeNotice toExport = new InvalidTimeNotice(FILENAME, "field_name", "time_value",
                "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCannotParseDateNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CannotParseDateNotice toExport = new CannotParseDateNotice(FILENAME, "field_name", 0,
                "raw_value");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidCurrencyCodeNoticeShouldWriteObject() throws IOException {

        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidCurrencyCodeNotice toExport = new InvalidCurrencyCodeNotice(FILENAME, "field_name", "entity_id",
                "currency_code");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportUnexpectedEnumValueNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        UnexpectedEnumValueNotice toExport = new UnexpectedEnumValueNotice(FILENAME, "field_name",
                "entity_id", 2);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportIllegalFieldValueCombinationNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        IllegalFieldValueCombinationNotice toExport = new IllegalFieldValueCombinationNotice(FILENAME, "field_name",
                "conflicting_field_name", "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDuplicatedEntityNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DuplicatedEntityNotice toExport = new DuplicatedEntityNotice(FILENAME, "field_name",
                "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidLangNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidLangNotice toExport = new InvalidLangNotice(FILENAME, "field_name",
                "entity_id", "lang_value");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidEmailNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidEmailNotice toExport = new InvalidEmailNotice(FILENAME, "field_name",
                "entity_id", "email_value");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteDescriptionEqualsNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        SameNameAndDescriptionForRouteNotice toExport = new SameNameAndDescriptionForRouteNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteColorAndTextContrastNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        RouteColorAndTextInsufficientContrastNotice toExport = new RouteColorAndTextInsufficientContrastNotice(FILENAME,
                "entity_id",
                0d);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteShortNameTooLongNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        RouteShortNameTooLongNotice toExport = new RouteShortNameTooLongNotice(FILENAME, "entity_id", "13");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInvalidRouteTypeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        InvalidRouteTypeNotice toExport = new InvalidRouteTypeNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingBothRouteNamesNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingShortAndLongNameForRouteNotice toExport = new MissingShortAndLongNameForRouteNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingRouteShortNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingRouteShortNameNotice toExport = new MissingRouteShortNameNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingRouteLongNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingRouteLongNameNotice toExport = new MissingRouteLongNameNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteLongNameEqualsShortNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        RouteLongNameEqualsShortNameNotice toExport = new RouteLongNameEqualsShortNameNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteLongNameContainsShortNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        RouteLongNameContainsShortNameNotice toExport = new RouteLongNameContainsShortNameNotice(FILENAME, "entity_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportCalendarEndDateBeforeStartDateShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        CalendarEndDateBeforeStartDateNotice toExport = new CalendarEndDateBeforeStartDateNotice(
                FILENAME,
                "wkend",
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 1, 1)
        );
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingAgencyIdNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final MissingAgencyIdNotice toExport = new MissingAgencyIdNotice("agency_name", "entity id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportInconsistentAgencyTimezoneNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final InconsistentAgencyTimezoneNotice toExport = new InconsistentAgencyTimezoneNotice(2,
                "set of inconsistent timezone");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportAgencyIdNotFoundNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final InvalidAgencyIdNotice toExport = new InvalidAgencyIdNotice("filename", "field name",
                "entity id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportNonExistingAgencyIdNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final AgencyIdNotFoundNotice toExport = new AgencyIdNotFoundNotice("filename", "field name",
                "entity id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportRouteIdNotFoundNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final RouteIdNotFoundNotice toExport = new RouteIdNotFoundNotice("filename", "entity id",
                "route id", "field name");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportServiceIdNotFoundNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final ServiceIdNotFoundNotice toExport = new ServiceIdNotFoundNotice("filename", "field name",
                "entity id", "service id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportShapeIdNotfoundNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final ShapeIdNotFoundNotice toExport =
                new ShapeIdNotFoundNotice("filename", "field name",
                        "composite key first part",
                        "composite key second part",
                        "composite key first value",
                        "composite key second value", "shape id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportTripIdNotFoundNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final TripIdNotFoundNotice toExport =
                new TripIdNotFoundNotice("filename",
                        "field name",
                        "composite key first part",
                        "composite key second part",
                        "composite key first value",
                        "composite key second value",
                        "trip id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportNotUsedShapeNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final ShapeNotUsedNotice toExport = new ShapeNotUsedNotice(
                "entity id",
                "field name");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFeedInfoStartDateAfterEndDateNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        FeedInfoStartDateAfterEndDateNotice toExport =
                new FeedInfoStartDateAfterEndDateNotice("feed_info.txt",
                        "start date",
                        "end date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFeedInfoExpiresInLessThan7DaysNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final FeedInfoExpiresInLessThan7DaysNotice toExport =
                new FeedInfoExpiresInLessThan7DaysNotice("feed_info.txt",
                        "current date",
                        "end date",
                        "feed_end_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFeedInfoExpiresInLessThan30DaysNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final FeedInfoExpiresInLessThan30DaysNotice toExport =
                new FeedInfoExpiresInLessThan30DaysNotice("feed_info.txt",
                        "current date",
                        "end date",
                        "feed_end_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingFeedEndDateNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final MissingFeedEndDateNotice toExport = new MissingFeedEndDateNotice("feed_info.txt",
                "feed_end_date",
                "feed_publisher_name",
                "feed_publisher_url",
                "feed_lang",
                "feed publisher name",
                "feed publisher url",
                "feed lang");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingFeedStartDateNoticeShouldWriteObject() throws IOException {
        final JsonGenerator mockGenerator = mock(JsonGenerator.class);

        final JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);
        final MissingFeedStartDateNotice toExport =
                new MissingFeedStartDateNotice("feed_info.txt",
                        "feed_start_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportStopTimeArrivalTimeAfterDepartureTimeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        StopTimeArrivalTimeAfterDepartureTimeNotice toExport =
                new StopTimeArrivalTimeAfterDepartureTimeNotice("stop_times.txt",
                        "arrival_time",
                        "departure_time",
                        "stop time trip id",
                        514);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFastTravelBetweenStopsNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        FastTravelBetweenStopsNotice toExport =
                new FastTravelBetweenStopsNotice("trip_id__value",
                        187.0f,
                        List.of(3, 4, 5, 6));
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportEmptyFileErrorNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        EmptyFileErrorNotice toExport =
                new EmptyFileErrorNotice("filename");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportEmptyFileWarningNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        EmptyFileWarningNotice toExport =
                new EmptyFileWarningNotice("filename");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFrequencyStartTimeAfterEndTimeNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        FrequencyStartTimeAfterEndTimeNotice toExport =
                new FrequencyStartTimeAfterEndTimeNotice("frequencies.txt",
                        "arrival_time",
                        "departure_time",
                        "trip_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportOverlappingTripFrequenciesNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        OverlappingTripFrequenciesNotice toExport =
                new OverlappingTripFrequenciesNotice("1234",
                        "arrival time value",
                        "departure time value",
                        "conflicting start time value",
                        "conflicting end time value");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportBackwardsTimeTravelInStopNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        BackwardsTimeTravelInStopNotice toExport =
                new BackwardsTimeTravelInStopNotice("stop_times.txt",
                        5,
                        "arrival_time",
                        "previous departure time",
                        2);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportTripNotUsedNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        TripNotUsedNotice toExport =
                new TripNotUsedNotice("trip_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportUnusableTripNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        UnusableTripNotice toExport =
                new UnusableTripNotice("trip_id");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportMissingCalendarAndCalendarDateFilesNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        MissingCalendarAndCalendarDateFilesNotice toExport =
                new MissingCalendarAndCalendarDateFilesNotice();
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportBlockTripsWithOverlappingStopTimesNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        BlockTripsWithOverlappingStopTimesNotice toExport =
                new BlockTripsWithOverlappingStopTimesNotice("trip id value", "07:00",
                        "10:00", "previous trip id value", "08:00",
                        "11:00", "block id");

        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportFeedInfoLangAgencyLangMismatchNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        FeedInfoLangAgencyLangMismatchNotice toExport =
                new FeedInfoLangAgencyLangMismatchNotice("agency id value", "agency name value"
                        , "agency lang value", "feed info lang value");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDuplicateRouteLongNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DuplicateRouteLongNameNotice toExport =
                new DuplicateRouteLongNameNotice("123", "456", "duplicate" +
                        " route long name");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDuplicateRouteShortNameNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DuplicateRouteShortNameNotice toExport =
                new DuplicateRouteShortNameNotice("123", "456", "duplicate" +
                        " route short name");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDuplicateRouteLongNameRouteShortNameCombinationNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DuplicateRouteLongNameRouteShortNameCombinationNotice toExport =
                new DuplicateRouteLongNameRouteShortNameCombinationNotice("123", "456",
                        "duplicate route long name",
                        "duplicate route short name");
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDecreasingStopTimeDistanceErrorNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DecreasingStopTimeDistanceNotice toExport =
                new DecreasingStopTimeDistanceNotice("12350", 2, 5f,
                        22, 3f);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }

    @Test
    void exportDecreasingShapeDistanceNoticeShouldWriteObject() throws IOException {
        JsonGenerator mockGenerator = mock(JsonGenerator.class);

        JsonNoticeExporter underTest = new JsonNoticeExporter(mockGenerator, false);

        DecreasingShapeDistanceNotice toExport =
                new DecreasingShapeDistanceNotice("12350", 22, 5f,
                        2, 50f);
        underTest.export(toExport);

        verify(mockGenerator, times(1)).writeObject(ArgumentMatchers.eq(toExport));
        verifyNoMoreInteractions(mockGenerator);
    }
}
