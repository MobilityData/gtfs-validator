package org.mobilitydata.gtfsvalidator.extensions.google.ticketing;

import java.util.Collection;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;

public class GoogleTicketingStopTimeValidator extends FileValidator {

  private final GtfsStopTimeTableContainer container;

  public GoogleTicketingStopTimeValidator(GtfsStopTimeTableContainer container) {
    this.container = container;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (Collection<GtfsStopTime> stopTimes : container.byStopIdMap().asMap().values()) {}
  }

  private static boolean ticketingIsUnavailable(GtfsStopTime stopTime) {
    /*
    if (!stopTime.hasExtension(GoogleTicketingStopTime.class)) {
      return false;
    }
    GoogleTicketingStopTime ticketingStopTime =
        stopTime.getExtension(GoogleTicketingStopTime.class);
    return ticketingStopTime.hasTicketingType()
        && ticketingStopTime.ticketingType() == GoogleTicketingType.UNAVAILABLE;
     */
    return false;
  }

  @GtfsValidationNotice(severity = SeverityLevel.ERROR)
  private static final class GoogleTicketingStopTimesWithInconsistentTicketingTypeNotice
      extends ValidationNotice {
    GoogleTicketingStopTimesWithInconsistentTicketingTypeNotice() {}
  }
}
