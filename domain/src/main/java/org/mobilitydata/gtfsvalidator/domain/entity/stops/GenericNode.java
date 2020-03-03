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
public class GenericNode extends LocationBase {

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
        super(id, code, name, description, latitude, longitude, zoneId, url, parentStation, timezone, wheelchairBoarding,
                levelId, platformCode);
    }

    public static class GenericNodeBuilder extends LocationBaseBuilder {

        public GenericNodeBuilder(@NotNull String id, @NotNull String parentStation) {
            this.id = id;
            this.parentStation = parentStation;
        }

        public GenericNodeBuilder name(@Nullable String name) {
            this.name = name;
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

        public GenericNode build() {
            return new GenericNode(id, code, name, description, latitude, longitude, zoneId, url, parentStation,
                    timezone, wheelchairBoarding, levelId, platformCode);
        }
    }
}
