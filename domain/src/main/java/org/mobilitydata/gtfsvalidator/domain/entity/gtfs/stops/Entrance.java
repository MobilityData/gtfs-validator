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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.WheelchairBoarding.INVALID_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;

/**
 * Model class for an entity defined in stops.txt with location_type = 2. Can't be constructed directly.
 * Use {@link EntranceBuilder} to instantiate
 */
public class Entrance extends LocationBase {
    @NotNull
    private final String parentStation;
    @NotNull
    private final WheelchairBoarding wheelchairBoarding;

    private Entrance(@NotNull final String stopId,
                     @Nullable final String stopCode,
                     @NotNull final String stopName,
                     @Nullable final String stopDesc,
                     @NotNull final Float stopLat,
                     @NotNull final Float stopLon,
                     @Nullable final String zoneId,
                     @Nullable final String stopUrl,
                     @NotNull final String parentStation,
                     @Nullable final String stopTimezone,
                     @NotNull final WheelchairBoarding wheelchairBoarding,
                     @Nullable final List<String> children) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone, children);
        this.parentStation = parentStation;
        this.wheelchairBoarding = wheelchairBoarding;
    }

    public @NotNull String getParentStation() {
        return parentStation;
    }

    public @NotNull WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public static class EntranceBuilder extends LocationBaseBuilder {

        private String parentStation;
        private WheelchairBoarding wheelchairBoarding = INVALID_VALUE;
        private Integer originalWheelchairBoarding = Integer.MAX_VALUE; // required to distinguish optional field not set

        public EntranceBuilder parentStation(final String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public EntranceBuilder wheelchairBoarding(@Nullable final Integer wheelchairBoarding) {
            this.wheelchairBoarding = WheelchairBoarding.fromInt(wheelchairBoarding);
            this.originalWheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public EntityBuildResult<?> build() {
            if (stopId == null || stopName == null || stopLat == null || stopLon == null || parentStation == null
                    || wheelchairBoarding == INVALID_VALUE) {
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
                if (parentStation == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "parent_station", stopId));
                }
                if (wheelchairBoarding == INVALID_VALUE) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stops.txt",
                            "wheelchair_boarding", stopId, originalWheelchairBoarding));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Entrance(stopId, stopCode, stopName, stopDesc, stopLat, stopLon,
                        zoneId, stopUrl, parentStation, stopTimezone, wheelchairBoarding, childrenIdList));
            }
        }

        public EntranceBuilder clear() {
            super.clear();
            parentStation = null;
            wheelchairBoarding = UNKNOWN_WHEELCHAIR_BOARDING;
            originalWheelchairBoarding = Integer.MAX_VALUE;
            return this;
        }
    }
}
