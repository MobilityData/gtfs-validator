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

/**
 * Model class for an entity defined in stops.txt with location_type = 4
 */
public class BoardingArea extends LocationBase {

    public String getParentStation() {
        return parentStation;
    }

    private final String parentStation;

    private BoardingArea(@NotNull String stopId,
                         @Nullable String stopCode,
                         @Nullable String stopName,
                         @Nullable String stopDesc,
                         @Nullable Float stopLat,
                         @Nullable Float stopLon,
                         @Nullable String zoneId,
                         @Nullable String stopUrl,
                         @NotNull String parentStation,
                         @Nullable String stopTimezone) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone);
        this.parentStation = parentStation;
    }

    public static class BoardingAreaBuilder extends LocationBaseBuilder {

        private String parentStation;

        public BoardingAreaBuilder(@NotNull String id, @NotNull String parentStation) {
            this.stopId = id;
            this.parentStation = parentStation;
        }

        public BoardingAreaBuilder stopName(@Nullable String stopName) {
            this.stopName = stopName;
            return this;
        }

        public BoardingAreaBuilder stopLat(@Nullable Float stopLat) {
            this.stopLat = stopLat;
            return this;
        }

        public BoardingAreaBuilder stopLon(@Nullable Float stopLon) {
            this.stopLon = stopLon;
            return this;
        }

        public BoardingArea build() {
            return new BoardingArea(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl,
                    parentStation, stopTimezone);
        }
    }
}
