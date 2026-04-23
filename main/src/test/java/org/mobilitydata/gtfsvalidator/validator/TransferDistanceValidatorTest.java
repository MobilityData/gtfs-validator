package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer.Builder;

public class TransferDistanceValidatorTest {

  private final NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void testDistanceAbove10KmGeneratesWarning() {
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder().setStopId("s0").setStopLat(0.0).setStopLon(0.0).build(),
                new GtfsStop.Builder().setStopId("s1").setStopLat(0.0).setStopLon(0.1).build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setCsvRowNumber(1).setFromStopId("s0").setToStopId("s1").build()),
            noticeContainer);

    new TransferDistanceValidator(transfers, stops).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferDistanceValidator.TransferDistanceTooLargeNotice(
                transfers.getEntities().get(0), 11.119510117748394));
  }

  @Test
  public void testDistanceAbove2KmGeneratesNotice() {
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder().setStopId("s0").setStopLat(0.0).setStopLon(0.0).build(),
                new GtfsStop.Builder().setStopId("s1").setStopLat(0.0).setStopLon(0.02).build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setCsvRowNumber(1).setFromStopId("s0").setToStopId("s1").build()),
            noticeContainer);

    new TransferDistanceValidator(transfers, stops).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferDistanceValidator.TransferDistanceAbove_2KmNotice(
                transfers.getEntities().get(0), 2.2239020235496785));
  }

  @Test
  public void testDistanceBellow2KmYieldsNoNotice() {
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new GtfsStop.Builder().setStopId("s0").setStopLat(0.0).setStopLon(0.0).build(),
                new GtfsStop.Builder().setStopId("s1").setStopLat(0.0).setStopLon(0.01).build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setCsvRowNumber(1).setFromStopId("s0").setToStopId("s1").build()),
            noticeContainer);

    new TransferDistanceValidator(transfers, stops).validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
