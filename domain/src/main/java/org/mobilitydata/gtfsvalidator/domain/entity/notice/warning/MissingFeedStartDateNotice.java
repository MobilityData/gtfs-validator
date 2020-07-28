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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class MissingFeedStartDateNotice extends WarningNotice {
    public MissingFeedStartDateNotice(final String fileName,
                                      final String fieldName,
                                      final String compositeKeyFirstPart,
                                      final String compositeKeySecondPart,
                                      final String compositeKeyThirdPart,
                                      final String compositeKeyFirstValue,
                                      final String compositeKeySecondValue,
                                      final String compositeKeyThirdValue) {
        super("feed_info.txt",
                W_011,
                "Missing `" + fieldName + "`",
                "Missing `" + fieldName + "` in file: `" + fileName + "` with composite id: `" +
                        "`" + compositeKeyFirstPart + "`: `" + compositeKeyFirstValue + "` -- " +
                        "`" + compositeKeySecondPart + "`: `" + compositeKeySecondValue + "` -- " +
                        "`" + compositeKeyThirdPart + "`: `" + compositeKeyThirdValue + "`.",
                null);

        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, compositeKeyFirstPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, compositeKeySecondPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART, compositeKeyThirdPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, compositeKeyFirstValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, compositeKeySecondValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE, compositeKeyThirdValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
