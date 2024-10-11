/*
 * Copyright 2021 Google LLC
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.validator.LocationHasStopTimesValidator.LocationWithUnexpectedStopTimeNotice;
import org.mobilitydata.gtfsvalidator.validator.LocationHasStopTimesValidator.StopWithoutStopTimeNotice;

@RunWith(JUnit4.class)
public final class LocationHasStopTimesValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops,
      List<GtfsStopTime> stopTimes,
      List<GtfsLocationGroupStops> locationGroupStops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new LocationHasStopTimesValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsLocationGroupStopsTableContainer.forEntities(locationGroupStops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsStop createLocation(GtfsLocationType locationType, String stopId) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(2)
        .setStopId(stopId)
        .setStopName("Location 1")
        .setLocationType(locationType)
        .build();
  }

  private static GtfsStop createLocation(GtfsLocationType locationType) {
    return createLocation(locationType, "location1");
  }

  private static GtfsStopTime createStopTimeFor(GtfsStop stop) {
    return new GtfsStopTime.Builder().setStopId(stop.stopId()).build();
  }

  private static GtfsLocationGroupStops createLocationGroupStops() {
    return new GtfsLocationGroupStops.Builder()
        .setCsvRowNumber(2)
        .setLocationGroupId("locationGroupId")
        .setStopId("stopId")
        .build();
  }

  @Test
  public void stopWithStopTime_yieldsNoNotice() {
    GtfsStop stop = createLocation(STOP);
    assertThat(
            generateNotices(
                ImmutableList.of(stop),
                ImmutableList.of(createStopTimeFor(stop)),
                ImmutableList.of(createLocationGroupStops())))
        .isEmpty();
  }

  @Test
  public void stopWithoutStopTime_yieldsNotice() {
    GtfsStop location = createLocation(STOP, "stopId");
    assertThat(generateNotices(ImmutableList.of(location), ImmutableList.of(), ImmutableList.of()))
        .containsExactly(new StopWithoutStopTimeNotice(location));
  }

  @Test
  public void stationWithStopTime_yieldsNotice() {
    GtfsStop location = createLocation(STATION);
    GtfsStopTime stopTime = createStopTimeFor(location);
    assertThat(
            generateNotices(
                ImmutableList.of(location),
                ImmutableList.of(stopTime),
                ImmutableList.of(createLocationGroupStops())))
        .containsExactly(new LocationWithUnexpectedStopTimeNotice(location, stopTime));
  }

  @Test
  public void stationWithoutStopTime_yieldsNoNotice() {
    GtfsStop location = createLocation(STATION);
    assertThat(
            generateNotices(
                ImmutableList.of(location),
                ImmutableList.of(),
                ImmutableList.of(createLocationGroupStops())))
        .isEmpty();
  }

  @Test
  public void entranceWithStopTime_yieldsNotice() {
    GtfsStop location = createLocation(ENTRANCE);
    GtfsStopTime stopTime = createStopTimeFor(location);
    assertThat(
            generateNotices(
                ImmutableList.of(location),
                ImmutableList.of(stopTime),
                ImmutableList.of(createLocationGroupStops())))
        .containsExactly(new LocationWithUnexpectedStopTimeNotice(location, stopTime));
  }

  @Test
  public void entranceWithoutStopTime_yieldsNoNotice() {
    GtfsStop location = createLocation(ENTRANCE);
    assertThat(
            generateNotices(
                ImmutableList.of(location),
                ImmutableList.of(),
                ImmutableList.of(createLocationGroupStops())))
        .isEmpty();
  }
}
