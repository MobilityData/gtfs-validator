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

public class FeedInfoExpiresInLessThan7DaysNotice extends ErrorNotice {
    private final String currentDate;
    private final String feedEndDate;
    private final String fieldName;

    public FeedInfoExpiresInLessThan7DaysNotice(final String currentDate,
                                                final String feedEndDate,
                                                final String entityId) {
        super("feed_info.txt", E_033, "Feed expiration date too close",
                "GTFS dataset should be valid for at least the next 7 days. Current date is: " +
                        currentDate + "Feed expires: "+ feedEndDate, entityId);
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
