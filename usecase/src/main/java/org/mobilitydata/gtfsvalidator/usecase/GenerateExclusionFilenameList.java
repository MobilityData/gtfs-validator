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

import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.util.*;

/**
 * Use case to create list of filename on which the GTFS semantic validation process should not be applied to
 */
public class GenerateExclusionFilenameList {
    private final String parameterJsonString;
    private final ObjectReader objectReader;

    public GenerateExclusionFilenameList(final String parameterJsonString, final ObjectReader objectReader) {
        this.parameterJsonString = parameterJsonString;
        this.objectReader = objectReader;
    }

    /**
     * Use case execution method: returns the list of filename on which the GTFS semantic validation should not be
     * applied to.
     *
     * @param toExcludeFromGtfsSemanticValidation the list of files to exclude from GTFS semantic validation. This list
     *                                            is provided by user via command line options or configuration file.
     */
    public List<String> execute(final List<String> toExcludeFromGtfsSemanticValidation) throws IOException {
        try {
            final Set<String> toReturn = new HashSet<>();
            for (String filename : toExcludeFromGtfsSemanticValidation) {
                toReturn.add(filename);
                final Iterator<String> stringIterator = objectReader.readTree(parameterJsonString)
                        .findValue(filename)
                        .fieldNames();
                while (stringIterator.hasNext()) {
                    toReturn.addAll(execute(List.of(stringIterator.next())));
                }
            }
            return new ArrayList<>(toReturn);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Wrong list of file to exclude from validation process, please check " +
                    "spelling. You might have forgotten the extension .txt at the end of some filename");
        }
    }
}