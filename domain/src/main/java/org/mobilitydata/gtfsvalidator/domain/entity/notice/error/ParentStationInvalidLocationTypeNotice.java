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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class ParentStationInvalidLocationTypeNotice extends ErrorNotice {

    public ParentStationInvalidLocationTypeNotice(final String childId,
                                                  final Integer childLocationType,
                                                  final String parentId,
                                                  final Integer expectedParentLocationType,
                                                  final Integer actualParentLocationType) {
        super("stops.txt", E_041,
                "Invalid parent `location_type`",
                "Stop with id:`" + childId + "` of type:`" + childLocationType + "` specify parent id:`" +
                        parentId + "` of type:`" + actualParentLocationType + "`. This is invalid. Expected parent" +
                        " `location_type` was:`" + expectedParentLocationType + "`.",
                childId);
        putNoticeSpecific(KEY_CHILD_LOCATION_TYPE, childLocationType);
        putNoticeSpecific(KEY_PARENT_ID, parentId);
        putNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE, expectedParentLocationType);
        putNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE, actualParentLocationType);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
