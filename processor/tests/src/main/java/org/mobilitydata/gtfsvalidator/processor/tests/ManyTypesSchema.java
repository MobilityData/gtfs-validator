package org.mobilitydata.gtfsvalidator.processor.tests;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("many_types.txt")
public interface ManyTypesSchema {
  int intValue();

  double doubleValue();

  String stringValue();

  BigDecimal bigDecimalValue();

  Currency currencyValue();

  GtfsColor colorValue();

  GtfsDate dateValue();

  GtfsTime timeValue();

  Locale localeValue();

  ZoneId zoneIdValue();
}
