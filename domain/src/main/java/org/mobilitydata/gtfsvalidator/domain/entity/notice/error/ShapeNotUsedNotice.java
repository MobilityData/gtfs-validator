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

public class ShapeNotUsedNotice extends ErrorNotice {
    public ShapeNotUsedNotice(final String entityId, final String fieldName) {
        super("shapes.txt",
                E_038,
                "Unused shape",
                String.format("All records defined by GTFS `shapes.txt` should be used in `trips.txt`. " +
                                "File `shapes.txt` defines entity with id: `%s` that is not used.",
                        entityId),
                entityId);

        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
    }

    @Override
    public void export(NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
