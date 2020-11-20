package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "STOP", value = 0)
@GtfsEnumValue(name = "STATION", value = 1)
@GtfsEnumValue(name = "ENTRANCE", value = 2)
@GtfsEnumValue(name = "GENERIC_NODE", value = 3)
@GtfsEnumValue(name = "BOARDING_AREA", value = 4)
public interface GtfsLocationTypeEnum {
}
