package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSortedSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.SortedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServicePeriodTest {
  @Test
  public void weeklyPatternFromMTWTFSS() {
    assertThat(ServicePeriod.weeklyPatternFromMTWTFSS(1, 1, 1, 0, 0, 1, 0)).isEqualTo(0b0100111);
    assertThat(ServicePeriod.weeklyPatternFromMTWTFSS(0, 0, 0, 0, 0, 0, 0)).isEqualTo(0);
    assertThat(ServicePeriod.weeklyPatternFromMTWTFSS(1, 1, 1, 1, 1, 1, 1)).isEqualTo(0b1111111);
  }

  @Test
  public void isIncludedInPattern() {
    // Test all days in 0b00100111 pattern, in the format of 0b0SSFTWTM
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.MONDAY, (byte) 0b0100111)).isTrue();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.TUESDAY, (byte) 0b0100111)).isTrue();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.WEDNESDAY, (byte) 0b0100111)).isTrue();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.THURSDAY, (byte) 0b0100111)).isFalse();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.FRIDAY, (byte) 0b0100111)).isFalse();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.SATURDAY, (byte) 0b0100111)).isTrue();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.SUNDAY, (byte) 0b0100111)).isFalse();

    // Test corner cases: all days included and no days included.
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.MONDAY, (byte) 0)).isFalse();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.MONDAY, (byte) 0b1111111)).isTrue();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.SUNDAY, (byte) 0)).isFalse();
    assertThat(ServicePeriod.isIncludedInPattern(DayOfWeek.SUNDAY, (byte) 0b1111111)).isTrue();
  }

  @Test
  public void segmentAddedAndRemovedDays() {
    final LocalDate serviceStart = LocalDate.of(2021, 1, 4);
    final LocalDate serviceEnd = LocalDate.of(2021, 1, 10);
    final byte weeklyPattern = 0b0000111;
    final SortedSet<LocalDate> addedDays =
        ImmutableSortedSet.of(
            LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 9), LocalDate.of(2021, 1, 10));
    final SortedSet<LocalDate> removedDays =
        ImmutableSortedSet.of(LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 9));
    ServicePeriod period =
        new ServicePeriod(serviceStart, serviceEnd, weeklyPattern, addedDays, removedDays);
    assertThat(period.getServiceStart()).isEqualTo(serviceStart);
    assertThat(period.getServiceEnd()).isEqualTo(serviceEnd);
    assertThat(period.getWeeklyPattern()).isEqualTo(weeklyPattern);
    assertThat(period.getAddedDays()).isEqualTo(addedDays);
    assertThat(period.getRemovedDays()).isEqualTo(removedDays);
    assertThat(period.toDates())
        .isEqualTo(
            ImmutableSortedSet.of(
                LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 10)));
  }

  @Test
  public void addedDays() {
    final SortedSet<LocalDate> dates =
        ImmutableSortedSet.of(
            LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 6), LocalDate.of(2021, 11, 28));
    assertThat(new ServicePeriod(dates).toDates()).isEqualTo(dates);
  }

  @Test
  public void segment() {
    // Mon 4 Jan 2021 - Fri 15 Jan 2021
    // Included days of week: Mon, Tue, Wed, Fri
    assertThat(
            new ServicePeriod(
                    LocalDate.of(2021, 1, 4),
                    LocalDate.of(2021, 1, 15),
                    ServicePeriod.weeklyPatternFromMTWTFSS(1, 1, 1, 0, 1, 0, 0),
                    ImmutableSortedSet.of(),
                    ImmutableSortedSet.of())
                .toDates())
        .isEqualTo(
            ImmutableSortedSet.of(
                LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 5),
                LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 8),
                LocalDate.of(2021, 1, 11), LocalDate.of(2021, 1, 12),
                LocalDate.of(2021, 1, 13), LocalDate.of(2021, 1, 15)));
  }

  @Test
  public void addedAndRemovedDays() {
    assertThat(
            new ServicePeriod(
                    ServicePeriod.EPOCH,
                    ServicePeriod.EPOCH,
                    (byte) 0,
                    ImmutableSortedSet.of(
                        LocalDate.of(2021, 1, 3),
                        LocalDate.of(2021, 1, 6),
                        LocalDate.of(2021, 11, 28)),
                    ImmutableSortedSet.of(LocalDate.of(2021, 1, 6)))
                .toDates())
        .isEqualTo(ImmutableSortedSet.of(LocalDate.of(2021, 1, 3), LocalDate.of(2021, 11, 28)));
  }

  @Test
  public void singleDay() {
    assertThat(
            new ServicePeriod(
                    LocalDate.of(2021, 1, 4),
                    LocalDate.of(2021, 1, 4),
                    (byte) 0b1111111,
                    ImmutableSortedSet.of(),
                    ImmutableSortedSet.of())
                .toDates())
        .isEqualTo(ImmutableSortedSet.of(LocalDate.of(2021, 1, 4)));
  }
}
