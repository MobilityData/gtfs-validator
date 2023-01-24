package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

/** */
@GtfsEnumValue(name = "CASH", value = 0)
@GtfsEnumValue(name = "CONTACTLESS_PAYMENT", value = 1)
@GtfsEnumValue(name = "TRANSIT_CARD", value = 2)
@GtfsEnumValue(name = "MOBILE_APP", value = 3)
public interface GtfsFarePaymentOptionTypeEnum {}
