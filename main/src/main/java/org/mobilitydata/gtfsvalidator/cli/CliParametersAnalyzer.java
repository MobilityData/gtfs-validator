/*
 * Original work Copyright (C) 2020 Google LLC
 * Modified work Copyright (C) 2020 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
