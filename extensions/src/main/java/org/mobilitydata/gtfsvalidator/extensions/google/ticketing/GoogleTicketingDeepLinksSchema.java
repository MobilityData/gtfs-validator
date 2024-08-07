package org.mobilitydata.gtfsvalidator.extensions.google.ticketing;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("ticketing_deep_links.txt")
public interface GoogleTicketingDeepLinksSchema {
  @FieldType(FieldTypeEnum.ID)
  @Required
  @PrimaryKey
  String ticketingDeepLinkId();

  @FieldType(FieldTypeEnum.URL)
  String webUrl();
}
