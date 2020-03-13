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

import org.mobilitydata.gtfsvalidator.usecase.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

public class ValidateAllRequiredFilePresence {

    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    public ValidateAllRequiredFilePresence(final GtfsSpecRepository specRepo,
                                           final RawFileRepository rawFileRepo,
                                           final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    public List<String> execute() {
        if (!rawFileRepo.getFilenameAll().containsAll(specRepo.getRequiredFilenameList())) {

            specRepo.getRequiredFilenameList().stream()
                    .filter(requiredFile -> !rawFileRepo.getFilenameAll().contains(requiredFile))
                    .forEach(missingFile -> resultRepo.addNotice(new MissingRequiredFileNotice(missingFile)));
        }
        return specRepo.getRequiredFilenameList();
    }
}