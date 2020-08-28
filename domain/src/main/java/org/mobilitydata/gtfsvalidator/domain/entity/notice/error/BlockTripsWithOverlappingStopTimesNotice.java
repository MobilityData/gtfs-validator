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
import java.util.List;
import java.util.Set;

public class BlockTripsWithOverlappingStopTimesNotice extends ErrorNotice {
    // This constructor is used when generating notice related to E054: conflicting trips have same service_id
    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String previousTripId,
                                                    final String previousTripFirstTime,
                                                    final String previousTripLastTime,
                                                    final String blockId) {
        super("trips.txt",
                E_054,
                "Overlapping stop times in trip block",
                String.format("Trips with `trip_id`: `%s` (first time: `%s`, last time: `%s`)" +
                                " and `%s` (first time: `%s`, last time: `%s`) from block: `%s` overlap.",
                        tripId, tripFirstTime, tripLastTime, previousTripId, previousTripFirstTime,
                        previousTripLastTime, blockId),
                tripId);

        putNoticeSpecific(KEY_TRIP_TRIP_ID, tripId);
        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_TRIP_PREVIOUS_TRIP_ID, previousTripId);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_FIRST_TIME, previousTripFirstTime);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_LAST_TIME, previousTripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, "");
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, "");
    }

    // This constructor is used when generating notice related to E054: conflicting trips have different service_id
    // and file `calendar.txt` is provided.
    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String previousTripId,
                                                    final String previousTripFirstTime,
                                                    final String previousTripLastTime,
                                                    final String blockId,
                                                    final List<String> conflictingDayCollection) {
        super("trips.txt",
                E_054,
                "Block trips must not have overlapping stop times",
                String.format("Trips with `trip_id`: `%s` (first time: `%s`, last time: `%s`)" +
                                " and `%s` (first time: `%s`, last time: `%s`) from block: `%s` overlap." +
                                " Conflicts happen on: %s.",
                        tripId, tripFirstTime, tripLastTime, previousTripId, previousTripFirstTime,
                        previousTripLastTime, blockId, conflictingDayCollection),
                tripId);

        putNoticeSpecific(KEY_TRIP_TRIP_ID, tripId);
        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_TRIP_PREVIOUS_TRIP_ID, previousTripId);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_FIRST_TIME, previousTripFirstTime);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_LAST_TIME, previousTripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, "");
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, conflictingDayCollection);
    }

    // This constructor is used when generating notice related to E054: conflicting trips have different service_id
    // and only file `calendar_dates.txt` is provided.
    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String previousTripId,
                                                    final String previousTripFirstTime,
                                                    final String previousTripLastTime,
                                                    final String blockId,
                                                    final Set<String> conflictingDateCollection) {
        super("trips.txt",
                E_054,
                "Block trips must not have overlapping stop times",
                String.format("Trips with `trip_id`: `%s` (first time: `%s`, last time: `%s`)" +
                                " and `%s` (first time: `%s`, last time: `%s`) from block: `%s` overlap." +
                                " Conflicts happen on: %s.",
                        tripId, tripFirstTime, tripLastTime, previousTripId, previousTripFirstTime,
                        previousTripLastTime, blockId, conflictingDateCollection),
                tripId);

        putNoticeSpecific(KEY_TRIP_TRIP_ID, tripId);
        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_TRIP_PREVIOUS_TRIP_ID, previousTripId);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_FIRST_TIME, previousTripFirstTime);
        putNoticeSpecific(KEY_PREVIOUS_TRIP_LAST_TIME, previousTripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, conflictingDateCollection);
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, "");
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
