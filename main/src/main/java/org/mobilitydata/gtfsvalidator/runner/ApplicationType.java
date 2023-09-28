package org.mobilitydata.gtfsvalidator.runner;

/** Specifies the execution environment for the application. */
public enum ApplicationType {
  /**
   * Command-line execution of validator, as either JAR or embedded in a container (e.g. Docker).
   */
  CLI,
  /** Desktop-based application. */
  DESKTOP,
  /** Web-based application. */
  WEB
}
