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

/**
 * Use case to generate the list of filename to validate
 */
public class GenerateFilenameListToProcess {
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
     * @return the list of filename to validate
     */
    public ArrayList<String> execute(final ArrayList<String> toExclude, final ArrayList<String> toProcess) {
        logger.info("List of filenames to exclude is: " + toExclude + System.lineSeparator());
        toProcess.removeAll(toExclude);
        logger.info("Will execute validation on the following subset of files: " + toProcess + System.lineSeparator());
        return toProcess;
    }
}