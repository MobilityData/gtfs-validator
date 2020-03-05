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

//TODO: use Kotlin data class - approval required
public class StopOrPlatform extends LocationBase {

    public WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(WheelchairBoarding toSet) {
        this.wheelchairBoarding = toSet;
    }

    private WheelchairBoarding wheelchairBoarding;

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
                           @Nullable String levelId,
                           @Nullable String platformCode) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, parentStation, stopTimezone,
                levelId, platformCode);
        this.wheelchairBoarding = wheelchairBoarding;
    }

    public static class StopOrPlatformBuilder extends LocationBaseBuilder {

        private WheelchairBoarding wheelchairBoarding = WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;

        public StopOrPlatformBuilder(@NotNull String stopId,
                                     @NotNull String stopName,
                                     @NotNull Float stopLat,
                                     @NotNull Float stopLon) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.stopLat = stopLat;
            this.stopLon = stopLon;
        }

        public StopOrPlatformBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public StopOrPlatform build() {
            return new StopOrPlatform(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, parentStation,
                    stopTimezone, wheelchairBoarding, levelId, platformCode);
        }
    }
}
