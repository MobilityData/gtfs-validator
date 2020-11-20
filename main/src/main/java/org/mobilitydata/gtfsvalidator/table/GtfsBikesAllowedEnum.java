package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "UNKNOWN", value = 0)
@GtfsEnumValue(name = "ALLOWED", value = 1)
@GtfsEnumValue(name = "NOT_ALLOWED", value = 2)
public interface GtfsBikesAllowedEnum {
}
