package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

@GtfsTable("enum_sizes.txt")
public interface EnumSizesSchema {
  ShortGtfs shortEnum();

  ByteGtfs byteEnum();

  IntGtfs intEnum();
}
