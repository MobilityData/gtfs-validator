package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("location_groups.txt")
public interface GtfsLocationGroupsSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String locationGroupId();

  @MixedCase
  @NoInvalidCharacters
  String locationGroupName();
}
