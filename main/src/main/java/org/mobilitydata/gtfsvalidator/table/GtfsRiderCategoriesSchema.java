package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("rider_categories.txt")
public interface GtfsRiderCategoriesSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String riderCategoryId();

  @Required
  String riderCategoryName();

  @RequiredColumn
  @DefaultValue("0")
  GtfsRiderFareCategory isDefaultFareCategory();

  @FieldType(FieldTypeEnum.URL)
  String eligibilityUrl();
}
