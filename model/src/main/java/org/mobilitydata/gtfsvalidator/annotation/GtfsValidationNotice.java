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
   * Whether the notice is deprecated. Deprecated notices are still supported in the documentation
   * but are not actively used in validation.
   */
  boolean deprecated() default false;

  /**
   * Reason for the deprecation of the notice. This field is only used if {@link #deprecated()} is
   * true.
   */
  String deprecationReason() default "";

  /**
   * Version on which the notice was deprecated. This field is only used if {@link #deprecated()} is
   * true.
   */
  String deprecationVersion() default "";

  /**
   * Replacement notice class for the deprecated notice. This field is only used if {@link
   * #deprecated()} is true and the notice has a replacement.
   */
  Class<?> replacementNotice() default Void.class;

  /**
   * GTFS specification section references. For specific file references, use {@link #files()}
   * instead.
   */
  SectionRefs sections() default @SectionRefs();

  /**
   * GTFS specification file references used in automatic documentation generation for the notice.
   */
  FileRefs files() default @FileRefs({});

  /**
   * GTFS Best Practices file references used in automatic documentation generation for the notice.
   */
  FileRefs bestPractices() default @FileRefs({});

  /**
   * Arbitrary documentation reference urls used in automatic documentation generation for the
   * notice.
   */
  UrlRef[] urls() default {};

  /**
   * Annotation used in notice documentation to specify a link to a general section in the GTFS
   * reference documentation, as identified by the section id.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface SectionRefs {
    /** Each value represents a section id string (e.g. `field-types`) in the reference docs. */
    SectionRef[] value() default {};
  }

  enum SectionRef {
    DATASET_FILES("dataset-files"),
    FILE_REQUIREMENTS("file-requirements"),
    FILED_TYPES("field-types"),
    FIELD_DEFINITIONS("field-definitions"),
    TERM_DEFINITIONS("term-definitions"),
    BEST_PRACTICES_DATASET_PUBLISHING("dataset-publishing-general-practices");

    // The HTML section id.
    private final String id;

    SectionRef(String id) {
      this.id = id;
    }

    public String id() {
      return this.id;
    }
  }

  /**
   * Annotation used in notice documentation to specify a link to the reference documentation for a
   * specific GTFS file, as identified by its table schema.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface FileRefs {
    /** The set of GTFS table schemas corresponding to the GTFS file in question. */
    Class<? extends GtfsEntity>[] value();
  }

  /**
   * Annotation used in notice documentation to specify a general reference URL for a notice. For
   * links to specific GTFS file references and best-practices, use {@link #files()} or {@link
   * #bestPractices()}.
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
