package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to be used on notice constructor. This specifies the constructor to be considered
 * while exporting notice information.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NoticeExport {}
