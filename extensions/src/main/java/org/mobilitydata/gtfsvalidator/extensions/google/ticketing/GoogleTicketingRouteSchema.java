package org.mobilitydata.gtfsvalidator.extensions.google.ticketing;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

@GtfsTable("routes.txt")
public interface GoogleTicketingRouteSchema {
  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "ticketing_deep_links.txt", field = "ticketing_deep_link_id")
  String ticketingDeepLinkId();
}
