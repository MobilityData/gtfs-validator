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

public abstract class LocationBase {

    // see https://gtfs.org/reference/static#stopstxt
    public static final int LOCATION_TYPE_STOP_OR_PLATFORM = 0;
    public static final int LOCATION_TYPE_STATION = 1;
    public static final int LOCATION_TYPE_ENTRANCE_OR_EXIT = 2;
    public static final int LOCATION_TYPE_GENERIC_NODE = 3;
    public static final int LOCATION_TYPE_BOARDING_AREA = 4;


    public enum WheelchairBoarding {
        UNKNOWN_WHEELCHAIR_BOARDING,
        WHEELCHAIR_ACCESSIBLE,
        NOT_WHEELCHAIR_ACCESSIBLE
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

    public WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(WheelchairBoarding toSet) {
        this.wheelchairBoarding = toSet;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getPlatformCode() {
        return platformCode;
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
    private WheelchairBoarding wheelchairBoarding;
    private final String levelId;
    private final String platformCode;


    protected LocationBase(@NotNull String id,
                           String stopCode,
                           String name,
                           String desc,
                           Float latitude,
                           Float longitude,
                           String zoneId,
                           String url,
                           String parentStation,
                           String timezone,
                           WheelchairBoarding wheelchairBoarding,
                           String levelId,
                           String platformCode) {
        this.stopId = id;
        this.stopCode = stopCode;
        this.stopName = name;
        this.stopDesc = desc;
        this.stopLat = latitude;
        this.stopLon = longitude;
        this.zoneId = zoneId;
        this.stopUrl = url;
        this.parentStation = parentStation;
        this.stopTimezone = timezone;
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
        this.platformCode = platformCode;
    }

    public static abstract class LocationBaseBuilder {
        protected String id;
        protected String code;
        protected String name;
        protected String description;
        protected Float latitude;
        protected Float longitude;
        protected String zoneId;
        protected String url;
        protected String parentStation;
        protected String timezone;
        protected LocationBase.WheelchairBoarding wheelchairBoarding = LocationBase.WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;
        protected String levelId;
        protected String platformCode;

        public LocationBaseBuilder code(@Nullable String code) {
            this.code = code;
            return this;
        }

        public LocationBaseBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public LocationBaseBuilder zoneId(@Nullable String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public LocationBaseBuilder url(@Nullable String url) {
            this.url = url;
            return this;
        }

        public LocationBaseBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public LocationBaseBuilder timezone(@Nullable String timezone) {
            this.timezone = timezone;
            return this;
        }

        public LocationBaseBuilder wheelchairBoarding(@Nullable LocationBase.WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public LocationBaseBuilder levelId(@Nullable String levelId) {
            this.levelId = levelId;
            return this;
        }

        public LocationBaseBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }
    }
}
