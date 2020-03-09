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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProtobufNoticeExporter implements ValidationResultRepository.NoticeExporter {

    private final String outputPath;
    private final GtfsValidationOutputProto.GtfsProblem.Builder protoBuilder;

    public ProtobufNoticeExporter(final String outputPath) {
        this.outputPath = outputPath;
        this.protoBuilder = GtfsValidationOutputProto.GtfsProblem.newBuilder();
    }

    @Override
    public void export(final NonStandardHeaderNotice toExport) {

        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");

            protoBuilder.setCsvFileName(toExport.getFilename())
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_UNKNOWN_COLUMN)
                    .setAltEntityValue(toExport.getExtraHeader())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(final InputZipContainsFolderNotice toExport) {

        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(final NonAsciiOrNonPrintableCharNotice toExport) {

        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.SUSPICIOUS_WARNING)
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                    .setAltEntityValue(toExport.getFieldName())
                    .setAltEntityId(toExport.getEntityId())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(final UnsupportedGtfsTypeNotice toExport) {

        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                    .setAltEntityValue(toExport.getFieldName())
                    .setAltEntityId(toExport.getEntityId())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(final CannotConstructDataProviderNotice toExport) {

        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(CannotDownloadArchiveFromNetworkNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_FILE_CORRUPTED)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(CannotParseFloatNotice toExport) {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(), toExport.getRawValue());
    }

    private void parsingNoticeToProto(String filename, int lineNumber, String fieldName, String rawValue) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(filename)
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setCsvColumnName(lineNumber, fieldName)
                    .setEntityValue(rawValue)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(CannotParseIntegerNotice toExport) {
        parsingNoticeToProto(toExport.getFilename(), toExport.getLineNumber(), toExport.getFieldName(), toExport.getRawValue());
    }

    @Override
    public void export(CannotUnzipInputArchiveNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ARCHIVE_CORRUPTED)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(FloatFieldValueOutOfRangeNotice toExport) {
        outOfRangeNoticeToProto(toExport.getFilename(), toExport.getEntityId(), toExport.getFieldName(), String.valueOf(toExport.getRangeMin()), String.valueOf(toExport.getRangeMax()), String.valueOf(toExport.getActualValue()));
    }

    @Override
    public void export(IntegerFieldValueOutOfRangeNotice toExport) {
        outOfRangeNoticeToProto(toExport.getFilename(), toExport.getEntityId(), toExport.getFieldName(), String.valueOf(toExport.getRangeMin()), String.valueOf(toExport.getRangeMax()), String.valueOf(toExport.getActualValue()));
    }

    private void outOfRangeNoticeToProto(String filename, String entityId, String fieldName, String s, String s2, String s3) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(filename)
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_OUT_OF_RANGE)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(entityId)
                    .addCsvColumnName(fieldName)
                    .setValue(s)
                    .setAltValue(s2)
                    .setAltValue(s3)
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(InvalidRowLengthNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_BAD_NUMBER_OF_ROWS)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityRow(toExport.getRowIndex())
                    .setAltEntityValue(String.valueOf(toExport.getExpectedLength()))
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(InvalidTimezoneNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_TIMEZONE)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getFieldName())
                    .setAltEntityValue(toExport.getTimezoneValue())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(InvalidUrlNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getFieldName())
                    .setAltEntityValue(toExport.getUrlValue())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(MissingHeaderNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_COLUMN)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getMissingHeaderName())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(MissingRequiredFileNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_TABLE)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getFilename())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(MissingRequiredValueNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_VALUE)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getFieldName())
                    .setAltEntityValue(toExport.getEntityId())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(CouldNotCleanOrCreatePathNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_UNKNOWN_ERROR)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getPathToCleanOrCreate())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(InvalidColorNotice toExport) {
        try {
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(this.outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + ".pb");
            this.protoBuilder
                    .setCsvFileName(toExport.getFilename())
                    .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                    .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                    .setAltEntityId(toExport.getFieldName())
                    .setAltEntityValue(toExport.getColorValue())
                    .build()
                    .writeTo(outputStream);
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
