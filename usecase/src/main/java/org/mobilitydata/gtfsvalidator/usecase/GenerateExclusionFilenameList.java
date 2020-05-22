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
import org.mobilitydata.gtfsvalidator.domain.entity.GtfsSchemaTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Use case to create list of filename on which the GTFS semantic validation process should not be applied to
 */
public class GenerateExclusionFilenameList {
    private final String gtfsSchemaAsString;
    private final ObjectReader objectReader;

    public GenerateExclusionFilenameList(final String gtfsSchemaAsString, final ObjectReader objectReader) {
        this.gtfsSchemaAsString = gtfsSchemaAsString;
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
            final GtfsSchemaTree gtfsSchemaTree = objectReader.readValue(gtfsSchemaAsString);
            final Set<String> toReturn = new HashSet<>();
            for (String filename : toExcludeFromGtfsSemanticValidation) {
                toReturn.addAll(gtfsSchemaTree.getChildWithName(filename).DFS(new HashSet<>()));
            }
            return new ArrayList<>(toReturn);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Wrong list of file to exclude from validation process, please check " +
                    "spelling. You might have forgotten the extension .txt at the end of some filename");
        }
    }
}