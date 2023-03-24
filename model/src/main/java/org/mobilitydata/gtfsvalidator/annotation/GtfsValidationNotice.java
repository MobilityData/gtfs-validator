package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

/**
 * An annotation used to identify, configure, and document a validation notice. All notices should
 * be annotated with this type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GtfsValidationNotice {

  /**
   * The default severity level for the notice. The actual severity level may be configured to be
   * different at validation time.
   */
  SeverityLevel severity();

  /**
   * GTFS specification file references used in automatic documentation generation for the notice.
   */
  FileRefs files() default @FileRefs({});

  /**
   * GTFS Best Practices file references used in automatic documentation generation for the notice.
   */
  BestPracticesRefs bestPractices() default @BestPracticesRefs({});

  /**
   * Arbitrary documentation reference urls used in automatic documentation generation for the
   * notice.
   */
  UrlRef[] urls() default {};

  /**
   * Annotation used in notice documentation to specify a link to the specification documentation
   * for a specific GTFS file, as identified by its table schema.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface BestPracticesRefs {
    /** The set of GTFS table schemas corresponding to the GTFS file in question. */
    Class<? extends GtfsEntity>[] value();
  }

  /**
   * Annotation used in notice documentation to specify a link to the Best Practices documentation
   * for a specific GTFS file, as identified by its table schema.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface FileRefs {
    /** The set of GTFS table schemas corresponding to the GTFS file in question. */
    Class<? extends GtfsEntity>[] value();

    /** True if a particular notice applies to all files in the GTFS spec. */
    boolean allFiles() default false;
  }

  /**
   * Annotation used in notice documentation to specify a general reference URL for a notice. For
   * links to specific GTFS file references and best-practices, use {@link FileRefs} or {@link
   * BestPracticesRefs} instead.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface UrlRef {

    /** The human-readable text used to display the url. */
    String label();

    /** The reference URL. */
    String url();
  }
}
