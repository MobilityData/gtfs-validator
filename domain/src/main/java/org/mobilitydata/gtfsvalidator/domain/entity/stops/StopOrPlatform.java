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
 * Model class for an entity defined in stops.txt with location_type = 0 (or blank)
 */
public class StopOrPlatform extends LocationBase {

    public String getParentStation() {
        return parentStation;
    }

    public WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(WheelchairBoarding toSet) {
        this.wheelchairBoarding = Objects.requireNonNullElse(toSet, WheelchairBoarding.INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING);
    }

    public String getPlatformCode() {
        return platformCode;
    }

    private String parentStation;
    private WheelchairBoarding wheelchairBoarding;
    private final String platformCode;

    private StopOrPlatform(@NotNull String stopId,
                           @Nullable String stopCode,
                           @NotNull String stopName,
                           @Nullable String stopDesc,
                           @NotNull Float stopLat,
                           @NotNull Float stopLon,
                           @Nullable String zoneId,
                           @Nullable String stopUrl,
                           @Nullable String parentStation,
                           @Nullable String stopTimezone,
                           @Nullable WheelchairBoarding wheelchairBoarding,
                           @Nullable String platformCode) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone);
        this.parentStation = parentStation;
        this.wheelchairBoarding = wheelchairBoarding;
        this.platformCode = platformCode;
    }

    public static class StopOrPlatformBuilder extends LocationBaseBuilder {

        private String parentStation;
        private WheelchairBoarding wheelchairBoarding = WheelchairBoarding.INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING;
        private String platformCode;

        public StopOrPlatformBuilder(@NotNull String stopId,
                                     @NotNull String stopName,
                                     @NotNull Float stopLat,
                                     @NotNull Float stopLon) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.stopLat = stopLat;
            this.stopLon = stopLon;
        }

        public StopOrPlatformBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public StopOrPlatformBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = Objects.requireNonNullElse(wheelchairBoarding, WheelchairBoarding.INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING);
            return this;
        }

        public StopOrPlatformBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }

        public StopOrPlatform build() {
            return new StopOrPlatform(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl,
                    parentStation, stopTimezone, wheelchairBoarding, platformCode);
        }
    }
}
