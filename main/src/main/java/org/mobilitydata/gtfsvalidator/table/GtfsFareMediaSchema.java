package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("fare_media.txt")
public interface GtfsFareMediaSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String fareMediaId();

  @NoInvalidCharacters
  String fareMediaName();

  @Required
  GtfsFareMediaType fareMediaType();
}
