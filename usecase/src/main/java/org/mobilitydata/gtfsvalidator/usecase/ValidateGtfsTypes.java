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

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate GTFS types from a {@code ParsedEntity}. This use case is called on each parsed row of a csv
 * file. It ensures that each value of a parsed row from a GTFS file has the type that is expected in the GTFS
 * specification.
 */
public class ValidateGtfsTypes {

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param specRepo   a repository storing information about the GTFS specification used
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateGtfsTypes(final GtfsSpecRepository specRepo,
                             final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method: applies the type validation requirement provided by the
     * {@code ParsedEntityTypeValidator} on the {@link ParsedEntity} provided as parameter.
     *
     * @param toValidate a parsed row from a GTFS file
     */
    public void execute(final ParsedEntity toValidate) {
        specRepo.getValidatorForFile(toValidate.getRawFileInfo()).validate(toValidate).forEach(resultRepo::addNotice);
    }
}
