package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "LIGHT_RAIL", value = 0)
@GtfsEnumValue(name = "SUBWAY", value = 1)
@GtfsEnumValue(name = "RAIL", value = 2)
@GtfsEnumValue(name = "BUS", value = 3)
@GtfsEnumValue(name = "FERRY", value = 4)
@GtfsEnumValue(name = "CABLE_TRAM", value = 5)
@GtfsEnumValue(name = "AERIAL_LIFT", value = 6)
@GtfsEnumValue(name = "FUNICULAR", value = 7)
@GtfsEnumValue(name = "TROLLEYBUS", value = 11)
@GtfsEnumValue(name = "MONORAIL", value = 12)
public interface GtfsRouteTypeEnum {
}
