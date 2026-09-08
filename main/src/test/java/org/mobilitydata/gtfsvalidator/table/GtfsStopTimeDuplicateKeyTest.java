/*
 * Copyright 2026 MobilityData
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

import com.google.common.collect.ImmutableList;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/**
 * Duplicate primary keys in stop_times.txt, which is one of the two tables where they are detected
 * by scanning the sorted groups of the trip_id index instead of a map keyed on the composite key.
 */
@RunWith(JUnit4.class)
public class GtfsStopTimeDuplicateKeyTest {

  private static final String FILENAME = "stop_times.txt";
  private static final String HEADER = "trip_id,stop_id,stop_sequence,arrival_time,departure_time";

  private LoadingHelper helper;
  private GtfsStopTimeTableDescriptor tableDescriptor;

  @Before
  public void setup() {
    tableDescriptor = new GtfsStopTimeTableDescriptor();
    helper = new LoadingHelper();
  }

  @Test
  public void distinctKeys_noNotice() throws ValidatorLoaderException {
    helper.load(
        tableDescriptor,
        HEADER,
        "t1,s1,1,08:00:00,08:00:00",
        "t1,s2,2,08:10:00,08:10:00",
        "t2,s1,1,09:00:00,09:00:00");

    assertThat(helper.getValidationNotices()).isEmpty();
  }

  @Test
  public void duplicateKeys_reportedInFileOrderAgainstTheFirstRow()
      throws ValidatorLoaderException {
    helper.load(
        tableDescriptor,
        HEADER,
        "t1,s1,1,08:00:00,08:00:00", // row 2
        "t2,s1,1,09:00:00,09:00:00", // row 3
        "t2,s2,1,09:10:00,09:10:00", // row 4, duplicate of row 3
        "t1,s2,1,08:10:00,08:10:00", // row 5, duplicate of row 2
        "t1,s3,1,08:20:00,08:20:00"); // row 6, duplicate of row 2 as well

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new DuplicateKeyNotice(FILENAME, 3, 4, "trip_id,stop_sequence", "t2,1"),
            new DuplicateKeyNotice(FILENAME, 2, 5, "trip_id,stop_sequence", "t1,1"),
            new DuplicateKeyNotice(FILENAME, 2, 6, "trip_id,stop_sequence", "t1,1"))
        .inOrder();
  }

  /**
   * Entities given to {@code forEntities} keep whatever order the caller used, and their row
   * numbers may repeat, so duplicates are reported against the first entity of the list and none of
   * them is dropped.
   */
  @Test
  public void forEntities_duplicateKeys_reportedAgainstTheFirstEntityOfTheList() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsStopTimeTableContainer.forEntities(
        ImmutableList.of(
            new GtfsStopTime.Builder().setTripId("t1").setStopSequence(1).setStopId("s1").build(),
            new GtfsStopTime.Builder().setTripId("t1").setStopSequence(1).setStopId("s2").build(),
            new GtfsStopTime.Builder().setTripId("t1").setStopSequence(1).setStopId("s3").build()),
        noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new DuplicateKeyNotice(FILENAME, 0, 0, "trip_id,stop_sequence", "t1,1"),
            new DuplicateKeyNotice(FILENAME, 0, 0, "trip_id,stop_sequence", "t1,1"));
  }

  /** The lookup used for translations keeps working, and returns the first row of a duplicate. */
  @Test
  public void byTranslationKey() throws ValidatorLoaderException {
    GtfsStopTimeTableContainer container =
        helper.load(
            tableDescriptor,
            HEADER,
            "t1,s1,1,08:00:00,08:00:00",
            "t1,s2,1,08:10:00,08:10:00",
            "t1,s3,2,08:20:00,08:20:00");

    assertThat(container.byTranslationKey("t1", "1").get().stopId()).isEqualTo("s1");
    assertThat(container.byTranslationKey("t1", "2").get().stopId()).isEqualTo("s3");
    assertThat(container.byTranslationKey("t1", "3").isPresent()).isFalse();
    assertThat(container.byTranslationKey("t2", "1").isPresent()).isFalse();
    assertThat(container.byTranslationKey("t1", "not a number").isPresent()).isFalse();
  }

  /** The index the duplicate detection relies on is still filled and sorted. */
  @Test
  public void byTripId_isSortedByStopSequence() throws ValidatorLoaderException {
    GtfsStopTimeTableContainer container =
        helper.load(
            tableDescriptor,
            HEADER,
            "t1,s3,3,08:20:00,08:20:00",
            "t1,s1,1,08:00:00,08:00:00",
            "t1,s2,2,08:10:00,08:10:00");

    assertThat(helper.getValidationNotices()).isEmpty();
    assertThat(
            container.byTripId("t1").stream()
                .map(GtfsStopTime::stopId)
                .collect(Collectors.toList()))
        .containsExactly("s1", "s2", "s3")
        .inOrder();
  }
}
