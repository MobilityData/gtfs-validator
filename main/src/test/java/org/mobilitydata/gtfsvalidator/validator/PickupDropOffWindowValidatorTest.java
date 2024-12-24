package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class PickupDropOffWindowValidatorTest {
  @Test
  public void shouldGenerateForbiddenArrivalOrDepartureTimeNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    PickupDropOffWindowValidator validator = new PickupDropOffWindowValidator();

    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setArrivalTime(GtfsTime.fromString("00:00:00"))
            .setDepartureTime(GtfsTime.fromString("00:00:01"))
            .setStartPickupDropOffWindow(GtfsTime.fromString("00:00:02"))
            .setEndPickupDropOffWindow(GtfsTime.fromString("00:00:03"))
            .build();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .hasSize(
            1); //
                // assertThat(Optional.ofNullable(noticeContainer.getValidationNotices().stream().collect(Collectors.toList()).get(0))).isInstanceOf(PickupDropOffWindowValidator.ForbiddenArrivalOrDepartureTimeNotice.class);
    assertThat(noticeContainer.getValidationNotices().stream().findFirst().get())
        .isInstanceOf(PickupDropOffWindowValidator.ForbiddenArrivalOrDepartureTimeNotice.class);
  }

  @Test
  public void shouldGenerateMissingPickupOrDropOffWindowNotice_missingStart() {
    NoticeContainer noticeContainer = new NoticeContainer();
    PickupDropOffWindowValidator validator = new PickupDropOffWindowValidator();

    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setEndPickupDropOffWindow(GtfsTime.fromString("00:00:03"))
            .build();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .hasSize(
            1); //
                // assertThat(Optional.ofNullable(noticeContainer.getValidationNotices().stream().collect(Collectors.toList()).get(0))).isInstanceOf(PickupDropOffWindowValidator.ForbiddenArrivalOrDepartureTimeNotice.class);
    assertThat(noticeContainer.getValidationNotices().stream().findFirst().get())
        .isInstanceOf(PickupDropOffWindowValidator.MissingPickupOrDropOffWindowNotice.class);
  }

  @Test
  public void shouldGenerateMissingPickupOrDropOffWindowNotice_missingEnd() {
    NoticeContainer noticeContainer = new NoticeContainer();
    PickupDropOffWindowValidator validator = new PickupDropOffWindowValidator();

    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setStartPickupDropOffWindow(GtfsTime.fromString("00:00:03"))
            .build();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .hasSize(
            1); //
                // assertThat(Optional.ofNullable(noticeContainer.getValidationNotices().stream().collect(Collectors.toList()).get(0))).isInstanceOf(PickupDropOffWindowValidator.ForbiddenArrivalOrDepartureTimeNotice.class);
    assertThat(noticeContainer.getValidationNotices().stream().findFirst().get())
        .isInstanceOf(PickupDropOffWindowValidator.MissingPickupOrDropOffWindowNotice.class);
  }

  @Test
  public void shouldGenerateInvalidPickupDropOffWindowNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    PickupDropOffWindowValidator validator = new PickupDropOffWindowValidator();

    GtfsStopTime stopTime =
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setStartPickupDropOffWindow(GtfsTime.fromString("00:00:03"))
            .setEndPickupDropOffWindow(GtfsTime.fromString("00:00:02"))
            .build();
    validator.validate(stopTime, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .hasSize(
            1); //
                // assertThat(Optional.ofNullable(noticeContainer.getValidationNotices().stream().collect(Collectors.toList()).get(0))).isInstanceOf(PickupDropOffWindowValidator.ForbiddenArrivalOrDepartureTimeNotice.class);
    assertThat(noticeContainer.getValidationNotices().stream().findFirst().get())
        .isInstanceOf(PickupDropOffWindowValidator.InvalidPickupDropOffWindowNotice.class);
  }
}
