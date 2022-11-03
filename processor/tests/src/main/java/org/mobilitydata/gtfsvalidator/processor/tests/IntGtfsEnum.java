package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "ZERO", value = 0)
@GtfsEnumValue(name = "ONE", value = 1)
@GtfsEnumValue(name = "MAX_INT", value = Integer.MAX_VALUE)
// MIN_INT is set to (MIN_VALUE + 1) since Integer.MIN_VALUE will be a special UNRECOGNIZED
// constant.
@GtfsEnumValue(name = "MIN_INT", value = Integer.MIN_VALUE + 1)
public interface IntGtfsEnum {}
