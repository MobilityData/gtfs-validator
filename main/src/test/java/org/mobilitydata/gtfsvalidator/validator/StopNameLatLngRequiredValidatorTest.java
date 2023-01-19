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
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.BOARDING_AREA;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.GENERIC_NODE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

@RunWith(JUnit4.class)
public class StopNameLatLngRequiredValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopNameLatLngRequiredValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void genericNodeWithoutNameLatLon_yieldsNoNotice() {
    assertThat(generateNotices(new GtfsStop.Builder()
                                   .setCsvRowNumber(2)
                                   .setLocationType(GENERIC_NODE.getNumber())
                                   .build()))
        .isEmpty();
  }

  @Test
  public void boardingAreaWithoutNameLatLon_yieldsNoNotice() {
    assertThat(generateNotices(new GtfsStop.Builder()
                                   .setCsvRowNumber(2)
                                   .setLocationType(BOARDING_AREA.getNumber())
                                   .build()))
        .isEmpty();
  }

  @Test
  public void entranceWithoutName_yieldsNoNotice() {
    assertThat(generateNotices(new GtfsStop.Builder()
                                   .setCsvRowNumber(2)
                                   .setLocationType(ENTRANCE.getNumber())
                                   .setStopLat(1.0)
                                   .setStopLon(2.0)
                                   .build()))
        .isEmpty();
  }

  @Test
  public void entranceWithoutLatLon_yieldsNotices() {
    assertThat(generateNotices(new GtfsStop.Builder()
                                   .setCsvRowNumber(2)
                                   .setLocationType(ENTRANCE.getNumber())
                                   .build()))
        .containsExactly(new MissingRequiredFieldNotice("stops.txt", 2, "stop_lat"),
            new MissingRequiredFieldNotice("stops.txt", 2, "stop_lon"));
  }

  @Test
  public void stopWithoutNameLatLon_yieldsNotices() {
    assertThat(
        generateNotices(
            new GtfsStop.Builder().setCsvRowNumber(2).setLocationType(STOP.getNumber()).build()))
        .containsExactly(new MissingRequiredFieldNotice("stops.txt", 2, "stop_name"),
            new MissingRequiredFieldNotice("stops.txt", 2, "stop_lat"),
            new MissingRequiredFieldNotice("stops.txt", 2, "stop_lon"));
  }

  @Test
  public void stationWithoutNameLatLon_yieldsNotices() {
    assertThat(
        generateNotices(
            new GtfsStop.Builder().setCsvRowNumber(2).setLocationType(STOP.getNumber()).build()))
        .containsExactly(new MissingRequiredFieldNotice("stops.txt", 2, "stop_name"),
            new MissingRequiredFieldNotice("stops.txt", 2, "stop_lat"),
            new MissingRequiredFieldNotice("stops.txt", 2, "stop_lon"));
  }
}
