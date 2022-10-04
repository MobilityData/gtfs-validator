package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.ForbiddenFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;

public class TransfersTransferTypeValidatorTest {

  private NoticeContainer noticeContainer = new NoticeContainer();
  private TransfersTransferTypeValidator validator = new TransfersTransferTypeValidator();

  @Test
  public void testRecommendedStopToStopTransfer() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.RECOMMENDED)
            .setFromStopId("s1")
            .setToStopId("s2")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testRecommendedStopToStopTransfer_MissingFromStopId() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.RECOMMENDED)
            .setToStopId("s2")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldNotice("transfers.txt", 2, "from_stop_id"));
  }

  @Test
  public void testRecommendedStopToStopTransfer_MissingToStopId() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.RECOMMENDED)
            .setFromStopId("s1")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldNotice("transfers.txt", 2, "to_stop_id"));
  }

  @Test
  public void testRecommendedRouteToRouteTransfer() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.RECOMMENDED)
            .setFromStopId("s1")
            .setFromRouteId("r1")
            .setToStopId("s2")
            .setToRouteId("r2")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testRecommendedTripToTripTransfer() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.RECOMMENDED)
            .setFromStopId("s1")
            .setFromRouteId("r1")
            .setFromTripId("t1")
            .setToStopId("s2")
            .setToRouteId("r2")
            .setToTripId("t2")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testLinkedTripInSeatTransfer() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
            .setFromTripId("t1")
            .setToTripId("t2")
            .build(),
        noticeContainer);
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(3)
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_NOT_ALLOWED)
            .setFromTripId("t3")
            .setToTripId("t4")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testLinkedTripInSeatTransfer_conditionallyForbiddenFields() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
            .setFromStopId("s1")
            .setFromRouteId("r1")
            .setFromTripId("t1")
            .setToStopId("s2")
            .setToRouteId("r2")
            .setToTripId("t2")
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new ForbiddenFieldNotice("transfers.txt", 2, "from_stop_id"),
            new ForbiddenFieldNotice("transfers.txt", 2, "to_stop_id"),
            new ForbiddenFieldNotice("transfers.txt", 2, "from_route_id"),
            new ForbiddenFieldNotice("transfers.txt", 2, "to_route_id"));
  }

  @Test
  public void testLinkedTripInSeatTransfer_conditionallyRequiredFields() {
    validator.validate(
        new GtfsTransfer.Builder()
            .setCsvRowNumber(2)
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
            .build(),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRequiredFieldNotice("transfers.txt", 2, "from_trip_id"),
            new MissingRequiredFieldNotice("transfers.txt", 2, "to_trip_id"));
  }
}
