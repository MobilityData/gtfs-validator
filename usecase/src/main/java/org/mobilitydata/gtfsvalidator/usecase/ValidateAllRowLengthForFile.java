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
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate the number of rows for a single csv file.
 */
public class ValidateAllRowLengthForFile {

    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param rawFileInfo an instance of {@link RawFileInfo}
     * @param rawFileRepo an instance of {@link RawFileRepository}
     * @param resultRepo  an instance of {@link ValidationResultRepository} storing information about the validation
     *                    process
     */
    public ValidateAllRowLengthForFile(final RawFileInfo rawFileInfo,
                                       final RawFileRepository rawFileRepo,
                                       final ValidationResultRepository resultRepo) {
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method. Uses a @{link }
     */
    public void execute() {
        rawFileRepo.getProviderForFile(rawFileInfo).ifPresentOrElse(
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
                },
                () -> resultRepo.addNotice(new CannotConstructDataProviderNotice(rawFileInfo.getFilename()))
        );
    }
}
