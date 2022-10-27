package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.table.ByteGtfs;
import org.mobilitydata.gtfsvalidator.table.IntGtfs;
import org.mobilitydata.gtfsvalidator.table.ShortGtfs;

@GtfsTable("enum_sizes.txt")
public interface EnumSizesSchema {
  ShortGtfs shortEnum();

  ByteGtfs byteEnum();

  IntGtfs intEnum();
}
