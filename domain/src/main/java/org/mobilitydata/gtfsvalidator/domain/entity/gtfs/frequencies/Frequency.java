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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in transfers.txt. Can not be directly instantiated: user must use the
 * {@link FrequencyBuilder} to create this.
 */
public class Frequency extends GtfsEntity {
    @NotNull
    private final String tripId;
    @NotNull
    private final Integer startTime;
    @NotNull
    private final Integer endTime;
    @NotNull
    private final Integer headwaySecs;
    @Nullable
    private final ExactTimes exactTimes;

    private Frequency(@NotNull String tripId,
                      @NotNull Integer startTime,
                      @NotNull Integer endTime,
                      @NotNull Integer headwaySecs,
                      @Nullable ExactTimes exactTimes) {
        this.tripId = tripId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headwaySecs = headwaySecs;
        this.exactTimes = exactTimes;
    }

    @NotNull
    public String getTripId() {
        return tripId;
    }

    @NotNull
    public Integer getStartTime() {
        return startTime;
    }

    @NotNull
    public Integer getEndTime() {
        return endTime;
    }

    @NotNull
    public Integer getHeadwaySecs() {
        return headwaySecs;
    }

    @Nullable
    public ExactTimes isExactTimes() {
        return exactTimes;
    }

    /**
     * Builder class to create {@link Frequency} objects. Allows an unordered definition of the different attributes of
     * {@link Frequency}.
     */
    public static class FrequencyBuilder {
        private String tripId;
        private Integer startTime;
        private Integer endTime;
        private Integer headwaySecs;
        private ExactTimes exactTimes;
        private String originalStartTime;
        private String originalEndTime;
        private Integer originalExactTimes;
        private final List<Notice> noticeCollection = new ArrayList<>();


        /**
         * Sets field tripId value and returns this
         *
         * @param tripId identifies a trip class
         * @return builder for future object creation
         */
        public FrequencyBuilder tripId(@NotNull String tripId) {
            this.tripId = tripId;
            return this;
        }

        /**
         * Sets field startTime value and returns this
         *
         * @param startTime identifies a start time for a trip service at the first stop
         * @return builder for future object creation
         */
        public FrequencyBuilder startTime(@NotNull String startTime) {
            this.startTime = convertTimeToInteger(startTime);
            this.originalStartTime = startTime;
            return this;
        }

        /**
         * Sets field endTime value and returns this
         *
         * @param endTime identifies an end time for a trip service at the first stop
         * @return builder for future object creation
         */
        public FrequencyBuilder endTime(@NotNull String endTime) {
            this.endTime = convertTimeToInteger(endTime);
            this.originalEndTime = endTime;
            return this;
        }

        /**
         * Sets field endTime value and returns this
         *
         * @param headwaySecs identifies times in seconds between departures in the same headway for a trip service
         * @return builder for future object creation
         */
        public FrequencyBuilder headwaySecs(@NotNull Integer headwaySecs) {
            this.headwaySecs = headwaySecs;
            return this;
        }

        /**
         * Sets field exactTimes value and returns this
         *
         * @param exactTimes identifies an end time for a trip
         * @return builder for future object creation
         */
        public FrequencyBuilder exactTimes(@Nullable Integer exactTimes) {
            this.exactTimes = ExactTimes.fromInt(exactTimes);
            this.originalExactTimes = exactTimes;
            return this;
        }

        public EntityBuildResult<?> build() {
            noticeCollection.clear();
            if (tripId == null || startTime == null || endTime == null || headwaySecs == null ||
                    headwaySecs < 0 || exactTimes == null) {

                if (tripId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("frequencies.txt",
                            "trip_id", tripId));
                }

                if (startTime == null && originalStartTime == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("frequencies.txt",
                            "start_time", tripId));
                }
                //else if (startTime == null && originalStartTime != null) InvalidTimeNotice?

                if (endTime == null && originalEndTime == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("frequencies.txt",
                            "end_time", tripId));
                }
                //else if (endTime == null && originalEndTime != null) InvalidTimeNotice?

                if (headwaySecs == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("frequencies.txt",
                            "headway_secs", tripId));
                } else if (headwaySecs < 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("frequencies.txt",
                            "headway_secs", tripId, 0, Integer.MAX_VALUE, headwaySecs));
                }

                // the following statement is true when ExactTimes.isEnumValueValid(originalTransferInteger)
                // returns false
                if (exactTimes == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("frequencies.txt",
                            "exact_times", tripId, originalExactTimes));
                }
                return new EntityBuildResult<>(noticeCollection);
            }
            return new EntityBuildResult<>(new Frequency(tripId, startTime, endTime, headwaySecs, exactTimes));
        }
    }

    public static Integer convertTimeToInteger(String timeString) {
        Integer timeValue = null;
        if (timeString != null && timeString.matches("([0-9][0-9]|[0-9]):[0-5][0-9]:[0-5][0-9]")) {
            String[] timeStringSplit = timeString.split(":");
            String hourString = timeStringSplit[0];
            String minuteString = timeStringSplit[1];
            String secondString = timeStringSplit[2];
            Integer hourValue = Integer.valueOf(hourString);
            Integer minuteValue = Integer.valueOf(minuteString);
            Integer secondValue = Integer.valueOf(secondString);

            //Converting time to an Integer value representing number of seconds
            timeValue = hourValue * 3600 + minuteValue * 60 + secondValue;
            //Setting noon as point zero, subtracting 12 * 3600 seconds
            timeValue = timeValue - 12 * 3600;
        }
        return timeValue;
    }
}
