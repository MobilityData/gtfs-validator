package org.mobilitydata.gtfsvalidator.cli;

import java.io.IOException;

/**
 * Provides convenient method to validate the requirement on CLI parameters
 */
public class ValidateCliParameters {
    private Arguments args;

    ValidateCliParameters(Arguments args) {
        this.args = args;
    }

    /**
     * Validate requirement on CLI parameters
     * @throws IOException
     */
    public void validate() throws IOException {
        if (args.input == null && args.url == null) {
            throw new IOException("One of the two following CLI parameter must be provided: '--input' and '--url'");
        }
        if (args.input != null && args.url != null) {
            throw new IOException("The two following CLI parameters cannot be provided at the same time:" +
                    " '--input' and '--url'");
        }
    }
}
