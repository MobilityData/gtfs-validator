package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("location_group_stops.txt")
public interface GtfsLocationGroupStopsSchema extends GtfsEntity {

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "location_groups.txt", field = "location_group_id")
  @Index
  @Required
  String locationGroupId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "stops.txt", field = "stop_id")
  @Required
  String stopId();
}
