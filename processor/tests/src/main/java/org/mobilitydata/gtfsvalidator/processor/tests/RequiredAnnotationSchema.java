package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("required.txt")
public interface RequiredAnnotationSchema {

  @Required
  String valueRequired();

  String valueNotRequired();
}
