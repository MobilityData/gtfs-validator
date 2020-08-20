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
    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String blockId,
                                                    final Integer tripFirstStopSequence,
                                                    final Integer tripLastStopSequence,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String conflictingTripId,
                                                    final Integer conflictingTripFirstStopSequence,
                                                    final Integer conflictingTripLastStopSequence,
                                                    final String conflictingTripFirstTime,
                                                    final String conflictingTripLastTime) {
        super("trips.txt",
                E_052,
                "Block trips must not have overlapping stop times",
                String.format("Trips with `trip_id`: `%s` and `%s` from block: `%s` overlap. " +
                                "Trip with `trip_id`: `%s` has first time: `%s`, last time: `%s` for stop_sequence" +
                                " `%s` to `%s`" +
                                " which conflicts with trip with `trip_id`: `%s` first time: `%s`, last time: `%s`" +
                                " for " +
                                "stop_sequence `%s` to `%s`", tripId, conflictingTripId, blockId, tripId,
                        tripFirstTime, tripLastTime, tripFirstStopSequence, tripLastStopSequence, conflictingTripId,
                        conflictingTripFirstTime, conflictingTripLastTime, conflictingTripFirstStopSequence,
                        conflictingTripLastStopSequence),
                tripId);

        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_CONFLICTING_TRIP_ID, conflictingTripId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_TIME, conflictingTripFirstTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_TIME, conflictingTripLastTime);
        putNoticeSpecific(KEY_TRIP_FIRST_STOP_SEQUENCE, tripFirstStopSequence);
        putNoticeSpecific(KEY_TRIP_LAST_STOP_SEQUENCE, tripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE, conflictingTripFirstStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE, conflictingTripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, "");
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, "");
    }

    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String blockId,
                                                    final Integer tripFirstStopSequence,
                                                    final Integer tripLastStopSequence,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String conflictingTripId,
                                                    final Integer conflictingTripFirstStopSequence,
                                                    final Integer conflictingTripLastStopSequence,
                                                    final String conflictingTripFirstTime,
                                                    final String conflictingTripLastTime,
                                                    final List<String> conflictingDayCollection) {
        super("trips.txt",
                E_052,
                "Block trips must not have overlapping stop times",
                String.format("Trips with `trip_id`: `%s` and `%s` from block: `%s` overlap. " +
                                "Trip with `trip_id`: `%s` has first time: `%s`, last time: `%s` for stop_sequence" +
                                " `%s` to `%s`" +
                                " which conflicts with trip with `trip_id`: `%s` first time: `%s`, last time: `%s`" +
                                " for stop_sequence `%s` to `%s`. Conflicts happen on: %s",
                        tripId, conflictingTripId, blockId, tripId,
                        tripFirstTime, tripLastTime, tripFirstStopSequence, tripLastStopSequence, conflictingTripId,
                        conflictingTripFirstTime, conflictingTripLastTime, conflictingTripFirstStopSequence,
                        conflictingTripLastStopSequence, conflictingDayCollection),
                tripId);

        putNoticeSpecific(KEY_TRIP_CONFLICTING_TRIP_ID, conflictingTripId);
        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_TIME, conflictingTripFirstTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_TIME, conflictingTripLastTime);
        putNoticeSpecific(KEY_TRIP_FIRST_STOP_SEQUENCE, tripFirstStopSequence);
        putNoticeSpecific(KEY_TRIP_LAST_STOP_SEQUENCE, tripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE, conflictingTripFirstStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE, conflictingTripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, conflictingDayCollection);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, "");
    }

    public BlockTripsWithOverlappingStopTimesNotice(final String tripId,
                                                    final String blockId,
                                                    final Integer tripFirstStopSequence,
                                                    final Integer tripLastStopSequence,
                                                    final String tripFirstTime,
                                                    final String tripLastTime,
                                                    final String conflictingTripId,
                                                    final Integer conflictingTripFirstStopSequence,
                                                    final Integer conflictingTripLastStopSequence,
                                                    final String conflictingTripFirstTime,
                                                    final String conflictingTripLastTime,
                                                    final Set<String> conflictingDateCollection) {
        super("trips.txt",
                E_052,
                "Block trips must not have overlapping stop times",
                String.format("Trips with `trip_id`: `%s` and `%s` from block: `%s` overlap. " +
                                "Trip with `trip_id`: `%s` has first time: `%s`, last time: `%s` for stop_sequence" +
                                " `%s` to `%s`" +
                                " which conflicts with trip with `trip_id`: `%s` first time: `%s`, last time: `%s`" +
                                " for stop_sequence `%s` to `%s`. Conflicts happen on: %s",
                        tripId, conflictingTripId, blockId, tripId,
                        tripFirstTime, tripLastTime, tripFirstStopSequence, tripLastStopSequence, conflictingTripId,
                        conflictingTripFirstTime, conflictingTripLastTime, conflictingTripFirstStopSequence,
                        conflictingTripLastStopSequence, conflictingDateCollection),
                tripId);

        putNoticeSpecific(KEY_TRIP_CONFLICTING_TRIP_ID, conflictingTripId);
        putNoticeSpecific(KEY_TRIP_BLOCK_ID, blockId);
        putNoticeSpecific(KEY_TRIP_FIRST_TIME, tripFirstTime);
        putNoticeSpecific(KEY_TRIP_LAST_TIME, tripLastTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_TIME, conflictingTripFirstTime);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_TIME, conflictingTripLastTime);
        putNoticeSpecific(KEY_TRIP_FIRST_STOP_SEQUENCE, tripFirstStopSequence);
        putNoticeSpecific(KEY_TRIP_LAST_STOP_SEQUENCE, tripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_FIRST_STOP_SEQUENCE, conflictingTripFirstStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_TRIP_LAST_STOP_SEQUENCE, conflictingTripLastStopSequence);
        putNoticeSpecific(KEY_CONFLICTING_DATE_LIST, conflictingDateCollection);
        putNoticeSpecific(KEY_CONFLICTING_DAY_LIST, "");
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
