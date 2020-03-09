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

import java.util.Collection;

public interface ValidationResultRepository {

    InfoNotice addNotice(InfoNotice newInfo);

    WarningNotice addNotice(WarningNotice newWarning);

    ErrorNotice addNotice(ErrorNotice newError);

    Notice addNotice(Notice newNotice);

    Collection<Notice> getAll();

    NoticeExporter getExporter(String outputPath);

    // an interface that will be implemented in different flavors: proto, json, string, ...
    interface NoticeExporter {

        void export(NonStandardHeaderNotice toExport);

        void export(InputZipContainsFolderNotice toExport);

        void export(NonAsciiOrNonPrintableCharNotice toExport);

        void export(UnsupportedGtfsTypeNotice toExport);

        void export(CannotConstructDataProviderNotice toExport);

        void export(CannotDownloadArchiveFromNetworkNotice toExport);

        void export(CannotParseFloatNotice toExport);

        void export(CannotParseIntegerNotice toExport);

        void export(CannotUnzipInputArchiveNotice toExport);

        void export(FloatFieldValueOutOfRangeNotice toExport);

        void export(IntegerFieldValueOutOfRangeNotice toExport);

        void export(InvalidRowLengthNotice toExport);

        void export(InvalidTimezoneNotice toExport);

        void export(InvalidUrlNotice toExport);

        void export(MissingHeaderNotice toExport);

        void export(MissingRequiredFileNotice toExport);

        void export(MissingRequiredValueNotice toExport);

        void export(CouldNotCleanOrCreatePathNotice toExport);

        void export(InvalidColorNotice toExport);
    }
}