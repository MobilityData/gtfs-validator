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

import java.util.ArrayList;
import java.util.List;

/**
 * Use case to create list of file on which the GTFS semantic validation process will be applied to
 */
public class CreateGtfsSemanticValidationFilenameList {
    private final List<String> filenameCollection;

    public CreateGtfsSemanticValidationFilenameList(final List<String> filenameCollection) {
        this.filenameCollection = filenameCollection;
    }

    /**
     * Use case execution method: returns the list of filename on which the GTFS semantic validation will be applied
     *
     * @param toExcludeFromGtfsSemanticValidation the list of files to exclude from GTFS semantic validation
     * @return list of filename on which the GTFS semantic validation will be applied
     */
    public List<String> execute(final List<String> toExcludeFromGtfsSemanticValidation) {
        final List<String> toReturn = new ArrayList<>();
        filenameCollection.forEach(filename -> {
            if (!toExcludeFromGtfsSemanticValidation.contains(filename)) {
                toReturn.add(filename);
            }
        });
        return toReturn;
    }
}