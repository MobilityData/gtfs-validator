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
    private final String currentDate;
    private final String feedEndDate;
    private final String fieldName;

    public FeedInfoExpiresInLessThan30DaysNotice(final String currentDate,
                                                 final String feedEndDate,
                                                 final String entityId) {
        super("feed_info.txt", W_009, "Too close feed expiration date",
                "If possible, the GTFS dataset should cover at least the next 30 days of service. " +
                        "Current date is: " + currentDate +
                        "Feed expires: "+ feedEndDate, entityId);
        this.currentDate = currentDate;
        this.feedEndDate = feedEndDate;
        this.fieldName = "feed_end_date";
    }

    @Override
    public void export(NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getFeedEndDate() {
        return feedEndDate;
    }

    public String getFieldName() {
        return fieldName;
    }
}
