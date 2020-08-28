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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.mobilitydata.gtfsvalidator.adapter.protos.GtfsValidationOutputProto.GtfsProblem.Type.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_UNKNOWN_SERVICE_ID;
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
                0, 66, 666));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).addCsvColumnName(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("0.0"));
        verify(mockBuilder, times(1)).setAltValue(ArgumentMatchers.eq("66.0"));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("666.0"));
        // FIXME: find source of NPE when checking call on mockBuilder with null value whereas no NPE is thrown when
        //  building proto. See issue #296
//        verify(mockBuilder, times(1)).setAltEntityName(ArgumentMatchers.eq(null));
//        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq(null));
//        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq(null));
//        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq(null));

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
    void exportDuplicatedHeaderNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new DuplicatedHeaderNotice(FILENAME, "duplicated_header"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_DUPLICATE_COLUMN_NAME));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("duplicated_header"));
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
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setAltValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setAltEntityName(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingTripStopTimeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        // first stop (departure_time)
        underTest.export(new MissingTripEdgeStopTimeNotice(
                        "departure_time",
                        "trip_id_XXX",
                        1234
                )
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("stop_times.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_TRIP_WITH_NO_TIME_FOR_FIRST_STOP_TIME
                )
        );
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("departure_time"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("stop_sequence"));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1)).setAltValue(ArgumentMatchers.eq("trip_id_XXX"));
        verify(mockBuilder, times(1)).setAltEntityName(ArgumentMatchers.eq("1234"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));

        // last stop (arrival_time)
        underTest.export(new MissingTripEdgeStopTimeNotice(
                        "arrival_time",
                        "trip_id_XXX",
                        1234
                )
        );

        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_TRIP_WITH_NO_TIME_FOR_LAST_STOP_TIME
                )
        );
    }

    @Test
    void exportFastTravelBetweenStopsNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        // first stop (departure_time)
        underTest.export(new FastTravelBetweenStopsNotice(
                        "trip_id_XXX",
                        180.0f,
                        List.of(1, 2, 3)
                )
        );

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("stop_times.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FAST_TRAVEL_BETWEEN_FAR_STOPS
                )
        );
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(
                ArgumentMatchers.eq("trip_id_XXX"));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("[1, 2, 3]"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("stop_sequence_list"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));

        // last stop (arrival_time)
        underTest.export(new MissingTripEdgeStopTimeNotice(
                        "arrival_time",
                        "trip_id_XXX",
                        1234
                )
        );

        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_TRIP_WITH_NO_TIME_FOR_LAST_STOP_TIME
                )
        );
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
        underTest.export(new CannotParseColorNotice(FILENAME, "field_name", 0, "#zz")
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
    void exportInvalidCurrencyCodeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {

        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);


        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidCurrencyCodeNotice(FILENAME, "field_name", "entity_id",
                "currency_code"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("entity_id"));
        verify(mockBuilder, times(1)).setAltEntityValue(ArgumentMatchers.eq("currency_code"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportUnexpectedEnumValueNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new UnexpectedEnumValueNotice(FILENAME, "field_name",
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
    void exportIllegalFieldValueCombinationShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new IllegalFieldValueCombinationNotice(FILENAME, "field_name",
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
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setEntityValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setAltValue(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).setAltEntityName(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportDuplicatedEntityNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new DuplicatedEntityNotice(FILENAME, "field_name", "entity_id"));

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

    @Test
    void exportInvalidLangNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidLangNotice(FILENAME, "field_name",
                "entity_id", "lang_value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_LANGUAGE_CODE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("lang_value"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidEmailNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidEmailNotice(FILENAME, "field_name",
                "entity_id", "email_value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("field_name"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("email_value"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteDescriptionEqualsNameNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new SameNameAndDescriptionForRouteNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_STOP_WITH_SAME_NAME_AND_DESCRIPTION));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteColorAndTextContrastNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new RouteColorAndTextInsufficientContrastNotice(FILENAME, "entity_id", 0));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_COLOR_CONTRAST));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("0.0"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteShortNameTooLongNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new RouteShortNameTooLongNotice(FILENAME, "entity_id", "13"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_IS_TOO_LONG));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("13"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInvalidRouteTypeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidRouteTypeNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_ROUTE_TYPE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingBothRouteNamesNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingShortAndLongNameForRouteNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingRouteShortNameNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingRouteShortNameNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingRouteLongNameNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingRouteLongNameNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteLongNameEqualsShortNameNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new RouteLongNameEqualsShortNameNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_EQUALS_LONG_NAME));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteLongNameContainsShortNameNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new RouteLongNameContainsShortNameNotice(FILENAME, "entity_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_IS_CONTAINED_IN_LONG_NAME));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportCalendarEndDateBeforeStartDateAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new CalendarEndDateBeforeStartDateNotice(
                FILENAME,
                "wkend",
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 1, 1)
        ));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq(FILENAME));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CALENDAR_START_AND_END_DATE_OUT_OF_ORDER));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingAgencyIdNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingAgencyIdNotice("filename", "entity id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportInconsistentAgencyTimezoneNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InconsistentAgencyTimezoneNotice(2,
                "[timezone00, timezone01]"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("agency.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem
                        .Type.TYPE_AGENCIES_WITH_DIFFERENT_TIMEZONES));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("agency_timezone"));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("[timezone00, timezone01]"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq(String.valueOf(2)));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportAgencyIdNotFoundShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new InvalidAgencyIdNotice("filename", "field name", "entity id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("field name"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportNonExistingAgencyIdShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new AgencyIdNotFoundNotice("filename", "field name", "entity id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("field name"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportRouteIdNotFoundNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new RouteIdNotFoundNotice("filename", "entity id", "route id",
                "entity id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq("route id"));
        verify(mockBuilder, times(1))
                .setAltEntityValue(ArgumentMatchers.eq("route id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportShapeIdNotFoundNoticeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new ShapeIdNotFoundNotice("filename", "field name",
                "composite key first part",
                "composite key second part",
                "composite key first value",
                "composite key second value", "shape id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("field name"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq("composite key first part"));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("composite key second part"));
        verify(mockBuilder, times(1))
                .setAltEntityValue(ArgumentMatchers.eq("composite key first value"));
        verify(mockBuilder, times(1))
                .setAltValue(ArgumentMatchers.eq("composite key second value"));
        verify(mockBuilder, times(1))
                .setCsvKeyName(ArgumentMatchers.eq("shape id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportTripIdNotFoundNoticeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new TripIdNotFoundNotice("filename",
                "field name",
                "composite key first part",
                "composite key second part",
                "composite key first value",
                "composite key second value",
                "trip id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("field name"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq("composite key first part"));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("composite key second part"));
        verify(mockBuilder, times(1))
                .setAltEntityValue(ArgumentMatchers.eq("composite key first value"));
        verify(mockBuilder, times(1))
                .setAltValue(ArgumentMatchers.eq("composite key second value"));
        verify(mockBuilder, times(1))
                .setCsvKeyName(ArgumentMatchers.eq("trip id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportShapeNotUsedNoticeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new ShapeNotUsedNotice("entity id",
                "field name"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("shapes.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setValue(ArgumentMatchers.eq("field name"));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportServiceIdNotFoundNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new ServiceIdNotFoundNotice("filename", "entity id", "service_id",
                "service id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(ArgumentMatchers.eq("entity id"));
        verify(mockBuilder, times(1)).setAltValue(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1))
                .setEntityValue(ArgumentMatchers.eq(KEY_UNKNOWN_SERVICE_ID));
        verify(mockBuilder, times(1))
                .setAltEntityValue(ArgumentMatchers.eq("service id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFeedInfoStartDateAfterEndDateNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new FeedInfoStartDateAfterEndDateNotice("feed_info.txt",
                        "start date",
                        "end date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_INFO_START_AND_END_DATE_OUT_OF_ORDER));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("feed_publisher_name"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("feed_publisher_url"));
        verify(mockBuilder, times(1)).setAltEntityName(
                ArgumentMatchers.eq("feed_lang"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("feed publisher name"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq("feed publisher url"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("feed lang"));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("start date"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("end date"));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFeedInfoExpiresInLessThan7DaysNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new FeedInfoExpiresInLessThan7DaysNotice("feed_info.txt",
                        "current date",
                        "end date",
                        "feed_end_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_EXPIRATION_DATE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setEntityId(
                ArgumentMatchers.eq("feed_end_date"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("feed_publisher_name"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("feed_publisher_url"));
        verify(mockBuilder, times(1)).setAltEntityName(
                ArgumentMatchers.eq("feed_lang"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("feed publisher name"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq("feed publisher url"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("feed lang"));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("current date"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("end date"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFeedInfoExpiresInLessThan30DaysNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new FeedInfoExpiresInLessThan30DaysNotice("feed_info.txt",
                        "current date",
                        "end date",
                        "feed_end_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_EXPIRATION_DATE));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).setEntityId(
                ArgumentMatchers.eq("feed_end_date"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("feed_publisher_name"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("feed_publisher_url"));
        verify(mockBuilder, times(1)).setAltEntityName(
                ArgumentMatchers.eq("feed_lang"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("feed publisher name"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq("feed publisher url"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("feed lang"));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("current date"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("end date"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingFeedEndDateNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new MissingFeedEndDateNotice("feed_info.txt",
                        "feed_end_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).setEntityId(
                ArgumentMatchers.eq("feed_end_date"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("feed_publisher_name"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("feed_publisher_url"));
        verify(mockBuilder, times(1)).setAltEntityName(
                ArgumentMatchers.eq("feed_lang"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("feed publisher name"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq("feed publisher url"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("feed lang"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }


    @Test
    void exportMissingFeedStartDateNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new MissingFeedStartDateNotice("feed_info.txt",
                        "feed_start_date",
                        "feed_publisher_name",
                        "feed_publisher_url",
                        "feed_lang",
                        "feed publisher name",
                        "feed publisher url",
                        "feed lang"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));
        verify(mockBuilder, times(1)).setEntityId(
                ArgumentMatchers.eq("feed_start_date"));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("feed_publisher_name"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("feed_publisher_url"));
        verify(mockBuilder, times(1)).setAltEntityName(
                ArgumentMatchers.eq("feed_lang"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("feed publisher name"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq("feed publisher url"));
        verify(mockBuilder, times(1)).setAltEntityId(
                ArgumentMatchers.eq("feed lang"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportStopTimeArrivalTimeAfterDepartureTimeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new StopTimeArrivalTimeAfterDepartureTimeNotice("stop_times.txt",
                        "arrival_time",
                        "departure_time",
                        "stop time trip id",
                        514));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("stop_times.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("tripId"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("stopSequence"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("stop time trip id"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq(String.valueOf(514)));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("arrival_time"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("departure_time"));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFrequencyStartTimeAfterEndTimeNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new FrequencyStartTimeAfterEndTimeNotice("frequencies.txt",
                        "250",
                        "120",
                        "trip id value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("frequencies.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("tripId"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("startTime"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("trip id value"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq(String.valueOf(250)));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportEmptyFileErrorNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new EmptyFileErrorNotice("filename"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(TYPE_CSV_BAD_NUMBER_OF_ROWS));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportEmptyFileWarningNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new EmptyFileWarningNotice("filename"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("filename"));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(TYPE_CSV_BAD_NUMBER_OF_ROWS));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportBackwardsTimeTravelInStopNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new BackwardsTimeTravelInStopNotice("trip id value",
                        5,
                        "arrival_time",
                        "previous stoptime departure time",
                        2));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(
                ArgumentMatchers.eq("stop_times.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setAltValue(
                ArgumentMatchers.eq("stopTimeTripId"));
        verify(mockBuilder, times(1)).setCsvKeyName(
                ArgumentMatchers.eq("stopTimeStopSequence"));
        verify(mockBuilder, times(1)).setOtherCsvFileName(
                ArgumentMatchers.eq("trip id value"));
        verify(mockBuilder, times(1)).setOtherCsvKeyName(
                ArgumentMatchers.eq(String.valueOf(5)));
        verify(mockBuilder, times(1)).setEntityValue(
                ArgumentMatchers.eq("arrival_time"));
        verify(mockBuilder, times(1)).setAltEntityValue(
                ArgumentMatchers.eq("previous stoptime departure time"));
        verify(mockBuilder, times(1)).setEntityName(
                ArgumentMatchers.eq("2"));

        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportTripNotUsedNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new TripNotUsedNotice("trip_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("trips.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportUnusableTripNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new UnusableTripNotice("trip_id"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("stop_times.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("trip_id"));
        verify(mockBuilder, times(1))
                .setType(ArgumentMatchers.eq(TYPE_TRIP_WITH_NO_USABLE_STOPS));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportMissingCalendarAndCalendarDateFilesNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(new MissingCalendarAndCalendarDateFilesNotice());

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("calendar.txt"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1))
                .setType(ArgumentMatchers.eq(TYPE_CSV_MISSING_TABLE));
        verify(mockBuilder, times(1))
                .setAltEntityId(ArgumentMatchers.eq("calendar_dates.txt"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }

    @Test
    void exportFeedInfoLangAgencyLangMismatchNoticeShouldMapToCsvProblemAndWriteToStream() throws IOException {
        GtfsValidationOutputProto.GtfsProblem.Builder mockBuilder =
                mock(GtfsValidationOutputProto.GtfsProblem.Builder.class, RETURNS_SELF);

        GtfsValidationOutputProto.GtfsProblem mockProblem = mock(GtfsValidationOutputProto.GtfsProblem.class);

        when(mockBuilder.build()).thenReturn(mockProblem);

        OutputStream mockStream = mock(OutputStream.class);

        ProtobufNoticeExporter.ProtobufOutputStreamGenerator mockStreamGenerator =
                mock(ProtobufNoticeExporter.ProtobufOutputStreamGenerator.class);
        when(mockStreamGenerator.getStream()).thenReturn(mockStream);

        ProtobufNoticeExporter underTest = new ProtobufNoticeExporter(mockBuilder, mockStreamGenerator);
        underTest.export(
                new FeedInfoLangAgencyLangMismatchNotice(
                        "agency id value",
                        "agency name value",
                        "agency lang value",
                        "feed info lang value"));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).setCsvFileName(ArgumentMatchers.eq("feed_info.txt"));
        verify(mockBuilder, times(1))
                .setEntityId(ArgumentMatchers.eq("agency id value"));
        verify(mockBuilder, times(1)).setSeverity(
                ArgumentMatchers.eq(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR));
        verify(mockBuilder, times(1)).setType(
                ArgumentMatchers.eq(TYPE_AGENCY_LANG_AND_FEED_LANG_MISMATCH));
        verify(mockBuilder, times(1))
                .setAltValue(ArgumentMatchers.eq("agency name value"));
        verify(mockBuilder, times(1))
                .setCsvKeyName(ArgumentMatchers.eq("agency lang value"));
        verify(mockBuilder, times(1))
                .setOtherCsvFileName(ArgumentMatchers.eq("feed info lang value"));
        verify(mockBuilder, times(1)).build();
        verify(mockProblem, times(1)).writeTo(ArgumentMatchers.eq(mockStream));
    }
}
