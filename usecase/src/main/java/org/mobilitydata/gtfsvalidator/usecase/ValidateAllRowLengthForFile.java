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

import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate the length of rows for a single csv file. It ensures compliance of the length of a row with
 * the expected number of headers for a csv file. This use case is triggered after the validation of the
 * presence of all required headers in csv files.
 */
public class ValidateAllRowLengthForFile {

    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param rawFileInfo an object containing information regarding a file location and expected content
     * @param rawFileRepo a repository storing information about a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
     */
    public ValidateAllRowLengthForFile(final RawFileInfo rawFileInfo,
                                       final RawFileRepository rawFileRepo,
                                       final ValidationResultRepository resultRepo) {
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method: validates the length of all rows of the file linked to the {@link RawFileInfo}.
     * For each row of a GTFS CSV file, a {@link RawEntity} is created with a 1 based index identifying the row location
     * within a GTFS CSV file and its content as a map of strings.
     */
    public void execute() {
        rawFileRepo.getProviderForFile(rawFileInfo).ifPresent(
                provider -> {
                    while (provider.hasNext()) {
                        RawEntity rawEntity = provider.getNext();
                        if (rawEntity.size() != provider.getHeaderCount()) {
                            resultRepo.addNotice(new InvalidRowLengthNotice(
                                    rawFileInfo.getFilename(),
                                    rawEntity.getIndex(),
                                    provider.getHeaderCount(),
                                    rawEntity.size())
                            );
                        }
                    }
                }
        );
    }
}
