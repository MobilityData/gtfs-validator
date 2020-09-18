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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all entities defined in stops.txt. Can not be directly instantiated: user must use a Builder
 * derived from {@link LocationBase.LocationBaseBuilder} to instantiate a subclass.
 */
public abstract class LocationBase extends GtfsEntity {

    private static final String FARE_RULES_TXT = "fare_rules.txt";
    private @NotNull
    final String stopId;
    private final String stopCode;
    private final String stopName;
    private final String stopDesc;
    private final Float stopLat;
    private final Float stopLon;
    private final String zoneId;
    private final String stopUrl;
    private final String stopTimezone;
    private final List<String> childrenList;

    protected LocationBase(@NotNull final String stopId,
                           final String stopCode,
                           final String stopName,
                           final String stopDesc,
                           final Float stopLat,
                           final Float stopLon,
                           final String zoneId,
                           final String stopUrl,
                           final String stopTimezone,
                           final List<String> childrenList) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.zoneId = zoneId;
        this.stopUrl = stopUrl;
        this.stopTimezone = stopTimezone;
        this.childrenList = childrenList;
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

    public String getStopTimezone() {
        return stopTimezone;
    }

    public List<String> getChildren() {
        return childrenList;
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
        protected String stopTimezone;
        protected List<String> childrenIdList = null;
        protected final List<Notice> noticeCollection = new ArrayList<>();

        public LocationBaseBuilder stopId(@NotNull final String stopId) {
            this.stopId = stopId;
            return this;
        }

        public LocationBaseBuilder stopCode(@Nullable String code) {
            this.stopCode = code;
            return this;
        }

        public LocationBaseBuilder stopName(@NotNull final String stopName) {
            this.stopName = stopName;
            return this;
        }

        public LocationBaseBuilder stopDesc(@Nullable String description) {
            this.stopDesc = description;
            return this;
        }

        public LocationBaseBuilder stopLat(@NotNull final Float stopLat) {
            this.stopLat = stopLat;
            return this;
        }

        public LocationBaseBuilder stopLon(@NotNull final Float stopLon) {
            this.stopLon = stopLon;
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

        public LocationBaseBuilder stopTimezone(@Nullable String timezone) {
            this.stopTimezone = timezone;
            return this;
        }

        public LocationBaseBuilder childrenList(@Nullable List<String> children) {
            this.childrenIdList = children;
            return this;
        }

        public LocationBaseBuilder clear() {
            stopId = null;
            stopCode = null;
            stopName = null;
            stopDesc = null;
            stopLat = null;
            stopLon = null;
            zoneId = null;
            stopUrl = null;
            stopTimezone = null;
            childrenIdList = null;
            noticeCollection.clear();
            return this;
        }

        protected boolean isFareRuleProvided(final List<String> filenameListToProcess) {
            return filenameListToProcess.contains(FARE_RULES_TXT);
        }
    }
}
