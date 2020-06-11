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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class RouteShortNameTooLongNotice extends WarningNotice {

    public RouteShortNameTooLongNotice(final String filename, final String entityId, final String shortNameLength) {
        super(filename, W_005,
                "Route short name too long",
                "Route short name length should be <= 12 characters but was " + shortNameLength + " for Route:" + entityId + " in file:" + filename,
                entityId);
        putExtra(NOTICE_SPECIFIC_KEY__SHORT_NAME_LENGTH, shortNameLength);
    }

    @Override
    public void export(final NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }
}