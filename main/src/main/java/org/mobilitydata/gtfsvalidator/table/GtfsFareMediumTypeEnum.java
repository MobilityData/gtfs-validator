package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

/** */
@GtfsEnumValue(name = "NONE", value = 0)
@GtfsEnumValue(name = "PAPER_TICKET", value = 1)
@GtfsEnumValue(name = "TRANSIT_CARD", value = 2)
@GtfsEnumValue(name = "CONTACTLESS_EMV", value = 3)
@GtfsEnumValue(name = "MOBILE_APP", value = 4)
public interface GtfsFareMediumTypeEnum {}
