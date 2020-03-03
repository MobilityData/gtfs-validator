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
public class GenericNode {

    public enum WheelchairBoarding {
        UNKNOWN_WHEELCHAIR_BOARDING,
        WHEELCHAIR_ACCESSIBLE,
        NOT_WHEELCHAIR_ACCESSIBLE
    }

    private final String id;
    private final String code;
    private final String name;
    private final String description;
    private final float latitude;
    private final float longitude;
    private final String zoneId;
    private final String url;
    private final String parentStation; // id of a station
    private final String timezone;
    private final WheelchairBoarding wheelchairBoarding;
    private final String levelId;
    private final String platformCode;

    private GenericNode(@NotNull String id,
                        @Nullable String code,
                        @Nullable String name,
                        @Nullable String description,
                        @Nullable Float latitude,
                        @Nullable Float longitude,
                        @Nullable String zoneId,
                        @Nullable String url,
                        @NotNull String parentStation,
                        @Nullable String timezone,
                        @Nullable WheelchairBoarding wheelchairBoarding,
                        @Nullable String levelId,
                        @Nullable String platformCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.latitude = latitude != null ? latitude : 0.0f;
        this.longitude = longitude != null ? longitude : 0.0f;
        this.zoneId = zoneId;
        this.url = url;
        this.parentStation = parentStation;
        this.timezone = timezone;
        this.wheelchairBoarding = wheelchairBoarding;
        this.levelId = levelId;
        this.platformCode = platformCode;
    }

    public static class GenericNodeBuilder {
        private String id;
        private String code;
        private String name;
        private String description;
        private Float latitude;
        private Float longitude;
        private String zoneId;
        private String url;
        private String parentStation;
        private String timezone;
        private WheelchairBoarding wheelchairBoarding = WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;
        private String levelId;
        private String platformCode;

        public GenericNodeBuilder(@NotNull String id,
                                  @NotNull String name) {
            this.id = id;
            this.name = name;
        }

        public GenericNodeBuilder id(@NotNull String id) {
            this.id = id;
            return this;
        }

        public GenericNodeBuilder code(@Nullable String code) {
            this.code = code;
            return this;
        }

        public GenericNodeBuilder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public GenericNodeBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public GenericNodeBuilder latitude(@Nullable Float latitude) {
            this.latitude = latitude;
            return this;
        }

        public GenericNodeBuilder longitude(@Nullable Float longitude) {
            this.longitude = longitude;
            return this;
        }

        public GenericNodeBuilder zoneId(@Nullable String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public GenericNodeBuilder url(@Nullable String url) {
            this.url = url;
            return this;
        }

        public GenericNodeBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public GenericNodeBuilder timezone(@Nullable String timezone) {
            this.timezone = timezone;
            return this;
        }

        public GenericNodeBuilder wheelchairBoarding(@Nullable WheelchairBoarding wheelchairBoarding) {
            this.wheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public GenericNodeBuilder levelId(@Nullable String levelId) {
            this.levelId = levelId;
            return this;
        }

        public GenericNodeBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }

        public GenericNode build() {
            return new GenericNode(
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
