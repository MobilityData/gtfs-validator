package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServiceIntervalTest {

  // ---------------------------------------------------------------------------
  // DateInterval tests
  // ---------------------------------------------------------------------------

  @Test
  public void dateInterval_singleDay_lengthIsOne() {
    DateInterval i = new DateInterval(d("2024-01-01"), d("2024-01-01"));
    assertThat(i.lengthInDays()).isEqualTo(1);
  }

  @Test
  public void dateInterval_multiDay_correctLength() {
    DateInterval i = new DateInterval(d("2024-01-01"), d("2024-01-07"));
    assertThat(i.lengthInDays()).isEqualTo(7);
  }

  @Test
  public void dateInterval_startAfterEnd_throws() {
    assertThrows(
        IllegalArgumentException.class, () -> new DateInterval(d("2024-01-10"), d("2024-01-01")));
  }

  @Test
  public void dateInterval_gapDaysUntil_adjacentIsZero() {
    DateInterval a = new DateInterval(d("2024-01-01"), d("2024-01-05"));
    DateInterval b = new DateInterval(d("2024-01-06"), d("2024-01-10"));
    assertThat(a.gapDaysUntil(b)).isEqualTo(0);
  }

  @Test
  public void dateInterval_gapDaysUntil_oneDayGap() {
    DateInterval a = new DateInterval(d("2024-01-01"), d("2024-01-05"));
    DateInterval b = new DateInterval(d("2024-01-07"), d("2024-01-10"));
    assertThat(a.gapDaysUntil(b)).isEqualTo(1);
  }

  @Test
  public void dateInterval_contains() {
    DateInterval i = new DateInterval(d("2024-01-01"), d("2024-01-07"));
    assertThat(i.contains(d("2024-01-01"))).isTrue();
    assertThat(i.contains(d("2024-01-04"))).isTrue();
    assertThat(i.contains(d("2024-01-07"))).isTrue();
    assertThat(i.contains(d("2023-12-31"))).isFalse();
    assertThat(i.contains(d("2024-01-08"))).isFalse();
  }

  // ---------------------------------------------------------------------------
  // ServiceInterval — empty / basic state
  // ---------------------------------------------------------------------------

  @Test
  public void serviceIntervals_empty_isEmpty() {
    ServiceInterval s = new ServiceInterval();
    assertThat(s.isEmpty()).isTrue();
    assertThat(s.getIntervals()).isEmpty();
    assertThat(s.getGaps()).isEmpty();
    assertThat(s.maxGapInDays()).isEqualTo(0);
    assertThat(s.firstActiveDate()).isNull();
    assertThat(s.lastActiveDate()).isNull();
  }

  // ---------------------------------------------------------------------------
  // addDate / removeDate
  // ---------------------------------------------------------------------------

  @Test
  public void addDate_singleDate_oneInterval() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-01"));
  }

  @Test
  public void addDate_adjacentDates_mergedIntoOneInterval() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-02"));
    s.addDate(d("2024-03-03"));
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-03"));
  }

  @Test
  public void addDate_nonAdjacentDates_separateIntervals() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-05"));
    assertThat(s.getIntervals())
        .containsExactly(interval("2024-03-01", "2024-03-01"), interval("2024-03-05", "2024-03-05"))
        .inOrder();
  }

  @Test
  public void addDate_bridgesGap_mergesThreeIntervals() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-03"));
    // Adding the middle date should merge all three.
    s.addDate(d("2024-03-02"));
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-03"));
  }

  @Test
  public void removeDate_fromMiddle_splitsTwoIntervals() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-02"));
    s.addDate(d("2024-03-03"));
    s.addDate(d("2024-03-04"));
    s.addDate(d("2024-03-05"));

    s.removeDate(d("2024-03-03"));

    assertThat(s.getIntervals())
        .containsExactly(interval("2024-03-01", "2024-03-02"), interval("2024-03-04", "2024-03-05"))
        .inOrder();
  }

  @Test
  public void removeDate_fromStart_trimsInterval() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-02"));
    s.addDate(d("2024-03-03"));

    s.removeDate(d("2024-03-01"));

    assertThat(s.getIntervals()).containsExactly(interval("2024-03-02", "2024-03-03"));
  }

  @Test
  public void removeDate_fromEnd_trimsInterval() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-02"));
    s.addDate(d("2024-03-03"));

    s.removeDate(d("2024-03-03"));

    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-02"));
  }

  @Test
  public void removeDate_singleDayInterval_intervalDisappears() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.removeDate(d("2024-03-01"));
    assertThat(s.getIntervals()).isEmpty();
    assertThat(s.isEmpty()).isTrue();
  }

  @Test
  public void removeDate_notPresent_noOp() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.removeDate(d("2024-04-15")); // completely outside
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-01"));
  }

  // ---------------------------------------------------------------------------
  // addInterval (weeklyPattern)
  // ---------------------------------------------------------------------------

  @Test
  public void addInterval_allDays_singleContiguousInterval() {
    ServiceInterval s = new ServiceInterval();
    // All 7 days active: 0b1111111
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-03-04"), d("2024-03-10"), allDays); // Mon–Sun
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-04", "2024-03-10"));
  }

  @Test
  public void addInterval_weekdaysOnly_weekendsAreGaps() {
    ServiceInterval s = new ServiceInterval();
    // Monday–Friday: bits 0–4 set = 0b0011111
    byte weekdays = ServicePeriod.weeklyPatternFromMTWTFSS(1, 1, 1, 1, 1, 0, 0);
    // Two full weeks: 2024-03-04 (Mon) to 2024-03-17 (Sun)
    s.addInterval(d("2024-03-04"), d("2024-03-17"), weekdays);

    List<DateInterval> intervals = s.getIntervals();
    // Expect two Mon–Fri blocks separated by the weekend.
    assertThat(intervals)
        .containsExactly(interval("2024-03-04", "2024-03-08"), interval("2024-03-11", "2024-03-15"))
        .inOrder();
  }

  @Test
  public void addInterval_zeroPattern_noIntervalsAdded() {
    ServiceInterval s = new ServiceInterval();
    s.addInterval(d("2024-03-04"), d("2024-03-10"), (byte) 0);
    assertThat(s.getIntervals()).isEmpty();
  }

  @Test
  public void addInterval_overlappingCalls_mergedCorrectly() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-03-01"), d("2024-03-10"), allDays);
    s.addInterval(d("2024-03-08"), d("2024-03-15"), allDays); // overlaps
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-15"));
  }

  @Test
  public void addInterval_adjacentCalls_mergedIntoOne() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-03-01"), d("2024-03-05"), allDays);
    s.addInterval(d("2024-03-06"), d("2024-03-10"), allDays); // adjacent
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-10"));
  }

  // ---------------------------------------------------------------------------
  // getGaps / maxGapInDays
  // ---------------------------------------------------------------------------

  @Test
  public void getGaps_singleInterval_noGaps() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-01"));
    s.addDate(d("2024-03-02"));
    assertThat(s.getGaps()).isEmpty();
    assertThat(s.maxGapInDays()).isEqualTo(0);
  }

  @Test
  public void getGaps_twoIntervals_oneGap() {
    ServiceInterval s = new ServiceInterval();
    // Active Jan 1–5, then Jan 20–25: gap is Jan 6–19 (14 days).
    s.addInterval(d("2024-01-01"), d("2024-01-05"), (byte) 0b1111111);
    s.addInterval(d("2024-01-20"), d("2024-01-25"), (byte) 0b1111111);

    List<DateInterval> gaps = s.getGaps();
    assertThat(gaps).containsExactly(interval("2024-01-06", "2024-01-19"));
    assertThat(s.maxGapInDays()).isEqualTo(14);
  }

  @Test
  public void getGaps_multipleGaps_allReturned() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-01-01"), d("2024-01-05"), allDays); // gap of 9 days
    s.addInterval(d("2024-01-15"), d("2024-01-20"), allDays); // gap of 4 days
    s.addInterval(d("2024-01-25"), d("2024-01-31"), allDays);

    List<DateInterval> gaps = s.getGaps();
    assertThat(gaps)
        .containsExactly(interval("2024-01-06", "2024-01-14"), interval("2024-01-21", "2024-01-24"))
        .inOrder();
    assertThat(s.maxGapInDays()).isEqualTo(9);
  }

  @Test
  public void maxGapInDays_exceedsThreshold_validationScenario() {
    // Simulates the real validation rule: gap > 13 days triggers notice.
    ServiceInterval s = new ServiceInterval();
    byte weekdays = ServicePeriod.weeklyPatternFromMTWTFSS(1, 1, 1, 1, 1, 0, 0);
    // Service runs Jan, then resumes in late February — gap > 13 days.
    s.addInterval(d("2024-01-01"), d("2024-01-31"), weekdays);
    s.addInterval(d("2024-02-20"), d("2024-03-31"), weekdays);

    assertThat(s.maxGapInDays()).isGreaterThan(13);
  }

  @Test
  public void maxGapInDays_withinThreshold_noNotice() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    // Gap of exactly 13 days — should NOT trigger (rule is > 13).
    s.addInterval(d("2024-01-01"), d("2024-01-10"), allDays);
    s.addInterval(d("2024-01-24"), d("2024-01-31"), allDays); // gap Jan 11–23 = 13 days

    assertThat(s.maxGapInDays()).isEqualTo(13);
    assertThat(s.maxGapInDays()).isAtMost(13); // would NOT trigger notice
  }

  // ---------------------------------------------------------------------------
  // firstActiveDate / lastActiveDate
  // ---------------------------------------------------------------------------

  @Test
  public void firstAndLastActiveDate_multipleIntervals() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-03-10"), d("2024-03-15"), allDays);
    s.addInterval(d("2024-03-01"), d("2024-03-05"), allDays);

    assertThat(s.firstActiveDate()).isEqualTo(d("2024-03-01"));
    assertThat(s.lastActiveDate()).isEqualTo(d("2024-03-15"));
  }

  // ---------------------------------------------------------------------------
  // Edge cases: addDate after removeDate, calendar_dates interaction
  // ---------------------------------------------------------------------------

  @Test
  public void removeDate_thenAddBack_restoresInterval() {
    ServiceInterval s = new ServiceInterval();
    byte allDays = (byte) 0b1111111;
    s.addInterval(d("2024-03-01"), d("2024-03-05"), allDays);
    s.removeDate(d("2024-03-03")); // splits into [1-2] and [4-5]
    s.addDate(d("2024-03-03")); // should merge back to [1-5]
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-01", "2024-03-05"));
  }

  @Test
  public void addDate_outsideExistingRange_extendsBoundary() {
    ServiceInterval s = new ServiceInterval();
    s.addDate(d("2024-03-05"));
    s.addDate(d("2024-03-04")); // extends left
    s.addDate(d("2024-03-06")); // extends right
    assertThat(s.getIntervals()).containsExactly(interval("2024-03-04", "2024-03-06"));
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  private static LocalDate d(String iso) {
    return LocalDate.parse(iso);
  }

  private static DateInterval interval(String start, String end) {
    return new DateInterval(LocalDate.parse(start), LocalDate.parse(end));
  }
}
