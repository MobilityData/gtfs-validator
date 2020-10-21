/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.info;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.InfoNotice;

import java.io.IOException;

public class ValidationProcessInfoNotice extends InfoNotice {
    public ValidationProcessInfoNotice(final String feedPublisherNameOrAgencyName,
                                       final String validationTimestamp,
                                       final int warningNoticeCount,
                                       final int errorNoticeCount,
                                       final String pathOrUrlToGtfsArchive,
                                       final float gtfsArchiveSizeBeforeUnzipping,
                                       final float gtfsArchiveSizeAfterUnzipping,
                                       final String gtfsValidatorVersion,
                                       final String processedFilenameCollection,
                                       final int processingTime) {
        super(null, I_001, null, null, null);
        putNoticeSpecific(FEED_PUBLISHER_NAME_OR_AGENCY_NAME, feedPublisherNameOrAgencyName);
        putNoticeSpecific(VALIDATION_TIMESTAMP, validationTimestamp);
        putNoticeSpecific(WARNING_NOTICE_COUNT, warningNoticeCount);
        putNoticeSpecific(ERROR_NOTICE_COUNT, errorNoticeCount);
        putNoticeSpecific(PATH_OR_URL_TO_GTFS_ARCHIVE, pathOrUrlToGtfsArchive);
        putNoticeSpecific(GTFS_ARCHIVE_SIZE_BEFORE_UNZIPPING, gtfsArchiveSizeBeforeUnzipping);
        putNoticeSpecific(GTFS_ARCHIVE_SIZE_AFTER_UNZIPPING, gtfsArchiveSizeAfterUnzipping);
        putNoticeSpecific(GTFS_VALIDATOR_VERSION, gtfsValidatorVersion);
        putNoticeSpecific(PROCESSING_TIME, processingTime);
        putNoticeSpecific(PROCESSED_FILENAME_COLLECTION, processedFilenameCollection);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
