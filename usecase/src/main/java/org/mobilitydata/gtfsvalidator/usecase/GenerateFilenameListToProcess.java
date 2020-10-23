/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Use case to generate the list of filename to validate
 */
public class GenerateFilenameListToProcess {
    public static final String FEED_INFO_TXT = "feed_info.txt";
    public static final String AGENCY_TXT = "agency.txt";
    private final Logger logger;

    public GenerateFilenameListToProcess(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Use case execution method. Returns the list of filename to validate from a list of filename to exclude and the
     * list of required and optional files present in the GTFS archive
     *
     * @param toExclude the list of filename to exclude from the validation process
     * @param toProcess the list of files (required and optional) contained in the GTFS archive
     * @return the list of filename to validate. `feed_info.txt` is placed in front if present,
     * otherwise `agency.txt` if present
     */
    public ArrayList<String> execute(final ArrayList<String> toExclude, final ArrayList<String> toProcess) {
        logger.info("List of filenames to exclude is: " + toExclude);
        toProcess.removeAll(toExclude);

        // We need to place those files in front to have the best chance of having a significant validation report name
        if (toProcess.contains(FEED_INFO_TXT)) {
            Collections.swap(toProcess, 0, toProcess.indexOf(FEED_INFO_TXT));
        } else if (toProcess.contains(AGENCY_TXT)) {
            Collections.swap(toProcess, 0, toProcess.indexOf(AGENCY_TXT));
        }

        logger.info("Will execute validation on the following subset of files: " + toProcess);
        return toProcess;
    }
}