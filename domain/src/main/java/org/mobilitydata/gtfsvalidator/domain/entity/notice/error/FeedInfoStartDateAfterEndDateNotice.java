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

public class FeedInfoStartDateAfterEndDateNotice extends ErrorNotice {
    final private String startDate;
    final private String endDate;

    public FeedInfoStartDateAfterEndDateNotice(final String startDateAsString,
                                               final String endDateAsString,
                                               final String entityId ) {
        super("feed_info.txt",
                E_032,
                "Fields `feed_start_date` af `feed_end_date` out of order",
                "`The feed_end_date` date must not precede the `feed_start_date` date if both are given. " +
                        "Record with `feed_publisher_name`: " + entityId + " `feed_end_date` is: " + endDateAsString
                        + "`feed_start_date` is: " + startDateAsString, entityId);
        this.startDate = startDateAsString;
        this.endDate = endDateAsString;
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
