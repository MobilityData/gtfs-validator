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

public class ShapeIdNotFoundNotice extends ErrorNotice {
    public ShapeIdNotFoundNotice(final String filename, final String fieldName, final String compositeKeyFirstPart,
                                 final String compositeKeySecondPart, final Object compositeKeyFirstValue,
                                 final Object compositeKeySecondValue, final String shapeId) {
        super(filename,
                E_034,
                "Value of field `shape_id` should exist in GTFS `shapes.txt` data",
                String.format("Entity of GTFS file `%s` with composite key: (`%s`=`%s` ; `%s`=`%s`) " +
                        "refers to GTFS `shape_id`: `%s`  which does not exist in GTFS data",
                        filename,
                        compositeKeyFirstPart,
                        compositeKeyFirstValue,
                        compositeKeySecondPart,
                        compositeKeySecondValue,
                        shapeId),
                null);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, compositeKeyFirstPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, compositeKeySecondPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, compositeKeyFirstValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, compositeKeySecondValue);
        putNoticeSpecific(KEY_UNKNOWN_SHAPE_ID, shapeId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
