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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.*;

import java.io.IOException;

public class JsonNoticeExporter implements NoticeExporter {

    private final JsonGenerator jsonGenerator;

    public JsonNoticeExporter(final JsonGenerator generator) {
        this.jsonGenerator = generator;
    }

    public static final String FILE_EXTENSION = ".json";

    @Override
    public String getExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public void exportBegin() throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("results");
        jsonGenerator.writeStartArray();
    }

    @Override
    public void exportEnd() throws IOException {
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.flush();
    }

    @Override
    public void export(final NonStandardHeaderNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InputZipContainsFolderNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final NonAsciiOrNonPrintableCharNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotConstructDataProviderNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotDownloadArchiveFromNetworkNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotParseFloatNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotParseIntegerNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotUnzipInputArchiveNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final FloatFieldValueOutOfRangeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final IntegerFieldValueOutOfRangeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidRowLengthNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidTimezoneNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidUrlNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingHeaderNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingRequiredFileNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingRequiredValueNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidColorNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final ExtraFileFoundNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidTimeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final CannotParseDateNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidCurrencyCodeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final UnexpectedEnumValueNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final IllegalFieldValueCombinationNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final DuplicatedEntityNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidEmailNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidLangNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final SameNameAndDescriptionForRouteNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final RouteColorAndTextInsufficientContrastNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final RouteShortNameTooLongNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidRouteTypeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingShortAndLongNameForRouteNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingRouteShortNameNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingRouteLongNameNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final RouteLongNameEqualsShortNameNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final RouteLongNameContainsShortNameNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CalendarEndDateBeforeStartDateNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final MissingAgencyIdNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InconsistentAgencyTimezoneNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(final InvalidAgencyIdNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }
}
