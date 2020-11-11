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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class OutOfMemoryNotice extends ErrorNotice {
    public OutOfMemoryNotice(final float datasetSizeMegaBytes, final int noticeCount) {
        super(null,
                E_061,
                "" +
                        "Out of memory notice",
                String.format("Out of memory error might have been raised because dataset was too big " +
                                "(dataset size: %f mb) or because too many notices were generated (notice count: %d)",
                        datasetSizeMegaBytes,
                        noticeCount),
                null);
        putNoticeSpecific(KEY_DATASET_SIZE_MEGABYTES, datasetSizeMegaBytes);
        putNoticeSpecific(KEY_DATASET_NOTICE_COUNT, noticeCount);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
