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

public class FeedInfoExpiresInLessThan30DaysNotice extends WarningNotice {
    public FeedInfoExpiresInLessThan30DaysNotice(final String fileName,
                                                 final String currentDateAsString,
                                                 final String feedEndDateAsString,
                                                 final String fieldName,
                                                 final String compositeKeyFirstPart,
                                                 final String compositeKeySecondPart,
                                                 final String compositeKeyThirdPart,
                                                 final String compositeKeyFirstValue,
                                                 final String compositeKeySecondValue,
                                                 final String compositeKeyThirdValue) {
        super("feed_info.txt",
                W_009,
                "Too close feed expiration date",
                String.format("If possible, GTFS dataset should be valid for at least the next 30 days. " +
                                "Current date is: `%s`. Feed expires: `%s` in field `%s` of file `%s` for entity with " +
                                "composite id: " +
                                "`%s`: `%s` -- " +
                                "`%s`: `%s` -- " +
                                "`%s`: `%s`.", currentDateAsString, feedEndDateAsString, fieldName, fileName,
                        compositeKeyFirstPart, compositeKeyFirstValue,
                        compositeKeySecondPart, compositeKeySecondValue,
                        compositeKeyThirdPart, compositeKeyThirdValue),
                null);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, compositeKeyFirstPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, compositeKeySecondPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_PART, compositeKeyThirdPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, compositeKeyFirstValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, compositeKeySecondValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_THIRD_VALUE, compositeKeyThirdValue);
        putNoticeSpecific(KEY_CURRENT_DATE, currentDateAsString);
        putNoticeSpecific(KEY_FEED_INFO_END_DATE, feedEndDateAsString);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
    }

    @Override
    public void export(NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
