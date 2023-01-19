/*
 * Copyright 2023 Google LLC
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

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.StationUsageValidator.StationWithoutPlatformsNotice;

@RunWith(JUnit4.class)
public final class StationUsageValidatorTest {
  private static String stopIdForRow(long csvRowNumber) {
    return "location" + csvRowNumber;
  }

  private static String stopNameForRow(long csvRowNumber) {
    return "Location " + csvRowNumber;
  }

  private static GtfsStop createLocation(
      int csvRowNumber, GtfsLocationType locationType, Optional<String> parentStation) {
    GtfsStop.Builder builder = new GtfsStop.Builder()
                                   .setCsvRowNumber(csvRowNumber)
                                   .setStopId(stopIdForRow(csvRowNumber))
                                   .setStopName(stopNameForRow(csvRowNumber))
                                   .setStopLat(10.0)
                                   .setStopLon(10.0)
                                   .setLocationType(locationType.getNumber());
    parentStation.ifPresent(builder::setParentStation);
    return builder.build();
  }

  private static List<ValidationNotice> generateNotices(List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StationUsageValidator(GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stationWithoutPlatforms_yieldsNotice() {
    GtfsStop station = createLocation(2, STATION, Optional.empty());
    assertThat(generateNotices(ImmutableList.of(station)))
        .containsExactly(new StationWithoutPlatformsNotice(station));
  }

  @Test
  public void stationWithEntranceButWithoutPlatforms_yieldsNotice() {
    GtfsStop station = createLocation(2, STATION, Optional.empty());
    GtfsStop entrance = createLocation(3, ENTRANCE, Optional.of(station.stopId()));

    assertThat(generateNotices(ImmutableList.of(station, entrance)))
        .containsExactly(new StationWithoutPlatformsNotice(station));
  }

  @Test
  public void unusedStop_yieldsNoNotice() {
    assertThat(generateNotices(ImmutableList.of(createLocation(2, STOP, Optional.empty()))))
        .isEmpty();
  }

  @Test
  public void stationWithPlatform_yieldsNoNotice() {
    GtfsStop station = createLocation(2, STATION, Optional.empty());
    GtfsStop stop = createLocation(3, STOP, Optional.of(station.stopId()));
    assertThat(generateNotices(ImmutableList.of(station, stop))).isEmpty();
  }
}
