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
import org.mobilitydata.gtfsvalidator.usecase.notice.info.UnsupportedGtfsTypeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.io.OutputStream;

public class ProtobufNoticeExporter implements ValidationResultRepository.NoticeExporter {

    private final GtfsValidationOutputProto.GtfsProblem.Builder protoBuilder;

    public ProtobufNoticeExporter(final GtfsValidationOutputProto.GtfsProblem.Builder builder) {
        this.protoBuilder = builder;
    }

    private static final String FILE_EXTENSION = ".pb";

    @Override
    public String getExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public void export(final NonStandardHeaderNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_COLUMN)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                .setAltEntityValue(toExport.getExtraHeader())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(final InputZipContainsFolderNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(final NonAsciiOrNonPrintableCharNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                .setAltEntityValue(toExport.getFieldName())
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(final UnsupportedGtfsTypeNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .setAltEntityValue(toExport.getFieldName())
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(final CannotConstructDataProviderNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(CannotDownloadArchiveFromNetworkNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_FILE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(CannotParseFloatNotice toExport, OutputStream targetStream) throws IOException {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(),
                toExport.getRawValue(), targetStream);
    }

    private void parsingNoticeToProto(String filename, int lineNumber, String fieldName, String rawValue,
                                      OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(filename)
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setCsvColumnName(lineNumber, fieldName)
                .setEntityValue(rawValue)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(CannotParseIntegerNotice toExport, OutputStream targetStream) throws IOException {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(),
                toExport.getRawValue(), targetStream);
    }

    @Override
    public void export(CannotUnzipInputArchiveNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(FloatFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException {
        outOfRangeNoticeToProto(toExport.getFilename(), toExport.getEntityId(), toExport.getFieldName(),
                String.valueOf(toExport.getRangeMin()), String.valueOf(toExport.getRangeMax()),
                String.valueOf(toExport.getActualValue()), targetStream);
    }

    @Override
    public void export(IntegerFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException {
        outOfRangeNoticeToProto(toExport.getFilename(), toExport.getEntityId(), toExport.getFieldName(),
                String.valueOf(toExport.getRangeMin()), String.valueOf(toExport.getRangeMax()),
                String.valueOf(toExport.getActualValue()), targetStream);
    }

    private void outOfRangeNoticeToProto(String filename, String entityId, String fieldName, String rangeMinAsString,
                                         String rangeMaxAsString, String actualValueAsString, OutputStream targetStream)
            throws IOException {
        protoBuilder.setCsvFileName(filename)
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(entityId)
                .addCsvColumnName(fieldName)
                .setValue(rangeMinAsString)
                //TODO: FIXME - can't set altValue twice
                .setAltValue(rangeMaxAsString)
                .setAltValue(actualValueAsString)
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(InvalidRowLengthNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_BAD_NUMBER_OF_VALUES)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityRow(toExport.getRowIndex())
                .setAltEntityValue(String.valueOf(toExport.getExpectedLength()))
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(InvalidTimezoneNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_TIMEZONE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getTimezoneValue())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(InvalidUrlNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getUrlValue())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(MissingHeaderNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_COLUMN)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getMissingHeaderName())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(MissingRequiredFileNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_TABLE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFilename())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(MissingRequiredValueNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_VALUE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getEntityId())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(CouldNotCleanOrCreatePathNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getPathToCleanOrCreate())
                .build()
                .writeTo(targetStream);
    }

    @Override
    public void export(InvalidColorNotice toExport, OutputStream targetStream) throws IOException {
        protoBuilder.setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFieldName())
                .setAltEntityValue(toExport.getColorValue())
                .build()
                .writeTo(targetStream);
    }
}
