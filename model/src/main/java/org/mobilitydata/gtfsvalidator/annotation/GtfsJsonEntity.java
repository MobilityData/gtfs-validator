package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates a field that defines entities for a GTFS JSON schema. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GtfsJsonEntity {
  String value();

  Class<?> clazz();
}
