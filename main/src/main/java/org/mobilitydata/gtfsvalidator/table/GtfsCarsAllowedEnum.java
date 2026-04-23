package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

/**
 * Enum representing the cars_allowed field values.
 *
 * <p>0 or empty: No information available about car access for trips associated with this agency or
 * route. 1: Cars are allowed for trips associated with this agency or route. 2: Cars are not
 * allowed for trips associated with this agency or route.
 */
@GtfsEnumValue(name = "NO_INFORMATION", value = 0)
@GtfsEnumValue(name = "ALLOWED", value = 1)
@GtfsEnumValue(name = "NOT_ALLOWED", value = 2)
public interface GtfsCarsAllowedEnum {}
