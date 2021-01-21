package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.OverlappingFrequencyNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class OverlappingFrequencyValidatorTest {
  private GtfsFrequency createFrequency(
      long csvRowNumber, String tripId, String startTime, String endTime, int headwaySecs) {
    return new GtfsFrequency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStartTime(GtfsTime.fromString(startTime))
        .setEndTime(GtfsTime.fromString(endTime))
        .setHeadwaySecs(headwaySecs)
        .build();
  }

  private List<ValidationNotice> validateFrequencies(GtfsFrequency... frequencies) {
    NoticeContainer noticeContainer = new NoticeContainer();
    OverlappingFrequencyValidator validator = new OverlappingFrequencyValidator();
    validator.table =
        GtfsFrequencyTableContainer.forEntities(Arrays.asList(frequencies), noticeContainer);
    validator.validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void validSequentialInOrder() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t0", "07:00:00", "10:00:00", 300)))
        .isEmpty();
  }

  @Test
  public void validSequentialReversed() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "07:00:00", "10:00:00", 300),
                createFrequency(3, "t0", "05:00:00", "07:00:00", 600)))
        .isEmpty();
  }

  @Test
  public void validWithGap() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t0", "08:00:00", "10:00:00", 300)))
        .isEmpty();
  }

  @Test
  public void validDifferentTrips() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t1", "06:00:00", "10:00:00", 300)))
        .isEmpty();
  }

  @Test
  public void overlappingPartially() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t0", "06:00:00", "10:00:00", 300)))
        .containsExactly(
            new OverlappingFrequencyNotice(
                2, GtfsTime.fromString("07:00:00"), 3, GtfsTime.fromString("06:00:00"), "t0"));
  }

  @Test
  public void overlappingIncludedSameStart() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t0", "05:00:00", "06:30:00", 300)))
        .containsExactly(
            new OverlappingFrequencyNotice(
                3, GtfsTime.fromString("06:30:00"), 2, GtfsTime.fromString("05:00:00"), "t0"));
  }

  @Test
  public void overlappingIncludedSameEnd() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "07:00:00", 600),
                createFrequency(3, "t0", "06:30:00", "07:00:00", 300)))
        .containsExactly(
            new OverlappingFrequencyNotice(
                2, GtfsTime.fromString("07:00:00"), 3, GtfsTime.fromString("06:30:00"), "t0"));
  }

  @Test
  public void overlappingIncluded() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "07:00:00", "12:00:00", 600),
                createFrequency(3, "t0", "08:00:00", "11:00:00", 300)))
        .containsExactly(
            new OverlappingFrequencyNotice(
                2, GtfsTime.fromString("12:00:00"), 3, GtfsTime.fromString("08:00:00"), "t0"));
  }

  @Test
  public void overlappingThreeIntervals() {
    assertThat(
            validateFrequencies(
                createFrequency(2, "t0", "05:00:00", "05:25:00", 600),
                createFrequency(3, "t0", "05:00:00", "05:15:00", 300),
                createFrequency(4, "t0", "05:20:00", "05:40:00", 300)))
        .containsExactly(
            new OverlappingFrequencyNotice(
                3, GtfsTime.fromString("05:15:00"), 2, GtfsTime.fromString("05:00:00"), "t0"),
            new OverlappingFrequencyNotice(
                2, GtfsTime.fromString("05:25:00"), 4, GtfsTime.fromString("05:20:00"), "t0"));
  }
}
