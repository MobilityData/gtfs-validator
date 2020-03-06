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
 * Model class for an entity defined in stops.txt with location_type = 3
 */
public class GenericNode extends LocationBase {

    public String getParentStation() {
        return parentStation;
    }

    private final String parentStation;

    private GenericNode(@NotNull String stopId,
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

    public static class GenericNodeBuilder extends LocationBaseBuilder {

        private String parentStation;

        public GenericNodeBuilder(@NotNull String stopId, @NotNull String parentStation) {
            this.stopId = stopId;
            this.parentStation = parentStation;
        }

        public GenericNodeBuilder stopName(@Nullable String stopName) {
            this.stopName = stopName;
            return this;
        }

        public GenericNodeBuilder stopLat(@Nullable Float stopLat) {
            this.stopLat = stopLat;
            return this;
        }

        public GenericNodeBuilder stopLon(@Nullable Float stopLon) {
            this.stopLon = stopLon;
            return this;
        }

        public GenericNode build() {
            return new GenericNode(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl,
                    parentStation, stopTimezone);
        }
    }
}
