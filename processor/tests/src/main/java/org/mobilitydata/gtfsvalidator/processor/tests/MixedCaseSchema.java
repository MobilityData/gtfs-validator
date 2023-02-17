package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.MixedCase;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("mixed_case.txt")
public interface MixedCaseSchema {
  @Required
  @MixedCase
  String mixedCase();
}
