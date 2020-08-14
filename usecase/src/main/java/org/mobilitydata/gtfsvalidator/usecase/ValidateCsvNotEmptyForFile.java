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
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EmptyFileErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EmptyFileWarningNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;


/**
 * Use case to validate a csv file is not empty.
 * - the file is completely empty: that's an error
 * - the file defines headers but no row of data
 * - required file: that's an error
 * - optional file: that's a warning
 */
public class ValidateCsvNotEmptyForFile {

    private final RawFileInfo rawFileInfo;
    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param rawFileInfo an object containing information regarding a file location and expected content
     * @param rawFileRepo a repository storing information about a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
     */
    public ValidateCsvNotEmptyForFile(final RawFileInfo rawFileInfo,
                                      final GtfsSpecRepository specRepo,
                                      final RawFileRepository rawFileRepo,
                                      final ValidationResultRepository resultRepo,
                                      final Logger logger
    ) {
        this.rawFileInfo = rawFileInfo;
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: for a file, checks if it empty
     * If no file content at all is available or it contains headers but no row of data and is required,
     * a {@link EmptyFileErrorNotice} is added to the {@link ValidationResultRepository} provided in the constructor.
     * If a file contains headers and is optional but no rows of data,
     * a {@link EmptyFileWarningNotice} is added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E_039 & W_009 - Empty file'" + System.lineSeparator());
        if (rawFileRepo.getActualHeadersForFile(rawFileInfo).size() == 0) {
            resultRepo.addNotice(new EmptyFileErrorNotice(rawFileInfo.getFilename()));
        } else {
            rawFileRepo.getProviderForFile(rawFileInfo).ifPresent(
                    provider -> {
                        if (!provider.hasNext()) {
                            if (specRepo.getRequiredFilenameList().contains(rawFileInfo.getFilename())) {
                                resultRepo.addNotice(new EmptyFileErrorNotice(rawFileInfo.getFilename()));
                            } else {
                                resultRepo.addNotice(new EmptyFileWarningNotice(rawFileInfo.getFilename()));
                            }
                        }
                    }
            );
        }
    }
}