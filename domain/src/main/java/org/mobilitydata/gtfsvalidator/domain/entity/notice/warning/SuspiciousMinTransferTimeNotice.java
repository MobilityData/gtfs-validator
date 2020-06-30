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

public class SuspiciousMinTransferTimeNotice extends WarningNotice {
    public SuspiciousMinTransferTimeNotice(
            final int rangeMin,
            final int rangeMax,
            final int actualValue,
            final String compositeKeyFirstPart,
            final String compositeKeySecondPart,
            final Object compositeKeyFirstValue,
            final Object compositeKeySecondValue) {
        super("transfers.txt", W_009,
                "Suspicious min_transfer_time integer value",
                "Suspicious value for field: min_transfer_time of entity with composite id:`" +
                        "`" + compositeKeyFirstPart + "`: " + "`" + compositeKeyFirstValue + "`" + "--" +
                        "`" + compositeKeySecondPart + "`: " + "`" + compositeKeySecondValue + "`." +
                        "` -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue,
                null);
        putNoticeSpecific(KEY_FIELD_NAME, "min_transfer_time");
        putNoticeSpecific(KEY_RANGE_MAX, rangeMax);
        putNoticeSpecific(KEY_RANGE_MIN, rangeMin);
        putNoticeSpecific(KEY_ACTUAL_VALUE, actualValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, compositeKeyFirstPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, compositeKeySecondPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, compositeKeyFirstValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, compositeKeySecondValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
