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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.INVALID_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.UNKNOWN_WHEELCHAIR_BOARDING;

/**
 * Model class for an entity defined in stops.txt with location_type = 0 (or blank). Can't be constructed directly.
 * Use {@link StopOrPlatformBuilder} to instantiate
 */
public class StopOrPlatform extends LocationBase {
    @Nullable
    private final String parentStation;
    @NotNull
    private final WheelchairBoarding wheelchairBoarding;
    @Nullable
    private final String platformCode;

    private StopOrPlatform(@NotNull final String stopId,
                           @Nullable final String stopCode,
                           @NotNull final String stopName,
                           @Nullable final String stopDesc,
                           @NotNull final Float stopLat,
                           @NotNull final Float stopLon,
                           @Nullable final String zoneId,
                           @Nullable final String stopUrl,
                           @Nullable final String parentStation,
                           @Nullable final String stopTimezone,
                           @NotNull final WheelchairBoarding wheelchairBoarding,
                           @Nullable final String platformCode,
                           @Nullable final List<String> children) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone, children);
        this.parentStation = parentStation;
        this.wheelchairBoarding = wheelchairBoarding;
        this.platformCode = platformCode;
    }

    public @Nullable String getParentStation() {
        return parentStation;
    }

    public @NotNull WheelchairBoarding getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public @Nullable String getPlatformCode() {
        return platformCode;
    }

    public static class StopOrPlatformBuilder extends LocationBaseBuilder {

        private String parentStation;
        private WheelchairBoarding wheelchairBoarding = INVALID_VALUE;
        private Integer originalWheelchairBoarding = Integer.MAX_VALUE; // required to distinguish optional field not set
        private String platformCode;

        public StopOrPlatformBuilder parentStation(@Nullable String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public StopOrPlatformBuilder wheelchairBoarding(@Nullable Integer wheelchairBoarding) {
            this.wheelchairBoarding = WheelchairBoarding.fromInt(wheelchairBoarding);
            this.originalWheelchairBoarding = wheelchairBoarding;
            return this;
        }

        public StopOrPlatformBuilder platformCode(@Nullable String platformCode) {
            this.platformCode = platformCode;
            return this;
        }

        public EntityBuildResult<?> build() {
            if (stopId == null || stopName == null || stopLat == null || stopLon == null
                    || wheelchairBoarding == INVALID_VALUE) {
                if (stopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_id", null));
                }
                if (stopName == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_name", stopId));
                }
                if (stopLat == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_lat", stopId));
                }
                if (stopLon == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_lon", stopId));
                }
                if (wheelchairBoarding == INVALID_VALUE) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stops.txt",
                            "wheelchair_boarding", stopId, originalWheelchairBoarding));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new StopOrPlatform(stopId, stopCode, stopName, stopDesc, stopLat, stopLon,
                        zoneId, stopUrl, parentStation, stopTimezone, wheelchairBoarding, platformCode, childrenIdList));
            }
        }

        public StopOrPlatformBuilder clear() {
            super.clearBase();
            parentStation = null;
            wheelchairBoarding = UNKNOWN_WHEELCHAIR_BOARDING;
            originalWheelchairBoarding = Integer.MAX_VALUE;
            platformCode = null;
            return this;
        }
    }
}
