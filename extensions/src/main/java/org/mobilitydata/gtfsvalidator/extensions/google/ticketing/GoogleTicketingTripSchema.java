package org.mobilitydata.gtfsvalidator.extensions.google.ticketing;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

@GtfsTable("trips.txt")
public interface GoogleTicketingTripSchema {
  String ticketingTripId();

  GoogleTicketingType ticketingType();
}
