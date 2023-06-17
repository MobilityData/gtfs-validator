package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("mixed_case.txt")
public interface MixedCaseTestSchema {
  @RequiredValue
  @MixedCase
  String someField();
}
