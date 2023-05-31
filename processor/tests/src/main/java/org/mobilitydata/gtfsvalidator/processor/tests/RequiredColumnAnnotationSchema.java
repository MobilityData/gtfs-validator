package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("required_column.txt")
public interface RequiredColumnAnnotationSchema {

  @RequiredValue
  String valueRequired();

  @RequiredColumn
  String columnRequired();
}
