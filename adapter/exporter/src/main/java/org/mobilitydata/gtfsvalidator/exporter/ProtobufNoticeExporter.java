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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;

public class ProtobufNoticeExporter implements NoticeExporter {

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
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_EXTRA_HEADER_NAME))
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
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
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
        parsingNoticeToProto(
                toExport.getFilename(),
                (int) toExport.getNoticeSpecific(KEY_LINE_NUMBER),
                (String) toExport.getNoticeSpecific(KEY_FIELD_NAME),
                (String) toExport.getNoticeSpecific(KEY_RAW_VALUE)
        );
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
        parsingNoticeToProto(
                toExport.getFilename(),
                (int) toExport.getNoticeSpecific(KEY_LINE_NUMBER),
                (String) toExport.getNoticeSpecific(KEY_FIELD_NAME),
                (String) toExport.getNoticeSpecific(KEY_RAW_VALUE)
        );
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
    public void export(final FloatFieldValueOutOfRangeNotice toExport) throws IOException {
        outOfRangeNoticeToProto(
                toExport.getFilename(),
                toExport.getEntityId(),
                (String) toExport.getNoticeSpecific(KEY_FIELD_NAME),
                String.valueOf(toExport.getNoticeSpecific(KEY_RANGE_MIN)),
                String.valueOf(toExport.getNoticeSpecific(KEY_RANGE_MAX)),
                String.valueOf(toExport.getNoticeSpecific(KEY_ACTUAL_VALUE)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE))
        );
    }

    @Override
    public void export(final IntegerFieldValueOutOfRangeNotice toExport) throws IOException {
        outOfRangeNoticeToProto(toExport.getFilename(),
                toExport.getEntityId(),
                (String) toExport.getNoticeSpecific(KEY_FIELD_NAME),
                String.valueOf(toExport.getNoticeSpecific(KEY_RANGE_MIN)),
                String.valueOf(toExport.getNoticeSpecific(KEY_RANGE_MAX)),
                String.valueOf(toExport.getNoticeSpecific(KEY_ACTUAL_VALUE)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)),
                String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE))
        );
    }

    private void outOfRangeNoticeToProto(final String filename, final String entityId, final String fieldName,
                                         final String rangeMinAsString,
                                         final String rangeMaxAsString, final String actualValueAsString,
                                         final String compositeKeyFirstPart, final String compositeKeySecondPart,
                                         final String compositeKeyFirstValue, final String compositeKeySecondValue)
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
                .setAltEntityName(compositeKeyFirstPart)
                .setEntityId(compositeKeySecondPart)
                .setEntityValue(compositeKeyFirstValue)
                .setAltEntityId(compositeKeySecondValue)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidRowLengthNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_BAD_NUMBER_OF_VALUES)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityRow((Integer) toExport.getNoticeSpecific(KEY_ROW_INDEX))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_EXPECTED_LENGTH)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidTimezoneNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_TIMEZONE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_TIMEZONE_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidUrlNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_URL_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingHeaderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_MISSING_COLUMN)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_MISSING_HEADER_NAME))
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
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue(toExport.getEntityId())
                .setValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART))
                .setAltValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE))
                .setAltEntityName((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidColorNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_COLOR_VALUE))
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
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_TIME_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotParseDateNotice toExport) throws IOException {
        parsingNoticeToProto(
                toExport.getFilename(),
                (int) toExport.getNoticeSpecific(KEY_LINE_NUMBER),
                (String) toExport.getNoticeSpecific(KEY_FIELD_NAME),
                (String) toExport.getNoticeSpecific(KEY_RAW_VALUE)
        );
    }

    @Override
    public void export(InvalidCurrencyCodeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setEntityId(toExport.getEntityId())
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_CURRENCY_CODE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(UnexpectedEnumValueNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_ENUM_VALUE)))
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final IllegalFieldValueCombination toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_CONFLICTING_FIELD_NAME))
                .setValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART))
                .setEntityValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART))
                .setAltValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE))
                .setAltEntityName((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(DuplicatedEntityNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_VALUE_ERROR)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityId(toExport.getEntityId())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidEmailNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_URL)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_EMAIL_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidLangNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_LANGUAGE_CODE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_FIELD_NAME))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_LANG_VALUE))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(SameNameAndDescriptionForRouteNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_STOP_WITH_SAME_NAME_AND_DESCRIPTION)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(RouteColorAndTextInsufficientContrastNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_COLOR_CONTRAST)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific("contrastRatio")))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(RouteShortNameTooLongNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_IS_TOO_LONG)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_SHORT_NAME_LENGTH))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(InvalidRouteTypeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_INVALID_ROUTE_TYPE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingShortAndLongNameForRouteNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingRouteShortNameNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingRouteLongNameNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_OR_LONG_NAME_REQUIRED)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(RouteLongNameEqualsShortNameNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_EQUALS_LONG_NAME)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(RouteLongNameContainsShortNameNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_ROUTE_SHORT_NAME_IS_CONTAINED_IN_LONG_NAME)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CalendarEndDateBeforeStartDateNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CALENDAR_START_AND_END_DATE_OUT_OF_ORDER)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final MissingAgencyIdNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(toExport.getFieldName())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final InconsistentAgencyTimezoneNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_AGENCIES_WITH_DIFFERENT_TIMEZONES)
                .setValue(toExport.getFieldName())
                .setEntityId(toExport.getConflictingTimezoneCollection())
                .setEntityValue(String.valueOf(toExport.getDistinctTimezoneCount()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final InvalidAgencyIdNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(toExport.getFieldName())
                .setEntityValue(String.valueOf(toExport.getEntityId()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final NonExistingRouteIdNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getEntityId())
                .setEntityValue(KEY_ROUTE_ID)
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_ROUTE_ID)))
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
