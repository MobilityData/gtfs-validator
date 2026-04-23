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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.ParentStationValidator.UnusedStationNotice;
import org.mobilitydata.gtfsvalidator.validator.ParentStationValidator.WrongParentLocationTypeNotice;

@RunWith(JUnit4.class)
public class ParentStationValidatorTest {

  private List<ValidationNotice> validateChildAndParent(
      GtfsLocationType childType, GtfsLocationType parentType) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ParentStationValidator(
            GtfsStopTableContainer.forEntities(
                ImmutableList.of(
                    new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setStopName("Child location")
                        .setLocationType(childType)
                        .setParentStation("parent")
                        .build(),
                    new GtfsStop.Builder()
                        .setCsvRowNumber(2)
                        .setStopId("parent")
                        .setStopName("Parent location")
                        .setLocationType(parentType)
                        .build()),
                noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private List<ValidationNotice> validateNoParent(GtfsLocationType locationType) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ParentStationValidator(
            GtfsStopTableContainer.forEntities(
                ImmutableList.of(
                    new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setLocationType(locationType)
                        .build()),
                noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stopParent() {
    assertThat(validateChildAndParent(GtfsLocationType.STOP, GtfsLocationType.STATION)).isEmpty();
    assertThat(validateChildAndParent(GtfsLocationType.STOP, GtfsLocationType.ENTRANCE))
        .containsExactly(
            new WrongParentLocationTypeNotice(
                1,
                "child",
                "Child location",
                GtfsLocationType.STOP.getNumber(),
                2,
                "parent",
                "Parent location",
                GtfsLocationType.ENTRANCE.getNumber(),
                GtfsLocationType.STATION.getNumber()));
  }

  @Test
  public void entranceParent() {
    assertThat(validateChildAndParent(GtfsLocationType.ENTRANCE, GtfsLocationType.STATION))
        // No issue with the types, but the station has no stop (just an entrance).
        .containsExactly(new UnusedStationNotice(2, "parent", "Parent location"));
    assertThat(validateChildAndParent(GtfsLocationType.ENTRANCE, GtfsLocationType.STOP))
        .containsExactly(
            new WrongParentLocationTypeNotice(
                1,
                "child",
                "Child location",
                GtfsLocationType.ENTRANCE.getNumber(),
                2,
                "parent",
                "Parent location",
                GtfsLocationType.STOP.getNumber(),
                GtfsLocationType.STATION.getNumber()));
  }

  @Test
  public void genericNodeParent() {
    assertThat(validateChildAndParent(GtfsLocationType.GENERIC_NODE, GtfsLocationType.STATION))
        // No issue with the types, but the station has no stop.
        .containsExactly(new UnusedStationNotice(2, "parent", "Parent location"));
    assertThat(validateChildAndParent(GtfsLocationType.GENERIC_NODE, GtfsLocationType.STOP))
        .containsExactly(
            new WrongParentLocationTypeNotice(
                1,
                "child",
                "Child location",
                GtfsLocationType.GENERIC_NODE.getNumber(),
                2,
                "parent",
                "Parent location",
                GtfsLocationType.STOP.getNumber(),
                GtfsLocationType.STATION.getNumber()));
  }

  @Test
  public void boardingAreaParent() {
    assertThat(validateChildAndParent(GtfsLocationType.BOARDING_AREA, GtfsLocationType.STOP))
        .isEmpty();
    assertThat(validateChildAndParent(GtfsLocationType.BOARDING_AREA, GtfsLocationType.STATION))
        .containsExactlyElementsIn(
            ImmutableList.of(
                new WrongParentLocationTypeNotice(
                    1,
                    "child",
                    "Child location",
                    GtfsLocationType.BOARDING_AREA.getNumber(),
                    2,
                    "parent",
                    "Parent location",
                    GtfsLocationType.STATION.getNumber(),
                    GtfsLocationType.STOP.getNumber()),
                new UnusedStationNotice(2, "parent", "Parent location")));
  }

  @Test
  public void noParent() {
    // ParentStationValidator ignores non-stations without parent_station.
    assertThat(validateNoParent(GtfsLocationType.STOP)).isEmpty();
    assertThat(validateNoParent(GtfsLocationType.ENTRANCE)).isEmpty();
    assertThat(validateNoParent(GtfsLocationType.GENERIC_NODE)).isEmpty();
    assertThat(validateNoParent(GtfsLocationType.BOARDING_AREA)).isEmpty();
  }

  @Test
  public void unusedStation() {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ParentStationValidator(
            GtfsStopTableContainer.forEntities(
                ImmutableList.of(
                    new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setStopName("Child location")
                        .setLocationType(GtfsLocationType.STOP)
                        .setParentStation("used_station")
                        .build(),
                    new GtfsStop.Builder()
                        .setCsvRowNumber(2)
                        .setStopId("unused_station")
                        .setStopName("Unused station")
                        .setLocationType(GtfsLocationType.STATION)
                        .build(),
                    new GtfsStop.Builder()
                        .setCsvRowNumber(3)
                        .setStopId("used_station")
                        .setStopName("Used station")
                        .setLocationType(GtfsLocationType.STATION)
                        .build()),
                noticeContainer))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new UnusedStationNotice(2, "unused_station", "Unused station"));
  }

  @Test
  public void foreignKeyViolation_handledGracefully() {
    NoticeContainer noticeContainer = new NoticeContainer();
    new ParentStationValidator(
            GtfsStopTableContainer.forEntities(
                ImmutableList.of(
                    new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("child")
                        .setStopName("Child location")
                        .setLocationType(GtfsLocationType.STOP)
                        .setParentStation("parent")
                        .build()),
                noticeContainer))
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
