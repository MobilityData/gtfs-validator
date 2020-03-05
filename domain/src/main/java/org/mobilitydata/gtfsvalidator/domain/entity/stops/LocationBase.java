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

import java.util.stream.Stream;

public abstract class LocationBase {

    // see https://gtfs.org/reference/static#stopstxt
    public enum LocationType {
        STOP_OR_PLATFORM(0),
        STATION(1),
        ENTRANCE_OR_EXIT(2),
        GENERIC_NODE(3),
        BOARDING_AREA(4);

        private int value;

        LocationType(int value) {
            this.value = value;
        }

        static public LocationType fromInt(Integer fromValue) {
            if (fromValue == null) {
                return STOP_OR_PLATFORM;
            }
            return Stream.of(LocationType.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .orElse(STOP_OR_PLATFORM);
        }
    }

    @NotNull
    public String getStopId() {
        return stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopDesc() {
        return stopDesc;
    }

    public Float getStopLat() {
        return stopLat;
    }

    public Float getStopLon() {
        return stopLon;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getStopUrl() {
        return stopUrl;
    }

    public String getParentStation() {
        return parentStation;
    }

    public String getStopTimezone() {
        return stopTimezone;
    }

    public String getLevelId() {
        return levelId;
    }

    private @NotNull
    final String stopId;
    private final String stopCode;
    private final String stopName;
    private final String stopDesc;
    private final Float stopLat;
    private final Float stopLon;
    private final String zoneId;
    private final String stopUrl;
    private final String parentStation;
    private final String stopTimezone;
    private final String levelId;

    protected LocationBase(@NotNull String stopId,
                           String stopCode,
                           String stopName,
                           String stopDesc,
                           Float stopLat,
                           Float stopLon,
                           String zoneId,
                           String stopUrl,
                           String parentStation,
                           String stopTimezone,
                           String levelId) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.zoneId = zoneId;
        this.stopUrl = stopUrl;
        this.parentStation = parentStation;
        this.stopTimezone = stopTimezone;
        this.levelId = levelId;
    }

    public static abstract class LocationBaseBuilder {
        protected String stopId;
        protected String stopCode;
        protected String stopName;
        protected String stopDesc;
        protected Float stopLat;
        protected Float stopLon;
        protected String zoneId;
        protected String stopUrl;
        protected String parentStation;
        protected String stopTimezone;
        protected String levelId;

        public LocationBaseBuilder stopCode(@Nullable String code) {
            this.stopCode = code;
            return this;
        }

        public LocationBaseBuilder stopDesc(@Nullable String description) {
            this.stopDesc = description;
            return this;
        }

        public LocationBaseBuilder zoneId(@Nullable String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public LocationBaseBuilder stopUrl(@Nullable String url) {
            this.stopUrl = url;
            return this;
        }

        public LocationBaseBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public LocationBaseBuilder stopTimezone(@Nullable String timezone) {
            this.stopTimezone = timezone;
            return this;
        }

        public LocationBaseBuilder levelId(@Nullable String levelId) {
            this.levelId = levelId;
            return this;
        }
    }
}
