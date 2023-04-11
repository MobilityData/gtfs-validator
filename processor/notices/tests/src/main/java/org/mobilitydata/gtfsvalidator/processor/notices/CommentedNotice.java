package org.mobilitydata.gtfsvalidator.processor.notices;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/** This is the notice comment. */
@GtfsValidationNotice(severity = WARNING)
public class CommentedNotice {

  /** This is the fieldA comment. */
  private String fieldA;

  /** This is the fieldB comment. */
  private String fieldB;
}
