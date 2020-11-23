package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates both custom and automatically generated validators to make them discoverable on the fly.
 * <p>
 * Example.
 *
 * <pre>
 *   @GtfsValidator
 *   public class AgencyConsistencyValidator extends FileValidator {
 *       @Inject
 *       GtfsAgencyTableContainer agencyTable;
 *
 *       @Override
 *       public void validate(NoticeContainer noticeContainer) {
 *       }
 *   }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GtfsValidator {
}
