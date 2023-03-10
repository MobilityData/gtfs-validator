package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.annotation.RequiredColumn;

@GtfsTable("required_column.txt")
public interface RequiredColumnAnnotationSchema {

  @Required
  String valueRequired();

  @RequiredColumn
  String columnRequired();
}
