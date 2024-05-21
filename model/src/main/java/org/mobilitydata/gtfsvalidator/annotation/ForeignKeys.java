package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies multiple references to foreign keys. A validator for data integrity will be generated
 * automatically for each foreign key.
 *
 * <p>Note that {@code @ForeignKeys} does not imply that the field is required and you need to put
 * an extra {@code @Required} annotation in this case.
 *
 * <p>Example.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ForeignKeys {
  ForeignKey[] value();
}
