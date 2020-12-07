package org.mobilitydata.gtfsvalidator.cli;

import org.apache.logging.log4j.Logger;

/**
 * Provides convenient method to validate the requirement on CLI parameters
 */
public class CliParametersAnalyzer {
    private Logger logger;

    CliParametersAnalyzer(Logger logger) {
        this.logger = logger;
    }

    /**
     * Check validity of CLI parameter combination
     *
     * @return true if CLI parameter combination is legal, otherwise return false
     */
    public boolean isValid(Arguments args) {
        if (args.getInput() == null && args.getUrl() == null) {
            logger.error("One of the two following CLI parameter must be provided: '--input' and '--url'");
            return false;
        }
        if (args.getInput() != null && args.getUrl() != null) {
            logger.error("The two following CLI parameters cannot be provided at the same time:" +
                    " '--input' and '--url'");
            return false;
        }
        if (args.getUrl() != null && args.getStorageDirectory() == null) {
            logger.error("CLI parameter '--storage_directory' must be provided if '--url' is provided");
            return false;
        }
        if (args.getStorageDirectory() != null && args.getUrl() == null) {
            logger.error("CLI parameter '--storage_directory' must not be provided if '--url' is not provided");
            return false;
        }
        return true;
    }
}
