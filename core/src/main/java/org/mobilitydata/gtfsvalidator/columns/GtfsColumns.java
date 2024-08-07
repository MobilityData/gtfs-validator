package org.mobilitydata.gtfsvalidator.columns;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.ColumnStoreTypes;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * This marker interface is used to generate the {@link GtfsColumnStore} implementation.
 *
 * <p>While we could maintain {@link GtfsColumnStore} by hand, the set of code to support each type
 * specified in {@link ColumnStoreTypes} is very repetitive. It was easier to generate the class
 * instead, which allows for easier refactoring and adding new types in the future.
 */
@ColumnStoreTypes({
  byte.class,
  short.class,
  int.class,
  double.class,
  String.class,
  BigDecimal.class,
  Currency.class,
  GtfsColor.class,
  GtfsDate.class,
  GtfsTime.class,
  Locale.class,
  ZoneId.class
})
public interface GtfsColumns {}
