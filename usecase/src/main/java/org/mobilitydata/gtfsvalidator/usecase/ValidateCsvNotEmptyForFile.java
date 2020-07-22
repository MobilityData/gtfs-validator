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

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;


/**
 * Use case to validate a csv file is not empty.
 */
public class ValidateCsvNotEmptyForFile {

    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param rawFileInfo an object containing information regarding a file location and expected content
     * @param rawFileRepo a repository storing information about a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
     */
    public ValidateCsvNotEmptyForFile(final RawFileInfo rawFileInfo,
                                      final RawFileRepository rawFileRepo,
                                      final ValidationResultRepository resultRepo
    ) {
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method: for a file, checks if it defines any row.
     * If not, a {@link EmptyFileNotice} is generated and added to the {@link ValidationResultRepository} provided
     * in the constructor.
     */
    public void execute() {
        rawFileRepo.getProviderForFile(rawFileInfo).ifPresentOrElse(
                provider -> {
                    if (!provider.hasNext()) {
                        resultRepo.addNotice(new EmptyFileNotice(rawFileInfo.getFilename()));
                    }
                },
                () -> resultRepo.addNotice(new CannotConstructDataProviderNotice(rawFileInfo.getFilename()))
        );
    }
}