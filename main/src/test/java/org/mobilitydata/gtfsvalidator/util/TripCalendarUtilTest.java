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

package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mobilitydata.gtfsvalidator.util.TripCalendarUtil.computeMajorityServiceCoverage;
import static org.mobilitydata.gtfsvalidator.util.TripCalendarUtil.computeServiceCoverage;
import static org.mobilitydata.gtfsvalidator.util.TripCalendarUtil.countTripsForEachServiceDate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import java.time.LocalDate;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.TripCalendarUtil.DateInterval;

@RunWith(JUnit4.class)
public final class TripCalendarUtilTest {

  @Test
  public void computeServiceCoverage_empty() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(
            computeServiceCoverage(
                ImmutableMap.of(),
                GtfsTripTableContainer.forEntities(ImmutableList.of(), noticeContainer)))
        .isEmpty();
  }

  @Test
  public void computeServiceCoverage_notEmpty() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(
            computeServiceCoverage(
                ImmutableMap.of(
                    "WEEK",
                    ImmutableSortedSet.of(
                        LocalDate.of(2021, 2, 1),
                        LocalDate.of(2021, 2, 14),
                        LocalDate.of(2021, 2, 28))),
                GtfsTripTableContainer.forEntities(
                    ImmutableList.of(
                        new GtfsTrip.Builder()
                            .setCsvRowNumber(2)
                            .setTripId("trip1")
                            .setServiceId("WEEK")
                            .build()),
                    noticeContainer)))
        .hasValue(DateInterval.create(LocalDate.of(2021, 2, 1), LocalDate.of(2021, 2, 28)));
  }

  @Test
  public void computeMajorityServiceCoverage_basic() {
    assertThat(
            computeMajorityServiceCoverage(
                new ImmutableSortedMap.Builder<LocalDate, Integer>(Ordering.natural())
                    .put(LocalDate.of(2021, 1, 1), 1)
                    .put(LocalDate.of(2021, 1, 2), 1)
                    .put(LocalDate.of(2021, 1, 3), 1)
                    .put(LocalDate.of(2021, 1, 4), 8)
                    .put(LocalDate.of(2021, 1, 5), 4)
                    .put(LocalDate.of(2021, 1, 6), 8)
                    .put(LocalDate.of(2021, 1, 7), 8)
                    .put(LocalDate.of(2021, 1, 8), 4)
                    .put(LocalDate.of(2021, 1, 9), 8)
                    .put(LocalDate.of(2021, 1, 10), 1)
                    .put(LocalDate.of(2021, 1, 11), 8)
                    .put(LocalDate.of(2021, 1, 12), 1)
                    .buildOrThrow()))
        .hasValue(DateInterval.create(LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 11)));
  }

  @Test
  public void computeMajorityServiceCoverage_outlier() {
    assertThat(
            computeMajorityServiceCoverage(
                new ImmutableSortedMap.Builder<LocalDate, Integer>(Ordering.natural())
                    .put(LocalDate.of(2021, 1, 1), 1)
                    .put(LocalDate.of(2021, 1, 2), 1)
                    .put(LocalDate.of(2021, 1, 3), 50)
                    .put(LocalDate.of(2021, 1, 4), 8)
                    .put(LocalDate.of(2021, 1, 5), 4)
                    .put(LocalDate.of(2021, 1, 6), 8)
                    .put(LocalDate.of(2021, 1, 7), 8)
                    .put(LocalDate.of(2021, 1, 8), 4)
                    .put(LocalDate.of(2021, 1, 9), 8)
                    .put(LocalDate.of(2021, 1, 10), 1)
                    .put(LocalDate.of(2021, 1, 11), 8)
                    .put(LocalDate.of(2021, 1, 12), 1)
                    .buildOrThrow()))
        .hasValue(DateInterval.create(LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 11)));
  }

  @Test
  public void computeMajorityServiceCoverage_infrequent() {
    NavigableMap<LocalDate, Integer> tripCountByDate = new TreeMap<>();
    // We have infrequent trips over a long interval.
    for (LocalDate d = LocalDate.of(2021, 1, 1), e = LocalDate.of(2021, 12, 31);
        !d.isAfter(e);
        d = d.plusDays(1)) {
      tripCountByDate.put(d, 1);
    }
    // We have frequent trips over a short interval.
    for (LocalDate d = LocalDate.of(2021, 9, 1), e = LocalDate.of(2021, 10, 31);
        !d.isAfter(e);
        d = d.plusDays(1)) {
      tripCountByDate.put(d, 101);
    }
    assertThat(computeMajorityServiceCoverage(tripCountByDate))
        .hasValue(DateInterval.create(LocalDate.of(2021, 9, 1), LocalDate.of(2021, 10, 31)));
  }

  @Test
  public void countTripsForEachServiceDate_frequencyBased() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(
            countTripsForEachServiceDate(
                ImmutableMap.of(
                    "service1",
                    ImmutableSortedSet.of(LocalDate.of(2021, 2, 1)),
                    "service2",
                    ImmutableSortedSet.of(LocalDate.of(2021, 2, 28))),
                GtfsTripTableContainer.forEntities(
                    ImmutableList.of(
                        new GtfsTrip.Builder()
                            .setCsvRowNumber(2)
                            .setTripId("trip1")
                            .setServiceId("service1")
                            .build(),
                        new GtfsTrip.Builder()
                            .setCsvRowNumber(2)
                            .setTripId("trip2")
                            .setServiceId("service2")
                            .build()),
                    noticeContainer),
                GtfsFrequencyTableContainer.forEntities(
                    ImmutableList.of(
                        new GtfsFrequency.Builder()
                            .setCsvRowNumber(2)
                            .setTripId("trip2")
                            .setStartTime(GtfsTime.fromString("12:00:00"))
                            .setEndTime(GtfsTime.fromString("13:00:00"))
                            .setHeadwaySecs(60 * 10)
                            .build()),
                    noticeContainer)))
        .containsExactly(LocalDate.of(2021, 2, 1), 1, LocalDate.of(2021, 2, 28), 6);
  }

  @Test
  public void countTripsForEachServiceDate_noData() {
    NoticeContainer noticeContainer = new NoticeContainer();
    assertThat(
            countTripsForEachServiceDate(
                ImmutableMap.of(),
                GtfsTripTableContainer.forEntities(ImmutableList.of(), noticeContainer),
                GtfsFrequencyTableContainer.forEntities(ImmutableList.of(), noticeContainer)))
        .isEmpty();
  }
}
