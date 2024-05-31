package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;

public class TransferStopIdsConditionalValidatorTest {

  private NoticeContainer noticeContainer;

  @Before
  public void setUp() {
    noticeContainer = new NoticeContainer();
  }

  /**
   * This test is used to verify that the validator does not generate a notice when the {@code
   * from_stop_id} is missing for in-seat transfer types.
   */
  @Test
  public void testTransferFromStopIdNoInSeatTransferNoNotice() {
    //        In seat transfer types are 4 and 5, so we test for all other types
    for (int i = 4; i <= 5; i++) {
      GtfsTransferType transferType = GtfsTransferType.forNumber(i);
      GtfsTransferTableContainer gtfsTransferTableContainer =
          GtfsTransferTableContainer.forEntities(
              ImmutableList.of(new GtfsTransfer.Builder().setTransferType(transferType).build()),
              noticeContainer);

      new TransferStopIdsConditionalValidator(gtfsTransferTableContainer).validate(noticeContainer);

      assertThat(noticeContainer.getValidationNotices()).isEmpty();
      noticeContainer.getValidationNotices().clear();
    }
  }

  /**
   * This test is used to verify that the validator generates a notice when the {@code from_stop_id}
   * is missing for transfer types other than in-seat transfer types.
   */
  @Test
  public void testTransferMissingFromStopIdNoInSeatTransfer() {
    for (int i = 0; i < 4; i++) {
      GtfsTransferType transferType = GtfsTransferType.forNumber(i);
      GtfsTransferTableContainer gtfsTransferTableContainer =
          GtfsTransferTableContainer.forEntities(
              ImmutableList.of(new GtfsTransfer.Builder().setTransferType(transferType).build()),
              noticeContainer);

      new TransferStopIdsConditionalValidator(gtfsTransferTableContainer).validate(noticeContainer);

      assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
      assertThat(noticeContainer.getValidationNotices())
          .containsExactlyElementsIn(
              Arrays.asList(
                  new MissingRequiredFieldNotice(
                      GtfsTransfer.FILENAME, 0, GtfsTransfer.FROM_STOP_ID_FIELD_NAME),
                  new MissingRequiredFieldNotice(
                      GtfsTransfer.FILENAME, 0, GtfsTransfer.TO_STOP_ID_FIELD_NAME)));

      noticeContainer.getValidationNotices().clear();
    }
  }

  /**
   * This test is used to verify that the validator doesn't generate a notice when the {@code
   * to_stop_id} and {@code from_stop_id} are present for all transfer types.
   */
  @Test
  public void testTransferFromStopIdNoInSeatTransfer() {
    for (int i = 0; i <= 5; i++) {
      GtfsTransferType transferType = GtfsTransferType.forNumber(i);
      GtfsTransferTableContainer gtfsTransferTableContainer =
          GtfsTransferTableContainer.forEntities(
              ImmutableList.of(
                  new GtfsTransfer.Builder()
                      .setFromStopId("stop1")
                      .setToStopId("stop2")
                      .setTransferType(transferType)
                      .build()),
              noticeContainer);

      new TransferStopIdsConditionalValidator(gtfsTransferTableContainer).validate(noticeContainer);

      assertThat(noticeContainer.getValidationNotices()).isEmpty();
      noticeContainer.getValidationNotices().clear();
    }
  }
}
