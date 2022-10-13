package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop.Builder;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;
import org.mobilitydata.gtfsvalidator.validator.TransfersStopTypeValidator.TransferWithInvalidStopLocationTypeNotice;

public class TransfersStopTypeValidatorTest {

  private NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void testStopToStationTransfer() {
    // Transfers between a stop and a station are allowed.
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setStopId("s0").setLocationType(GtfsLocationType.STOP).build(),
                new Builder().setStopId("s1").setLocationType(GtfsLocationType.STATION).build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new GtfsTransfer.Builder()
                    .setFromStopId("s0")
                    .setToStopId("s1")
                    .setTransferType(GtfsTransferType.RECOMMENDED)
                    .build()),
            noticeContainer);

    new TransfersStopTypeValidator(transfers, stops).validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testEntranceToGenericNodeTransfer() {
    // Transfers between an entrance and a generic pathway node are NOT allowed.
    GtfsStopTableContainer stops =
        GtfsStopTableContainer.forEntities(
            ImmutableList.of(
                new Builder().setStopId("s0").setLocationType(GtfsLocationType.ENTRANCE).build(),
                new Builder()
                    .setStopId("s1")
                    .setLocationType(GtfsLocationType.GENERIC_NODE)
                    .build()),
            noticeContainer);
    GtfsTransferTableContainer transfers =
        GtfsTransferTableContainer.forEntities(
            ImmutableList.of(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setFromStopId("s0")
                    .setToStopId("s1")
                    .setTransferType(GtfsTransferType.RECOMMENDED)
                    .build()),
            noticeContainer);

    new TransfersStopTypeValidator(transfers, stops).validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new TransferWithInvalidStopLocationTypeNotice(
                2, "from_stop_id", "s0", GtfsLocationType.ENTRANCE),
            new TransferWithInvalidStopLocationTypeNotice(
                2, "to_stop_id", "s1", GtfsLocationType.GENERIC_NODE));
  }
}
