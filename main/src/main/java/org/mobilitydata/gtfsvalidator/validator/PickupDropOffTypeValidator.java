package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPickupDropOff;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

public class PickupDropOffTypeValidator extends SingleEntityValidator<GtfsStopTime> {
  @Override
  public void validate(GtfsStopTime entity, NoticeContainer noticeContainer) {
    if ((entity.hasStartPickupDropOffWindow() || entity.hasEndPickupDropOffWindow()) &&
            (entity.pickupType().equals(GtfsPickupDropOff.ALLOWED) || entity.pickupType().equals(GtfsPickupDropOff.ON_REQUEST_TO_DRIVER))) {
      noticeContainer.addValidationNotice(
              new ForbiddenPickupTypeNotice(
                      entity.csvRowNumber(),
                      entity.startPickupDropOffWindow(),
                      entity.endPickupDropOffWindow()));
    }

    if ((entity.hasStartPickupDropOffWindow() || entity.hasEndPickupDropOffWindow()) &&
            entity.dropOffType().equals(GtfsPickupDropOff.ALLOWED)) {
      noticeContainer.addValidationNotice(
              new ForbiddenDropOffTypeNotice(
                      entity.csvRowNumber(),
                      entity.startPickupDropOffWindow(),
                      entity.endPickupDropOffWindow()));
    }
  }

  /**
   * pickup_drop_off_window fields are forbidden when the pickup_type is regularly scheduled (0) or must be coordinated with the driver (3).
   */
  @GtfsValidationNotice(severity = ERROR)
  public static class ForbiddenPickupTypeNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public ForbiddenPickupTypeNotice(int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
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

    public ForbiddenDropOffTypeNotice(int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }
}


