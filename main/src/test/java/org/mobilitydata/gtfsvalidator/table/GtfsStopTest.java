/*
 * Copyright 2020 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_LEVEL_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_LOCATION_TYPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_PARENT_STATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_PLATFORM_CODE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_CODE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_DESC;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_LAT;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_LON;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_TIMEZONE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_STOP_URL;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_TTS_STOP_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_WHEELCHAIR_BOARDING;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.DEFAULT_ZONE_ID;

import java.time.ZoneId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsStopTest {
  @Test
  public void shouldReturnFieldValues() {
    Builder builder = new Builder();
    GtfsStop underTest =
        builder
            .setStopId("stop id")
            .setStopCode("stop code")
            .setStopName("stop name")
            .setTtsStopName("tts name")
            .setStopDesc("stop desc")
            .setStopLat(20d)
            .setStopLon(20d)
            .setZoneId("zone id")
            .setStopUrl("https://www.github.com/MobilityData.org")
            .setLocationType(2)
            .setParentStation("parent id")
            .setStopTimezone(ZoneId.systemDefault())
            .setWheelchairBoarding(1)
            .setLevelId("level id")
            .setPlatformCode("platform code")
            .build();

    assertThat(underTest.stopId()).isEqualTo("stop id");
    assertThat(underTest.stopCode()).isEqualTo("stop code");
    assertThat(underTest.stopName()).isEqualTo("stop name");
    assertThat(underTest.ttsStopName()).isEqualTo("tts name");
    assertThat(underTest.stopDesc()).isEqualTo("stop desc");
    assertThat(underTest.stopLat()).isEqualTo(20d);
    assertThat(underTest.stopLon()).isEqualTo(20d);
    assertThat(underTest.zoneId()).isEqualTo("zone id");
    assertThat(underTest.stopUrl()).isEqualTo("https://www.github.com/MobilityData.org");
    assertThat(underTest.locationType()).isEqualTo(GtfsLocationType.forNumber(2));
    assertThat(underTest.parentStation()).isEqualTo("parent id");
    assertThat(underTest.stopTimezone().getId()).isEqualTo(ZoneId.systemDefault().getId());
    assertThat(underTest.wheelchairBoarding()).isEqualTo(GtfsWheelchairBoarding.forNumber(1));
    assertThat(underTest.levelId()).isEqualTo("level id");
    assertThat(underTest.platformCode()).isEqualTo("platform code");

    assertThat(underTest.hasStopId()).isTrue();
    assertThat(underTest.hasStopCode()).isTrue();
    assertThat(underTest.hasStopName()).isTrue();
    assertThat(underTest.hasTtsStopName()).isTrue();
    assertThat(underTest.hasStopDesc()).isTrue();
    assertThat(underTest.hasStopLat()).isTrue();
    assertThat(underTest.hasStopLon()).isTrue();
    assertThat(underTest.hasZoneId()).isTrue();
    assertThat(underTest.hasStopUrl()).isTrue();
    assertThat(underTest.hasLocationType()).isTrue();
    assertThat(underTest.hasParentStation()).isTrue();
    assertThat(underTest.hasStopTimezone()).isTrue();
    assertThat(underTest.hasWheelchairBoarding()).isTrue();
    assertThat(underTest.hasLevelId()).isTrue();
    assertThat(underTest.hasPlatformCode()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    Builder builder = new Builder();
    GtfsStop underTest =
        builder
            .setStopId(null)
            .setStopCode(null)
            .setStopName(null)
            .setTtsStopName(null)
            .setStopDesc(null)
            .setStopLat(null)
            .setStopLon(null)
            .setZoneId(null)
            .setStopUrl(null)
            .setLocationType(null)
            .setParentStation(null)
            .setStopTimezone(null)
            .setWheelchairBoarding(null)
            .setLevelId(null)
            .setPlatformCode(null)
            .build();

    assertThat(underTest.stopId()).isEqualTo(DEFAULT_STOP_ID);
    assertThat(underTest.stopCode()).isEqualTo(DEFAULT_STOP_CODE);
    assertThat(underTest.stopName()).isEqualTo(DEFAULT_STOP_NAME);
    assertThat(underTest.ttsStopName()).isEqualTo(DEFAULT_TTS_STOP_NAME);
    assertThat(underTest.stopDesc()).isEqualTo(DEFAULT_STOP_DESC);
    assertThat(underTest.stopLat()).isEqualTo(DEFAULT_STOP_LAT);
    assertThat(underTest.stopLon()).isEqualTo(DEFAULT_STOP_LON);
    assertThat(underTest.zoneId()).isEqualTo(DEFAULT_ZONE_ID);
    assertThat(underTest.stopUrl()).isEqualTo(DEFAULT_STOP_URL);
    assertThat(underTest.locationType())
        .isEqualTo(GtfsLocationType.forNumber(DEFAULT_LOCATION_TYPE));
    assertThat(underTest.parentStation()).isEqualTo(DEFAULT_PARENT_STATION);
    assertThat(underTest.stopTimezone()).isEqualTo(DEFAULT_STOP_TIMEZONE);
    assertThat(underTest.wheelchairBoarding())
        .isEqualTo(GtfsWheelchairBoarding.forNumber(DEFAULT_WHEELCHAIR_BOARDING));
    assertThat(underTest.levelId()).isEqualTo(DEFAULT_LEVEL_ID);
    assertThat(underTest.platformCode()).isEqualTo(DEFAULT_PLATFORM_CODE);

    assertThat(underTest.hasStopId()).isFalse();
    assertThat(underTest.hasStopCode()).isFalse();
    assertThat(underTest.hasStopName()).isFalse();
    assertThat(underTest.hasTtsStopName()).isFalse();
    assertThat(underTest.hasStopDesc()).isFalse();
    assertThat(underTest.hasStopLat()).isFalse();
    assertThat(underTest.hasStopLon()).isFalse();
    assertThat(underTest.hasZoneId()).isFalse();
    assertThat(underTest.hasStopUrl()).isFalse();
    assertThat(underTest.hasLocationType()).isFalse();
    assertThat(underTest.hasParentStation()).isFalse();
    assertThat(underTest.hasStopTimezone()).isFalse();
    assertThat(underTest.hasWheelchairBoarding()).isFalse();
    assertThat(underTest.hasLevelId()).isFalse();
    assertThat(underTest.hasPlatformCode()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    Builder builder = new Builder();
    builder
        .setStopId("stop id")
        .setStopCode("stop code")
        .setStopName("stop name")
        .setTtsStopName("tts name")
        .setStopDesc("stop desc")
        .setStopLat(20d)
        .setStopLon(20d)
        .setZoneId("zone id")
        .setStopUrl("https://www.github.com/MobilityData.org")
        .setLocationType(2)
        .setParentStation("parent id")
        .setStopTimezone(ZoneId.systemDefault())
        .setWheelchairBoarding(1)
        .setLevelId("level id")
        .setPlatformCode("platform code");
    builder.clear();
    GtfsStop underTest = builder.build();

    assertThat(underTest.stopId()).isEqualTo(DEFAULT_STOP_ID);
    assertThat(underTest.stopCode()).isEqualTo(DEFAULT_STOP_CODE);
    assertThat(underTest.stopName()).isEqualTo(DEFAULT_STOP_NAME);
    assertThat(underTest.ttsStopName()).isEqualTo(DEFAULT_TTS_STOP_NAME);
    assertThat(underTest.stopDesc()).isEqualTo(DEFAULT_STOP_DESC);
    assertThat(underTest.stopLat()).isEqualTo(DEFAULT_STOP_LAT);
    assertThat(underTest.stopLon()).isEqualTo(DEFAULT_STOP_LON);
    assertThat(underTest.zoneId()).isEqualTo(DEFAULT_ZONE_ID);
    assertThat(underTest.stopUrl()).isEqualTo(DEFAULT_STOP_URL);
    assertThat(underTest.locationType())
        .isEqualTo(GtfsLocationType.forNumber(DEFAULT_LOCATION_TYPE));
    assertThat(underTest.parentStation()).isEqualTo(DEFAULT_PARENT_STATION);
    assertThat(underTest.stopTimezone()).isEqualTo(DEFAULT_STOP_TIMEZONE);
    assertThat(underTest.wheelchairBoarding())
        .isEqualTo(GtfsWheelchairBoarding.forNumber(DEFAULT_WHEELCHAIR_BOARDING));
    assertThat(underTest.levelId()).isEqualTo(DEFAULT_LEVEL_ID);
    assertThat(underTest.platformCode()).isEqualTo(DEFAULT_PLATFORM_CODE);

    assertThat(underTest.hasStopId()).isFalse();
    assertThat(underTest.hasStopCode()).isFalse();
    assertThat(underTest.hasStopName()).isFalse();
    assertThat(underTest.hasTtsStopName()).isFalse();
    assertThat(underTest.hasStopDesc()).isFalse();
    assertThat(underTest.hasStopLat()).isFalse();
    assertThat(underTest.hasStopLon()).isFalse();
    assertThat(underTest.hasZoneId()).isFalse();
    assertThat(underTest.hasStopUrl()).isFalse();
    assertThat(underTest.hasLocationType()).isFalse();
    assertThat(underTest.hasParentStation()).isFalse();
    assertThat(underTest.hasStopTimezone()).isFalse();
    assertThat(underTest.hasWheelchairBoarding()).isFalse();
    assertThat(underTest.hasLevelId()).isFalse();
    assertThat(underTest.hasPlatformCode()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    Builder builder = new Builder();
    GtfsStop underTest = builder.build();

    assertThat(underTest.stopId()).isNull();
    assertThat(underTest.stopCode()).isNull();
    assertThat(underTest.stopName()).isNull();
    assertThat(underTest.ttsStopName()).isNull();
    assertThat(underTest.stopDesc()).isNull();
    assertThat(underTest.stopLat()).isEqualTo(DEFAULT_STOP_LAT);
    assertThat(underTest.stopLon()).isEqualTo(DEFAULT_STOP_LON);
    assertThat(underTest.zoneId()).isNull();
    assertThat(underTest.stopUrl()).isNull();
    assertThat(underTest.locationType())
        .isEqualTo(GtfsLocationType.forNumber(DEFAULT_LOCATION_TYPE));
    assertThat(underTest.parentStation()).isNull();
    assertThat(underTest.stopTimezone()).isNull();
    assertThat(underTest.wheelchairBoarding()).isEqualTo(GtfsWheelchairBoarding.UNKNOWN);
    assertThat(underTest.levelId()).isNull();
    assertThat(underTest.platformCode()).isNull();

    assertThat(underTest.hasStopId()).isFalse();
    assertThat(underTest.hasStopCode()).isFalse();
    assertThat(underTest.hasStopName()).isFalse();
    assertThat(underTest.hasTtsStopName()).isFalse();
    assertThat(underTest.hasStopDesc()).isFalse();
    assertThat(underTest.hasStopLat()).isFalse();
    assertThat(underTest.hasStopLon()).isFalse();
    assertThat(underTest.hasZoneId()).isFalse();
    assertThat(underTest.hasStopUrl()).isFalse();
    assertThat(underTest.hasLocationType()).isFalse();
    assertThat(underTest.hasParentStation()).isFalse();
    assertThat(underTest.hasStopTimezone()).isFalse();
    assertThat(underTest.hasWheelchairBoarding()).isFalse();
    assertThat(underTest.hasLevelId()).isFalse();
    assertThat(underTest.hasPlatformCode()).isFalse();
  }
}
