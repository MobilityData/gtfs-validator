package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

/**
 * Named "Method" instead of "Type" to avoid collision with parent schema class of the same name.
 */
@GtfsEnumValue(name = "CASH", value = 0)
@GtfsEnumValue(name = "CONTACTLESS_PAYMENT", value = 1)
@GtfsEnumValue(name = "TRANSIT_CARD", value = 2)
@GtfsEnumValue(name = "MOBILE_APP", value = 3)
public interface GtfsFarePaymentMethodEnum {}
