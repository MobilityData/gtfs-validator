package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "ZERO", value = 0)
@GtfsEnumValue(name = "ONE", value = 1)
@GtfsEnumValue(name = "MAX_BYTE", value = 127)
@GtfsEnumValue(name = "MIN_BYTE", value = -128)
public class ByteGtfsEnum {}
