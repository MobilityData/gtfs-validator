package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "ZERO", value = 0)
@GtfsEnumValue(name = "ONE", value = 1)
@GtfsEnumValue(name = "MAX_SHORT", value = Short.MAX_VALUE)
@GtfsEnumValue(name = "MIN_SHORT", value = -Short.MAX_VALUE)
public interface ShortGtfsEnum {}
