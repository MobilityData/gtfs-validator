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

import java.util.Objects;

import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING;

/**
 * Model class for an entity defined in stops.txt with location_type = 2
 */
public class Entrance extends LocationBase {
    @NotNull
    private final String parentStation;
    @SuppressWarnings("NotNullFieldNotInitialized") //initialized through setter
    @NotNull
    private WheelchairBoarding wheelchairBoarding;

    private Entrance(@NotNull String stopId,
                     @Nullable String stopCode,
                     @NotNull String stopName,
                     @Nullable String stopDesc,
                     @NotNull Float stopLat,
                     @NotNull Float stopLon,
                     @Nullable String zoneId,
                     @Nullable String stopUrl,
                     @NotNull String parentStation,
                     @Nullable String stopTimezone,
                     @Nullable WheelchairBoarding wheelchairBoarding) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone);
        setWheelchairBoarding(wheelchairBoarding);
        this.parentStation = parentStation;
    }

    public @NotNull String getParentStation() {
        return parentStation;
    }

    public @NotNull WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(final WheelchairBoarding toSet) {
        this.wheelchairBoarding = Objects.requireNonNullElse(toSet, INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING);
    }

    public static class EntranceBuilder extends LocationBaseBuilder {

        private String parentStation;
        private WheelchairBoarding wheelchairBoarding = INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING;
        private Integer originalWheelchairBoarding = Integer.MAX_VALUE; // required to distinguish optional field not set

        public EntranceBuilder parentStation(@NotNull final String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public EntranceBuilder wheelchairBoarding(@Nullable final Integer wheelchairBoarding) {
            this.wheelchairBoarding = WheelchairBoarding.fromInt(wheelchairBoarding);
            this.originalWheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public EntityBuildResult<?> build() {
            if (stopId == null || stopCode == null || stopLat == null || stopLon == null || parentStation == null) {
                if (stopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_id", null));
                }
                if (stopCode == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_code", stopId));
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
                if (wheelchairBoarding == INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING) {
                    if (!WheelchairBoarding.isEnumValueValid(originalWheelchairBoarding)) {
                        noticeCollection.add(new UnexpectedEnumValueNotice("stops.txt",
                                "wheelchair_boarding", stopId, originalWheelchairBoarding));
                    }
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Entrance(stopId, stopCode, stopName, stopDesc, stopLat, stopLon,
                        zoneId, stopUrl, parentStation, stopTimezone, wheelchairBoarding));
            }
        }

        public EntranceBuilder clear() {
            super.clearBase();
            parentStation = null;
            wheelchairBoarding = INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING;
            originalWheelchairBoarding = Integer.MAX_VALUE;
            return this;
        }
    }
}
