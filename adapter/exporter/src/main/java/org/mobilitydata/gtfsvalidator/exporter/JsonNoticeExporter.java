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
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;

public class JsonNoticeExporter implements ValidationResultRepository.NoticeExporter {

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
    public void export(CannotDownloadArchiveFromNetworkNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CannotParseFloatNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CannotParseIntegerNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CannotUnzipInputArchiveNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(FloatFieldValueOutOfRangeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(IntegerFieldValueOutOfRangeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(InvalidRowLengthNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(InvalidTimezoneNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(InvalidUrlNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(MissingHeaderNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(MissingRequiredFileNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(MissingRequiredValueNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CouldNotCleanOrCreatePathNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(InvalidColorNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(ExtraFileFoundNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(InvalidTimeNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(CannotParseDateNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(UnexpectedValueNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(IncoherentValuesForFieldsNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(UnexpectedDefinedFieldNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }

    @Override
    public void export(EntityMustBeUniqueNotice toExport) throws IOException {
        jsonGenerator.writeObject(toExport);
    }
}
