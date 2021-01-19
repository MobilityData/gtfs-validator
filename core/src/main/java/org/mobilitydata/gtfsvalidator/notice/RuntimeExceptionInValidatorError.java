package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * Describes a runtime exception during validation. This normally indicates a
 * bug in validator code, e.g., in a custom validator class.
 */
public class RuntimeExceptionInValidatorError extends SystemError {
    public RuntimeExceptionInValidatorError(String validatorClassName,
                                            String exceptionClassName,
                                            String message) {
        super(ImmutableMap.of("validator", validatorClassName, "exception",
                              exceptionClassName, "message", message));
    }

    @Override
    public String getCode() {
        return "runtime_exception_in_validator";
    }
}
