package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.RequiredValue;

@GtfsTable("required.txt")
public interface RequiredAnnotationSchema {

  @RequiredValue
  String valueRequired();

  String valueNotRequired();
}
