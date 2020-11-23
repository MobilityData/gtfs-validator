package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects a table container to a validator.
 * <p>
 * This annotation should be placed on a public or a package-visible member field.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsValidator
 *   public class AgencyConsistencyValidator extends FileValidator {
 *       @Inject
 *       GtfsAgencyTableContainer agencyTable;
 *   }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
