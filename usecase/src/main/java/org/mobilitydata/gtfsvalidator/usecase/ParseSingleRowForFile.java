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
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.MalformedCsvRowException;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to parse a single row of a csv file. This use case is triggered after the validation of the length of all
 * rows for a specific file.
 */
public class ParseSingleRowForFile {

    private final ValidationResultRepository resultRepo;
    private RawFileRepository.RawEntityProvider provider;
    private GtfsSpecRepository.RawEntityParser parser;

    /**
     * @param rawFileInfo an object containing information regarding a file location and expected content
     * @param rawFileRepo a repository storing information about a GTFS dataset
     * @param specRepo    a repository storing information about the GTFS specification used
     * @param resultRepo  a repository storing information about the validation process
     */
    public ParseSingleRowForFile(final RawFileInfo rawFileInfo,
                                 final RawFileRepository rawFileRepo,
                                 final GtfsSpecRepository specRepo,
                                 final ValidationResultRepository resultRepo) {
        this.resultRepo = resultRepo;

        rawFileRepo.getProviderForFile(rawFileInfo).ifPresent(
                provider -> {
                    this.parser = specRepo.getParserForFile(rawFileInfo);
                    this.provider = provider;
                }
        );
    }

    /**
     * Returns true if a row has a next row, else false
     *
     * @return true if a row has a next row, else false
     */
    public boolean hasNext() {
        return provider != null && provider.hasNext();
    }

    /**
     * Use case execution method returns a parsed row from a GTFS CSV file. While the processed file has rows, a
     * {@link RawEntity} is created with 1 based index identifying the row location within a GTFS CSV file and its
     * content as a map of strings; which allows validation of numeric types.
     *
     * @return a parsed row from a GTFS file
     */
    public ParsedEntity execute() {
        if (!hasNext()) {
            return null;
        }
        final RawEntity rawEntity = provider.getNext();
        parser.validateNonStringTypes(rawEntity).forEach(resultRepo::addNotice);
        try {
            return parser.parse(rawEntity);
        } catch (MalformedCsvRowException e) {
            return null;
        }
    }
}
