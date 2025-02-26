package org.mobilitydata.gtfsvalidator.validator;

import javax.annotation.Nullable;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class PickupBookingRuleIdValidator extends FileValidator {
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsBookingRulesTableContainer bookingRulesTable;

  @Inject
  public PickupBookingRuleIdValidator(
      GtfsStopTimeTableContainer stopTimeTable, GtfsBookingRulesTableContainer bookingRulesTable) {
    this.stopTimeTable = stopTimeTable;
    this.bookingRulesTable = bookingRulesTable;
  }

  public void validate(GtfsStopTime entity, NoticeContainer noticeContainer) {
    if (entity.hasPickupType()
        && entity.pickupType() == GtfsPickupDropOff.MUST_PHONE
        && !entity.hasPickupBookingRuleId()) {
      noticeContainer.addValidationNotice(
          new MissingPickupDropOffBookingRuleIdNotice(
              entity.csvRowNumber(),
              entity.pickupType(),
              entity.hasDropOffType() ? entity.dropOffType() : null));
    }
    if (entity.hasDropOffType()
        && entity.dropOffType() == GtfsPickupDropOff.MUST_PHONE
        && !entity.hasDropOffBookingRuleId()) {
      noticeContainer.addValidationNotice(
          new MissingPickupDropOffBookingRuleIdNotice(
              entity.csvRowNumber(),
              entity.hasPickupType() ? entity.pickupType() : null,
              entity.dropOffType()));
    }
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (bookingRulesTable.isMissingFile()) {
      return;
    }
    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      validate(stopTime, noticeContainer);
    }
  }

  /**
   * `pickup_booking_rule_id` is recommended when `pickup_type=2` and `drop_off_booking_rule_id` is
   * recommended when `drop_off_type=2`
   */
  @GtfsValidationNotice(
      severity = SeverityLevel.WARNING,
      files = @FileRefs(GtfsStopTimeSchema.class))
  static class MissingPickupDropOffBookingRuleIdNotice extends ValidationNotice {
    /** The row number of the faulty record in `stop_times.txt` */
    private final int csvRowNumber;

    /** The pickup type of the faulty record. */
    @Nullable private final GtfsPickupDropOff pickupType;

    /** The drop-off type of the faulty record. */
    @Nullable private final GtfsPickupDropOff dropOffType;

    public MissingPickupDropOffBookingRuleIdNotice(
        int csvRowNumber, GtfsPickupDropOff pickupType, GtfsPickupDropOff dropOffType) {
      this.csvRowNumber = csvRowNumber;
      this.pickupType = pickupType;
      this.dropOffType = dropOffType;
    }
  }
}
