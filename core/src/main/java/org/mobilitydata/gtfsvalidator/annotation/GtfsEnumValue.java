package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(GtfsEnumValues.class)
public @interface GtfsEnumValue {
    String name();

    int value();
}
