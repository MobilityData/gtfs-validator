package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "RECOMMENDED", value = 0)
@GtfsEnumValue(name = "TIMED", value = 1)
@GtfsEnumValue(name = "MINIMUM_TIME", value = 2)
@GtfsEnumValue(name = "IMPOSSIBLE", value = 3)
public interface GtfsTransferTypeEnum {
}
