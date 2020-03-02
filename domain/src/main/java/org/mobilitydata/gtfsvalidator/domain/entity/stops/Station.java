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

//TODO: use Kotlin data class
public class Station {

    public enum WheelchairBoarding {
        NO_INFO,
        SOME_VEHICLES,
        NOT_POSSIBLE,
    }

    private final String id;
    private final String code;
    private final String name;
    private final String description;
    private final float latitude;
    private final float longitude;
    private final String zoneId;
    private final String url;
    private final String timezone;
    private final WheelchairBoarding wheelchairBoarding;
    private final String levelId;
    private final String platformCode;

    private Station(@NotNull String id,
                    @Nullable String code,
                    @NotNull String name,
                    @Nullable String description,
                    @NotNull Float latitude,
                    @NotNull Float longitude,
                    @Nullable String zoneId,
                    @Nullable String url,
                    @Nullable String timezone,
                    @Nullable WheelchairBoarding wheelchairBoarding,
                    @Nullable String levelId,
                    @Nullable String platformCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoneId = zoneId;
        this.url = url;
        this.timezone = timezone;
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
        this.platformCode = platformCode;
    }

    public static class StationBuilder {
        private String id;
        private String code;
        private String name;
        private String description;
        private float latitude;
        private float longitude;
        private String zoneId;
        private String url;
        private String timezone;
        private WheelchairBoarding wheelchairBoarding;
        private String levelId;
        private String platformCode;

        public StationBuilder(@NotNull String id,
                              @NotNull String name,
                              @NotNull Float latitude,
                              @NotNull Float longitude) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public StationBuilder id(@NotNull String id) {
            this.id = id;
            return this;
        }

        public StationBuilder code(@Nullable String code) {
            this.code = code;
            return this;
        }

        public StationBuilder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public StationBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public StationBuilder zoneId(@Nullable String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public StationBuilder url(@Nullable String url) {
            this.url = url;
            return this;
        }

        public StationBuilder timezone(@Nullable String timezone) {
            this.timezone = timezone;
            return this;
        }

        public StationBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public StationBuilder levelId(@Nullable String levelId) {
            this.levelId = levelId;
            return this;
        }

        public StationBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }

        public Station build() {
            return new Station(
                    id,
                    code,
                    name,
                    description,
                    latitude,
                    longitude,
                    zoneId,
                    url,
                    timezone,
                    wheelchairBoarding,
                    levelId,
                    platformCode
            );
        }
    }
}
