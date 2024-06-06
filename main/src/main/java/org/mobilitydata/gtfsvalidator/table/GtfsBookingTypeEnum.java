package org.mobilitydata.gtfsvalidator.table;
import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "REALTIME", value = 0)
@GtfsEnumValue(name = "SAMEDAY", value = 1)
@GtfsEnumValue(name = "PRIORDAY", value = 2)
public interface GtfsBookingTypeEnum {}
