package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the first part of a composite key in tables like stop_times.txt (trip_id).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FirstKey {
}
