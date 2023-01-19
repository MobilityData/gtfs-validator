package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer.Builder;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;
import org.mobilitydata.gtfsvalidator.validator.TransfersInSeatTransferTypeValidator.TransferWithSuspiciousMidTripInSeatNotice;
import org.mobilitydata.gtfsvalidator.validator.TransfersStopTypeValidator.TransferWithInvalidStopLocationTypeNotice;

public class TransfersInSeatTransferTypeValidatorTest {

  private final NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void testValidInSeatTransfer() {
    // In-seat transfer between the last stop of the from-trip and the first-stop of the to-trip.
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder()
                    .setStopId("s0")
                    .setLocationType(GtfsLocationType.STOP)
                    .build(),
                new GtfsStop.Builder()
                    .setStopId("s1")
                    .setLocationType(GtfsLocationType.STOP)
                    .build()),
            noticeContainer);
    GtfsStopTimeTableContainer stopTimes =
        GtfsStopTimeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s?")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s0")
                    .setStopSequence(1)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s1")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s?")
                    .setStopSequence(1)
                    .build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setFromStopId("s0")
                    .setToStopId("s1")
                    .setFromTripId("t0")
                    .setToTripId("t1")
                    .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
                    .build()),
            noticeContainer);

    new TransfersInSeatTransferTypeValidator(transfers, stops, stopTimes).validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testInvalidInSeatTransferToStation() {
    // Invalid: `to_stop_id` refers to a station, which is forbidden for in-seat transfers.
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder()
                    .setStopId("s0")
                    .setLocationType(GtfsLocationType.STOP)
                    .build(),
                new GtfsStop.Builder()
                    .setStopId("s1")
                    .setLocationType(GtfsLocationType.STATION)
                    .build()),
            noticeContainer);
    GtfsStopTimeTableContainer stopTimes =
        GtfsStopTimeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s?")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s0")
                    .setStopSequence(1)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s1")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s?")
                    .setStopSequence(1)
                    .build()),
            noticeContainer);
    GtfsTransfer transfer =
        new Builder()
            .setCsvRowNumber(2)
            .setFromStopId("s0")
            .setToStopId("s1")
            .setFromTripId("t0")
            .setToTripId("t1")
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
            .build();
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(ImmutableList.of(transfer), noticeContainer);

    new TransfersInSeatTransferTypeValidator(transfers, stops, stopTimes).validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferWithInvalidStopLocationTypeNotice(
                transfer, TransferDirection.TRANSFER_TO, GtfsLocationType.STATION));
  }

  @Test
  public void testSuspiciousMidTripInSeatTransfer() {
    // Invalid: `to_stop_id` refers to a station, which is forbidden for in-seat transfers.
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder()
                    .setStopId("s0")
                    .setLocationType(GtfsLocationType.STOP)
                    .build(),
                new GtfsStop.Builder()
                    .setStopId("s1")
                    .setLocationType(GtfsLocationType.STOP)
                    .build()),
            noticeContainer);
    GtfsStopTimeTableContainer stopTimes =
        GtfsStopTimeTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s0")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t0")
                    .setStopId("s?")
                    .setStopSequence(1)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s?")
                    .setStopSequence(0)
                    .build(),
                new GtfsStopTime.Builder()
                    .setTripId("t1")
                    .setStopId("s1")
                    .setStopSequence(1)
                    .build()),
            noticeContainer);
    GtfsTransfer transfer =
        new Builder()
            .setCsvRowNumber(2)
            .setFromStopId("s0")
            .setToStopId("s1")
            .setFromTripId("t0")
            .setToTripId("t1")
            .setTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
            .build();
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(ImmutableList.of(transfer), noticeContainer);

    new TransfersInSeatTransferTypeValidator(transfers, stops, stopTimes).validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferWithSuspiciousMidTripInSeatNotice(
                transfer, TransferDirection.TRANSFER_FROM),
            new TransferWithSuspiciousMidTripInSeatNotice(transfer, TransferDirection.TRANSFER_TO));
  }
}
