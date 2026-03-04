package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

/**
 * Enum representing the cemv_support field values.
 *
 * <p>0 or empty: No contactless EMV information available for trips associated with this agency or
 * route. 1: Riders may use contactless EMVs as fare media for trips associated with this agency or
 * route. 2: contactless EMVs are not supported as fare media for trips associated with this agency
 * or route.
 */
@GtfsEnumValue(name = "NO_INFORMATION", value = 0)
@GtfsEnumValue(name = "SUPPORTED", value = 1)
@GtfsEnumValue(name = "NOT_SUPPORTED", value = 2)
public interface GtfsContactlessEmvSupportEnum {}
