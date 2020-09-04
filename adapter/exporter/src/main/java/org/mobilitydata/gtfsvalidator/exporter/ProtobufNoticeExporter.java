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

import static org.mobilitydata.gtfsvalidator.adapter.protos.GtfsValidationOutputProto.GtfsProblem.Type.*;
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
    public void export(DuplicatedHeaderNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_CSV_DUPLICATE_COLUMN_NAME)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId((String) toExport.getNoticeSpecific(KEY_DUPLICATED_HEADER_NAME))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(MissingRequiredFileNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(TYPE_CSV_MISSING_TABLE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(toExport.getFilename())
                .build()
                .writeTo(streamGenerator.getStream());
    }

    private void parsingEmptyFileNoticeToProto(final String filename,
                                               final GtfsValidationOutputProto.GtfsProblem.Severity severity)
            throws IOException {
        protoBuilder.clear()
                .setCsvFileName(filename)
                .setType(TYPE_CSV_BAD_NUMBER_OF_ROWS)
                .setSeverity(severity)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final EmptyFileErrorNotice toExport) throws IOException {
        parsingEmptyFileNoticeToProto(toExport.getFilename(), GtfsValidationOutputProto.GtfsProblem.Severity.ERROR);
    }

    @Override
    public void export(final EmptyFileWarningNotice toExport) throws IOException {
        parsingEmptyFileNoticeToProto(toExport.getFilename(), GtfsValidationOutputProto.GtfsProblem.Severity.WARNING);
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
    public void export(MissingTripEdgeStopTimeNotice toExport) throws IOException {
        String fieldName = (String) toExport.getNoticeSpecific(KEY_FIELD_NAME);
        GtfsValidationOutputProto.GtfsProblem.Type problemType = fieldName.contains("departure") ?
                GtfsValidationOutputProto.GtfsProblem.Type.TYPE_TRIP_WITH_NO_TIME_FOR_FIRST_STOP_TIME :
                GtfsValidationOutputProto.GtfsProblem.Type.TYPE_TRIP_WITH_NO_TIME_FOR_LAST_STOP_TIME;

        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(problemType)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltEntityId(fieldName)
                .setValue("trip_id")
                .setAltEntityValue("stop_sequence")
                .setAltValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(CannotParseColorNotice toExport) throws IOException {
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
    public void export(final IllegalFieldValueCombinationNotice toExport) throws IOException {
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
                .setEntityId(toExport.getEntityId())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
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
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityValue(String.valueOf(toExport.getEntityId()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final AgencyIdNotFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityValue(String.valueOf(toExport.getEntityId()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final RouteIdNotFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getEntityId())
                .setEntityValue((String) toExport.getNoticeSpecific(KEY_UNKNOWN_ROUTE_ID))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_UNKNOWN_ROUTE_ID)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final StationWithParentStationNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getEntityId())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_STATION_WITH_PARENT_STATION)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final ParentStationInvalidLocationTypeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getEntityId())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_PARENT_STATION_WITH_WRONG_LOCATION_TYPE)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final ShapeIdNotFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_UNKNOWN_SHAPE_ID)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final TripIdNotFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_UNKNOWN_TRIP_ID)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final ShapeNotUsedNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityId(String.valueOf(toExport.getEntityId()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final ServiceIdNotFoundNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(toExport.getEntityId())
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setEntityValue(KEY_UNKNOWN_SERVICE_ID)
                .setAltEntityValue(String.valueOf(String.valueOf(toExport.getNoticeSpecific(KEY_UNKNOWN_SERVICE_ID))))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final FeedInfoStartDateAfterEndDateNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_INFO_START_AND_END_DATE_OUT_OF_ORDER)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_FEED_INFO_START_DATE)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_FEED_INFO_END_DATE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final FeedInfoExpiresInLessThan7DaysNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_EXPIRATION_DATE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_CURRENT_DATE)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_FEED_INFO_END_DATE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final FeedInfoExpiresInLessThan30DaysNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FEED_EXPIRATION_DATE)
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_CURRENT_DATE)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_FEED_INFO_END_DATE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final FeedInfoLangAgencyLangMismatchNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setEntityId(toExport.getEntityId())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setType(TYPE_AGENCY_LANG_AND_FEED_LANG_MISMATCH)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_AGENCY_NAME)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_AGENCY_AGENCY_LANG)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_FEED_INFO_FEED_LANG)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final MissingFeedEndDateNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final MissingFeedStartDateNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.WARNING)
                .setEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_FIELD_NAME)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final StopTimeArrivalTimeAfterDepartureTimeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void export(final FastTravelBetweenStopsNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setType(((List<Integer>) toExport.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE_LIST)).size() > 2 ?
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FAST_TRAVEL_BETWEEN_FAR_STOPS :
                        GtfsValidationOutputProto.GtfsProblem.Type.TYPE_FAST_TRAVEL_BETWEEN_CONSECUTIVE_STOPS
                )
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE))
                .setEntityValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE_LIST)))
                .setAltEntityValue((String) toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final FrequencyStartTimeAfterEndTimeNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final BackwardsTimeTravelInStopNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME)))
                .setEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final TripNotUsedNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(String.valueOf(toExport.getEntityId()))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final UnusableTripNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(String.valueOf(toExport.getEntityId()))
                .setType(TYPE_TRIP_WITH_NO_USABLE_STOPS)
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final MissingCalendarAndCalendarDateFilesNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setType(TYPE_CSV_MISSING_TABLE)
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_OTHER_MISSING_FILENAME)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final OverlappingTripFrequenciesNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_FREQUENCY_START_TIME)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_FREQUENCY_END_TIME)))
                .setEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_PREVIOUS_FREQUENCY_START_TIME)))
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_PREVIOUS_FREQUENCY_END_TIME)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final BlockTripsWithOverlappingStopTimesNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityId(String.valueOf(toExport.getEntityId()))
                .setType(TYPE_BLOCK_TRIPS_WITH_OVERLAPPING_STOP_TIMES)
                .setEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_TRIP_TRIP_ID)))
                .setCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_TRIP_FIRST_TIME)))
                .setAltEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_TRIP_LAST_TIME)))
                .setAltEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_TRIP_PREVIOUS_TRIP_ID)))
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_PREVIOUS_TRIP_FIRST_TIME)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_PREVIOUS_TRIP_LAST_TIME)))
                .setAltValue(String.valueOf(toExport.getNoticeSpecific(KEY_TRIP_BLOCK_ID)))
                .setParentEntityId(String.valueOf(toExport.getNoticeSpecific(KEY_CONFLICTING_DATE_LIST)))
                .build()
                .writeTo(streamGenerator.getStream());
    }

    @Override
    public void export(final DecreasingStopTimeDistanceNotice toExport) throws IOException {
        protoBuilder.clear()
                .setCsvFileName(toExport.getFilename())
                .setSeverity(GtfsValidationOutputProto.GtfsProblem.Severity.ERROR)
                .setEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART)))
                .setEntityName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART)))
                .setOtherCsvFileName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE)))
                .setOtherCsvKeyName(String.valueOf(toExport.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE)))
                .setAltEntityValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE)))
                .setValue(String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED)))
                .setAltEntityName(
                        String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_PREVIOUS_SHAPE_DIST_TRAVELED)))
                .setParentEntityName(
                        String.valueOf(toExport.getNoticeSpecific(KEY_STOP_TIME_PREVIOUS_STOP_SEQUENCE)))
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
