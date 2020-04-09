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

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsValidationOutputProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import static org.mockito.Mockito.*;

class ProtobufNoticeExporterTest {
    private static final String FILENAME = "test.tst";

    @Test
    void exportBeginDoNothing() {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);

        underTest.exportBegin();

        verifyNoInteractions(mockBuilder, mockStreamGenerator);
    }

    @Test
    void exportEndCloseAllStreams() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);

        underTest.exportEnd();

        verifyNoInteractions(mockBuilder);
        verify(mockStreamGenerator, times(1)).closeAll();
    }


    @Test
    void exportNonStandardHeaderNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new NonStandardHeaderNotice(FILENAME, "extra"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_COLUMN));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("extra"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInputZipContainsFolderNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InputZipContainsFolderNotice(FILENAME, "extraFolder"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportNonAsciiOrNonPrintableCharNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new NonAsciiOrNonPrintableCharNotice(FILENAME, "field_name", "entity_id",
                "entity_id_value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotConstructDataProviderNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotConstructDataProviderNotice(FILENAME));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotDownloadArchiveFromNetworkNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotDownloadArchiveFromNetworkNotice(new URL("https://mobilitydata.org"))
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("https://mobilitydata.org"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_FILE_CORRUPTED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotParseFloatNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotParseFloatNotice(FILENAME, "field_name", 666, "abc")
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setCsvColumnName(
                ArgumentMatchers.eq(666), ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq("abc"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotParseIntegerNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotParseIntegerNotice(FILENAME, "field_name", 666, "abc")
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setCsvColumnName(
                ArgumentMatchers.eq(666), ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq("abc"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotUnzipInputArchiveNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotUnzipInputArchiveNotice(FILENAME));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFloatFieldValueOutOfRangeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new FloatFieldValueOutOfRangeNotice(FILENAME, "field_name", "entity_id",
                0, 66, 666
        ));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).addCsvColumnName(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setValue(
                ArgumentMatchers.eq("0.0"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("66.0"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("666.0"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportIntegerFieldValueOutOfRangeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new IntegerFieldValueOutOfRangeNotice(FILENAME, "field_name", "entity_id",
                0, 66, 666
        ));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).addCsvColumnName(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setValue(
                ArgumentMatchers.eq("0"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("66"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("666"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));

    }

    @Test
    void exportInvalidRowLengthNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidRowLengthNotice(FILENAME, 666, 10, 8));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_BAD_NUMBER_OF_VALUES));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityRow(ArgumentMatchers.eq(666));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("10"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidTimezoneNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidTimezoneNotice(FILENAME, "field_name", "entity_id",
                "neverland"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_TIMEZONE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("neverland"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidUrlNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidUrlNotice(FILENAME, "field_name", "entity_id",
                "ftp://truc.bidule"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("ftp://truc.bidule"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingHeaderNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingHeaderNotice(FILENAME, "missing_header"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_COLUMN));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("missing_header"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingRequiredFileNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingRequiredFileNotice(FILENAME));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_TABLE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingRequiredValueNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingRequiredValueNotice(FILENAME, "field_name", "entity_id")
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_VALUE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCouldNotCleanOrCreatePathNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CouldNotCleanOrCreatePathNotice("../output")
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(""));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("../output"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidColorNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidColorNotice(FILENAME, "field_name", "entity_id", "#zz")
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("#zz"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportExtraFileFoundNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new ExtraFileFoundNotice(FILENAME));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_FILE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidTimeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidTimeNotice(FILENAME, "field_name", "entity_id", "time_value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("time_value"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCannotParseDateNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CannotParseDateNotice(FILENAME, "field_name", 0, "raw_value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setCsvColumnName(ArgumentMatchers.eq(0),
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq("raw_value"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportUnexpectedValueNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new UnexpectedValueNotice(FILENAME, "field_name",
                "entity_id", 2));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq("2"));
        verify(mockBuilder, times(1)).setAltEntityId("entity_id");
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportIncoherentValuesNoticeForFieldsShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new IncoherentValuesForFieldsNotice(FILENAME, "field_name",
                "conflicting_field_name", "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("conflicting_field_name"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportUnexpectedDefinedFieldNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new UnexpectedDefinedFieldNotice(FILENAME, "field_name",
                "field_value", "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("field_value"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportEntityMustBeUniqueNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new EntityMustBeUniqueNotice(FILENAME, "field_name", "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }
}