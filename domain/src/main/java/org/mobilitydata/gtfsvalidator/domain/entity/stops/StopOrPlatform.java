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
                           @Nullable LocationBase.WheelchairBoarding wheelchairBoarding,
                           @Nullable String levelId,
                           @Nullable String platformCode) {
        super(id, code, name, description, latitude, longitude, zoneId, url, parentStation, timezone, wheelchairBoarding,
                levelId, platformCode);
    }

    public static class StopOrPlatformBuilder extends LocationBaseBuilder {

        public StopOrPlatformBuilder(@NotNull String id,
                                     @NotNull String name,
                                     @NotNull Float latitude,
                                     @NotNull Float longitude) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public StopOrPlatform build() {
            return new StopOrPlatform(id, code, name, description, latitude, longitude, zoneId, url, parentStation,
                    timezone, wheelchairBoarding, levelId, platformCode);
        }
    }
}
