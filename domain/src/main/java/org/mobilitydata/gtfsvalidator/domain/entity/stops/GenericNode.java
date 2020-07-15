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

import java.util.List;

/**
 * Model class for an entity defined in stops.txt with location_type = 3
 */
public class GenericNode extends LocationBase {

    @NotNull
    private final String parentStation;

    private GenericNode(@NotNull final String stopId,
                        @Nullable final String stopCode,
                        @Nullable final String stopName,
                        @Nullable final String stopDesc,
                        @Nullable final Float stopLat,
                        @Nullable final Float stopLon,
                        @Nullable final String zoneId,
                        @Nullable final String stopUrl,
                        @NotNull final String parentStation,
                        @Nullable final String stopTimezone,
                        @Nullable final List<String> children) {
        super(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, stopTimezone, children);
        this.parentStation = parentStation;
    }

    public @NotNull String getParentStation() {
        return parentStation;
    }

    public static class GenericNodeBuilder extends LocationBaseBuilder {

        private String parentStation;

        public GenericNodeBuilder parentStation(final String parentStation) {
            this.parentStation = parentStation;
            return this;
        }

        public EntityBuildResult<?> build() {
            if (stopId == null || parentStation == null) {
                if (stopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "stop_id", null));
                }
                if (parentStation == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stops.txt", "parent_station", stopId));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new GenericNode(stopId, stopCode, stopName, stopDesc, stopLat, stopLon,
                        zoneId, stopUrl, parentStation, stopTimezone, childrenIdList));
            }
        }

        public GenericNodeBuilder clear() {
            super.clearBase();
            parentStation = null;
            return this;
        }
    }
}
