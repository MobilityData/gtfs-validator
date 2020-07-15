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

package org.mobilitydata.gtfsvalidator.domain.entity.stops;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.INVALID_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;

/**
 * Model class for an entity defined in stops.txt with location_type = 1
 */
public class Station extends LocationBase {

    @NotNull
    private final WheelchairBoarding wheelchairBoarding;
    @Nullable
    private final String levelId;

    private Station(@NotNull final String stopId,
                    @Nullable final String stopCode,
                    @NotNull final String stopName,
                    @Nullable final String stopDesc,
                    @NotNull final Float stopLat,
                    @NotNull final Float stopLon,
                    @Nullable final String zoneId,
                    @Nullable final String stopUrl,
                    @Nullable final String stopTimezone,
                    @NotNull final WheelchairBoarding wheelchairBoarding,
                    @Nullable final String levelId,
                    @Nullable final List<String> children) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone, children);
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
    }

    public @NotNull WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public @Nullable String getLevelId() {
        return levelId;
    }

    public static class StationBuilder extends LocationBaseBuilder {

        private String levelId;
        private WheelchairBoarding wheelchairBoarding = INVALID_VALUE;
        private Integer originalWheelchairBoarding = Integer.MAX_VALUE; // required to distinguish optional field not set

        public StationBuilder levelId(@Nullable final String levelId) {
            this.levelId = levelId;
            return this;
        }

        public StationBuilder wheelchairBoarding(@Nullable final Integer wheelchairBoarding) {
            this.wheelchairBoarding = WheelchairBoarding.fromInt(wheelchairBoarding);
            this.originalWheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public EntityBuildResult<?> build() {
            if (stopId == null || stopName == null || stopLat == null || stopLon == null) {
                if (stopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_id", null));
                }
                if (stopName == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_name", stopId));
                }
                if (stopLat == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_lat", stopId));
                }
                if (stopLon == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_lon", stopId));
                }
                if (wheelchairBoarding == INVALID_VALUE) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stops.txt",
                            "wheelchair_boarding", stopId, originalWheelchairBoarding));
                }

                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Station(stopId, stopCode, stopName, stopDesc, stopLat, stopLon,
                        zoneId, stopUrl, stopTimezone, wheelchairBoarding, levelId, childrenIdList));
            }
        }

        public StationBuilder clear() {
            super.clearBase();
            levelId = null;
            wheelchairBoarding = UNKNOWN_WHEELCHAIR_BOARDING;
            originalWheelchairBoarding = Integer.MAX_VALUE;
            return this;
        }
    }
}
