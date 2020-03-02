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
public class StopOrPlatform {

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
    private final String parentStation;
    private final String timezone;
    private final WheelchairBoarding wheelchairBoarding;
    private final String levelId;
    private final String platformCode;

    private StopOrPlatform(@NotNull String id,
                           @Nullable String code,
                           @NotNull String name,
                           @Nullable String description,
                           @NotNull Float latitude,
                           @NotNull Float longitude,
                           @Nullable String zoneId,
                           @Nullable String url,
                           @Nullable String parentStation,
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
        this.parentStation = parentStation;
        this.timezone = timezone;
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
        this.platformCode = platformCode;
    }

    //public static StopOrPlatform builder

    public static class StopOrPlatformBuilder {
        private String id;
        private String code;
        private String name;
        private String description;
        private float latitude;
        private float longitude;
        private String zoneId;
        private String url;
        private String parentStation;
        private String timezone;
        private WheelchairBoarding wheelchairBoarding;
        private String levelId;
        private String platformCode;

        public StopOrPlatformBuilder(@NotNull String id,
                                     @NotNull String name,
                                     @NotNull Float latitude,
                                     @NotNull Float longitude) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public StopOrPlatformBuilder id(@NotNull String id) {
            this.id = id;
            return this;
        }

        public StopOrPlatformBuilder code(@Nullable String code) {
            this.code = code;
            return this;
        }

        public StopOrPlatformBuilder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public StopOrPlatformBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public StopOrPlatformBuilder zoneId(@Nullable String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public StopOrPlatformBuilder url(@Nullable String url) {
            this.url = url;
            return this;
        }

        public StopOrPlatformBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public StopOrPlatformBuilder timezone(@Nullable String timezone) {
            this.timezone = timezone;
            return this;
        }

        public StopOrPlatformBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public StopOrPlatformBuilder levelId(@Nullable String levelId) {
            this.levelId = levelId;
            return this;
        }

        public StopOrPlatformBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }

        public StopOrPlatform build() {
            return new StopOrPlatform(
                    id,
                    code,
                    name,
                    description,
                    latitude,
                    longitude,
                    zoneId,
                    url,
                    parentStation,
                    timezone,
                    wheelchairBoarding,
                    levelId,
                    platformCode
            );
        }
    }
}
