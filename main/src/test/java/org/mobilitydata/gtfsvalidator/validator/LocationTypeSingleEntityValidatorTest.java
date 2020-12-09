/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.LocationWithoutParentStationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.PlatformWithoutParentStationNotice;
import org.mobilitydata.gtfsvalidator.notice.StationWithParentStationNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class LocationTypeSingleEntityValidatorTest {
    private List<ValidationNotice> validateStop(GtfsStop stop) {
        NoticeContainer container = new NoticeContainer();
        LocationTypeSingleEntityValidator validator = new LocationTypeSingleEntityValidator();
        validator.validate(stop, container);
        return container.getValidationNotices();
    }

    @Test
    public void stationWithParentStation() {
        GtfsStop.Builder builder = new GtfsStop.Builder()
                .setStopId("s0")
                .setCsvRowNumber(1)
                .setLocationType(GtfsLocationType.STATION.getNumber());

        assertThat(validateStop(builder.build())).isEmpty();

        builder.setParentStation("parent");
        assertThat(validateStop(builder.build()))
                .containsExactly(new StationWithParentStationNotice("s0", 1, "parent"));
    }

    @Test
    public void platformWithoutParentStation() {
        // A GTFS stop with platform_code field should have parent_station.
        GtfsStop.Builder builder = new GtfsStop.Builder()
                .setStopId("s0")
                .setCsvRowNumber(1)
                .setLocationType(GtfsLocationType.STOP.getNumber())
                .setPlatformCode("1")
                .setParentStation("parent");

        assertThat(validateStop(builder.build())).isEmpty();

        builder.setParentStation(null);
        assertThat(validateStop(builder.build()))
                .containsExactly(new PlatformWithoutParentStationNotice("s0", 1));

        // A GTFS stop without platform_code may be an isolated stop and may have no parent_station.
        builder.setPlatformCode(null);
        assertThat(validateStop(builder.build())).isEmpty();
    }

    @Test
    public void locationWithoutParentStationNotice() {
        for (GtfsLocationType locationType : new GtfsLocationType[]{GtfsLocationType.ENTRANCE,
                GtfsLocationType.GENERIC_NODE, GtfsLocationType.BOARDING_AREA}) {
            GtfsStop.Builder builder = new GtfsStop.Builder()
                    .setStopId("s0")
                    .setCsvRowNumber(1)
                    .setLocationType(locationType.getNumber())
                    .setParentStation("parent");

            assertThat(validateStop(builder.build())).isEmpty();

            builder.setParentStation(null);
            assertThat(validateStop(builder.build()))
                    .containsExactly(new LocationWithoutParentStationNotice("s0", 1, locationType.getNumber()));
        }
    }
}
