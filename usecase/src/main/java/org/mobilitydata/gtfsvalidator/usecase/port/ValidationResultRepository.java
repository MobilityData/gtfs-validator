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

package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.info.UnsupportedGtfsTypeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface ValidationResultRepository {

    InfoNotice addNotice(InfoNotice newInfo);

    WarningNotice addNotice(WarningNotice newWarning);

    ErrorNotice addNotice(ErrorNotice newError);

    Notice addNotice(Notice newNotice);

    Collection<Notice> getAll();

    NoticeExporter getExporter();

    // an interface that will be implemented in different flavors: proto, json, string, ...
    interface NoticeExporter {

        String getExtension();

        void export(NonStandardHeaderNotice toExport, OutputStream targetStream) throws IOException;

        void export(InputZipContainsFolderNotice toExport, OutputStream targetStream) throws IOException;

        void export(NonAsciiOrNonPrintableCharNotice toExport, OutputStream targetStream) throws IOException;

        void export(UnsupportedGtfsTypeNotice toExport, OutputStream targetStream) throws IOException;

        void export(CannotConstructDataProviderNotice toExport, OutputStream targetStream) throws IOException;

        void export(CannotDownloadArchiveFromNetworkNotice toExport, OutputStream targetStream) throws IOException;

        void export(CannotParseFloatNotice toExport, OutputStream targetStream) throws IOException;

        void export(CannotParseIntegerNotice toExport, OutputStream targetStream) throws IOException;

        void export(CannotUnzipInputArchiveNotice toExport, OutputStream targetStream) throws IOException;

        void export(FloatFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException;

        void export(IntegerFieldValueOutOfRangeNotice toExport, OutputStream targetStream) throws IOException;

        void export(InvalidRowLengthNotice toExport, OutputStream targetStream) throws IOException;

        void export(InvalidTimezoneNotice toExport, OutputStream targetStream) throws IOException;

        void export(InvalidUrlNotice toExport, OutputStream targetStream) throws IOException;

        void export(MissingHeaderNotice toExport, OutputStream targetStream) throws IOException;

        void export(MissingRequiredFileNotice toExport, OutputStream targetStream) throws IOException;

        void export(MissingRequiredValueNotice toExport, OutputStream targetStream) throws IOException;

        void export(CouldNotCleanOrCreatePathNotice toExport, OutputStream targetStream) throws IOException;

        void export(InvalidColorNotice toExport, OutputStream targetStream) throws IOException;
    }
}