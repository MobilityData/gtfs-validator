package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;

@GtfsTable("many_fields.txt")
public interface ManyFieldsSchema {
  int field1();

  int field2();

  int field3();

  int field4();

  int field5();

  int field6();

  int field7();

  int field8();

  int field9();

  int field10();
}
