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

import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsValidationOutputProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProtobufNoticeExporter implements ValidationResultRepository.NoticeExporter {

    private final GtfsValidationOutputProto.GtfsProblem.Builder protoBuilder;
    private final ProtobufOutputStreamGenerator streamGenerator;

    public ProtobufNoticeExporter(final GtfsValidationOutputProto.GtfsProblem.Builder builder,
                                  final ProtobufOutputStreamGenerator streamGenerator) {
        this.protoBuilder = builder;
        this.streamGenerator = streamGenerator;
    }

    public static final String FILE_EXTENSION = ".pb";

    @Override
    public String getExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public void exportBegin() {

    }

    @Override
    public void exportEnd() throws IOException {
        streamGenerator.closeAll();
    }

    @Override
    public void export(final NonStandardHeaderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_COLUMN)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                .setAltEntityValue(toExport.getExtraHeader())
                .clearAltEntityId()
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final InputZipContainsFolderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final NonAsciiOrNonPrintableCharNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                .setAltEntityValue(toExport.getFieldName())
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final CannotConstructDataProviderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotDownloadArchiveFromNetworkNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_FILE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotParseFloatNotice toExport) throws IOException {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(),
                toExport.getRawValue());
    }

    private void parsingNoticeToProto(String filename, int lineNumber, String fieldName, String rawValue) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(filename)
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setCsvColumnName(lineNumber, fieldName)
                .setEntityValue(rawValue)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotParseIntegerNotice toExport) throws IOException {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(),
                toExport.getRawValue());
    }

    @Override
    public void export(CannotUnzipInputArchiveNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(FloatFieldValueOutOfRangeNotice toExport) throws IOException {
        outOfRangeNoticeToProto(toExport.getFilename(), toExport.getEntityId(), toExport.getFieldName(),
                String.valueOf(toExport.getRangeMin()), String.valueOf(toExport.getRangeMax()),
                String.valueOf(toExport.getActualValue()));
    }

    @Override
    public void export(IntegerFieldValueOutOfRangeNotice toExport) throws IOException {
        outOfRangeNoticeToProto(toExport.getFilename(),
                toExport.getEntityId(),
                toExport.getFieldName(),
                String.valueOf(toExport.getRangeMin()),
                String.valueOf(toExport.getRangeMax()),
                String.valueOf(toExport.getActualValue()));
    }

    private void outOfRangeNoticeToProto(String filename, String entityId, String fieldName, String rangeMinAsString,
                                         String rangeMaxAsString, String actualValueAsString)
            throws IOException {
        protoBuilder.clear()
                .setCsvFileName(filename)
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(entityId)
                .addCsvColumnName(fieldName)
                .setValue(rangeMinAsString)
                .setAltValue(rangeMaxAsString)
                .setAltEntityValue(actualValueAsString)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidRowLengthNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_BAD_NUMBER_OF_VALUES)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityRow(toExport.getRowIndex())
                .setAltEntityValue(String.valueOf(toExport.getExpectedLength()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidTimezoneNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_TIMEZONE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getTimezoneValue())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidUrlNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getUrlValue())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingHeaderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_COLUMN)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getMissingHeaderName())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingRequiredFileNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_TABLE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFilename())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingRequiredValueNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_VALUE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getEntityId())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CouldNotCleanOrCreatePathNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getPathToCleanOrCreate())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidColorNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getColorValue())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(ExtraFileFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_FILE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidTimeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getTimeValue())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotParseDateNotice toExport) throws IOException {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(),
                toExport.getRawValue());
    }

    @Override
    public void export(UnexpectedValueNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getFieldName())
                .setEntityValue(toExport.getEnumValue())
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(IncoherentValuesForFields toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getFieldName())
                .setAltEntityId(toExport.getConflictingFieldName())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    public static class ProtobufOutputStreamGenerator {
        private final String targetPath;
        private final List<OutputStream> openedStreamCollection = new ArrayList<>();
        private int streamCounter;

        public ProtobufOutputStreamGenerator(final String outputPath) {
            this.targetPath = outputPath;
        }

        public OutputStream getStream() throws IOException {
            OutputStream newStream = Files.newOutputStream(Paths.get(
                    targetPath + File.separator + streamCounter +
                            ProtobufNoticeExporter.FILE_EXTENSION
            ));
            ++streamCounter;
            openedStreamCollection.add(newStream);
            return newStream;
        }

        public void closeAll() throws IOException {
            for (OutputStream outputStream : openedStreamCollection) {
                outputStream.close();
            }
        }
    }
}