package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates that the `start_pickup_drop_off_window` or `end_pickup_drop_off_window` fields are not
 * set when the `pickup_type` is regularly scheduled (0) or must be coordinated with the driver (3),
 * and that these fields are not set when the `drop_off_type` is regularly scheduled (0).
 *
 * <p>Generated notices include: - {@link ForbiddenPickupTypeNotice} - {@link
 * ForbiddenDropOffTypeNotice} if the `drop_off_type` is invalid.
 */
@GtfsValidator
public class PickupDropOffTypeValidator extends SingleEntityValidator<GtfsStopTime> {
  @Override
  public void validate(GtfsStopTime entity, NoticeContainer noticeContainer) {
    if ((entity.hasStartPickupDropOffWindow() || entity.hasEndPickupDropOffWindow())
        && (entity.pickupType().equals(GtfsPickupDropOff.ALLOWED)
            || entity.pickupType().equals(GtfsPickupDropOff.ON_REQUEST_TO_DRIVER))) {
      noticeContainer.addValidationNotice(
          new ForbiddenPickupTypeNotice(
              entity.csvRowNumber(),
              entity.startPickupDropOffWindow(),
              entity.endPickupDropOffWindow()));
    }

    if ((entity.hasStartPickupDropOffWindow() || entity.hasEndPickupDropOffWindow())
        && entity.dropOffType().equals(GtfsPickupDropOff.ALLOWED)) {
      noticeContainer.addValidationNotice(
          new ForbiddenDropOffTypeNotice(
              entity.csvRowNumber(),
              entity.startPickupDropOffWindow(),
              entity.endPickupDropOffWindow()));
    }
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return header.hasColumn(GtfsStopTime.PICKUP_TYPE_FIELD_NAME)
        && header.hasColumn(GtfsStopTime.DROP_OFF_TYPE_FIELD_NAME);
  }

  /**
   * pickup_drop_off_window fields are forbidden when the pickup_type is regularly scheduled (0) or
   * must be coordinated with the driver (3).
   */
  @GtfsValidationNotice(severity = ERROR)
  public static class ForbiddenPickupTypeNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public ForbiddenPickupTypeNotice(
        int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }

  /**
   * pickup_drop_off_window fields are forbidden when the drop_off_type is regularly scheduled (0).
   */
  @GtfsValidationNotice(severity = ERROR)
  public static class ForbiddenDropOffTypeNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public ForbiddenDropOffTypeNotice(
        int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }
}
