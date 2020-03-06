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

import java.util.Objects;

/**
 * Model class for an entity defined in stops.txt with location_type = 2
 */
public class Entrance extends LocationBase {

    public WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(WheelchairBoarding toSet) {
        this.wheelchairBoarding = Objects.requireNonNullElse(toSet, WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING);
    }

    public String getParentStation() {
        return parentStation;
    }

    private final String parentStation;
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
        this.wheelchairBoarding = wheelchairBoarding;
        this.parentStation = parentStation;
    }

    public static class EntranceBuilder extends LocationBaseBuilder {

        private String parentStation;

        private WheelchairBoarding wheelchairBoarding = WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;

        public EntranceBuilder(@NotNull String stopId,
                               @NotNull String stopName,
                               @NotNull Float stopLat,
                               @NotNull Float stopLon,
                               @NotNull String parentStation) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.stopLat = stopLat;
            this.stopLon = stopLon;
            this.parentStation = parentStation;
        }

        public EntranceBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = Objects.requireNonNullElse(wheelchairBoarding, WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING);
            return this;
        }

        public Entrance build() {
            return new Entrance(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl,
                    parentStation, stopTimezone, wheelchairBoarding);
        }
    }
}
