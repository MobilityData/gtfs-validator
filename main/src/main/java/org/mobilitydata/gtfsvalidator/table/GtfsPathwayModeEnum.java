package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "WALKWAY", value = 1)
@GtfsEnumValue(name = "STAIRS", value = 2)
@GtfsEnumValue(name = "MOVING_SIDEWALK", value = 3)
@GtfsEnumValue(name = "ESCALATOR", value = 4)
@GtfsEnumValue(name = "ELEVATOR", value = 5)
@GtfsEnumValue(name = "FARE_GATE", value = 6)
@GtfsEnumValue(name = "EXIT_GATE", value = 7)
public interface GtfsPathwayModeEnum {
}
