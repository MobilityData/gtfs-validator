package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "ALLOWED", value = 0)
@GtfsEnumValue(name = "NOT_AVAILABLE", value = 1)
@GtfsEnumValue(name = "MUST_PHONE", value = 2)
@GtfsEnumValue(name = "ON_REQUEST_TO_DRIVER", value = 3)
public interface GtfsPickupDropOffEnum {
}
