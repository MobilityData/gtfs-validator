package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.TimeframeOverlapValidator.TimeframeOverlapNoice;

@RunWith(JUnit4.class)
public class TimeframeOverlapValidatorTest {

  @Test
  public void testSingleTimeframe() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("00:00:00"))
                    .setEndTime(GtfsTime.fromString("24:00:00"))
                    .setCsvRowNumber(2)
                    .build()))
        .isEmpty();
  }

  @Test
  public void testNoOverlap() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(2)
                    .build(),
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("17:00:00"))
                    .setEndTime(GtfsTime.fromString("18:00:00"))
                    .setCsvRowNumber(3)
                    .build()))
        .isEmpty();
  }

  @Test
  public void testNoOverlapButAdjacent() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(2)
                    .build(),
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("09:00:00"))
                    .setEndTime(GtfsTime.fromString("10:00:00"))
                    .setCsvRowNumber(3)
                    .build()))
        .isEmpty();
  }

  @Test
  public void testOverlap() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(2)
                    .build(),
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:30:00"))
                    .setEndTime(GtfsTime.fromString("09:30:00"))
                    .setCsvRowNumber(3)
                    .build()))
        .containsExactly(
            new TimeframeOverlapNoice(
                2,
                GtfsTime.fromString("09:00:00"),
                3,
                GtfsTime.fromString("08:30:00"),
                "PEAK",
                "WEEKDAY"));
  }

  @Test
  public void testWithDifferentServiceIds() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(2)
                    .build(),
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKEND")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(3)
                    .build()))
        .isEmpty();
  }

  @Test
  public void testWithDifferentGroupIds() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(2)
                    .build(),
                new GtfsTimeframe.Builder()
                    .setTimeframeGroupId("NON-PEAK")
                    .setServiceId("WEEKDAY")
                    .setStartTime(GtfsTime.fromString("08:00:00"))
                    .setEndTime(GtfsTime.fromString("09:00:00"))
                    .setCsvRowNumber(3)
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validate(GtfsTimeframe... timeframes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTimeframeTableContainer container =
        GtfsTimeframeTableContainer.forEntities(Arrays.asList(timeframes), noticeContainer);
    TimeframeOverlapValidator validator = new TimeframeOverlapValidator(container);
    validator.validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
