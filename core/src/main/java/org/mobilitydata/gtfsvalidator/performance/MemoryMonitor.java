package org.mobilitydata.gtfsvalidator.performance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to monitor memory usage of a method. The annotated method should return a {@link
 * MemoryUsage} object. The key is used to group memory usage of different methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemoryMonitor {
  String key() default "";
}
