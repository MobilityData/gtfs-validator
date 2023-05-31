package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("fare_media.txt")
public interface GtfsFareMediaSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @RequiredValue
  String fareMediaId();

  String fareMediaName();

  @RequiredValue
  GtfsFareMediaType fareMediaType();
}
