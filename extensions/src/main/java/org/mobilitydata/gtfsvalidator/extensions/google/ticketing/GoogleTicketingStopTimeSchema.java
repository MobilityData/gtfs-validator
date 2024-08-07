package org.mobilitydata.gtfsvalidator.extensions.google.ticketing;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

@GtfsTable("stop_times.txt")
public interface GoogleTicketingStopTimeSchema {
  GoogleTicketingType ticketingType();
}
