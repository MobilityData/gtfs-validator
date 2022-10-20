package org.mobilitydata.gtfsvalidator.validator;

/**
 * This is a no-op class that allows for static references to other validators as a form of
 * dependency documentation.
 */
public final class ValidatorReference {
  private ValidatorReference() {}

  /**
   * A no-op method that allows one to statically document that a particular validation condition is
   * handled by another validator.
   */
  public static final void validatedElsewhereBy(Class<?>... validator) {}
}
