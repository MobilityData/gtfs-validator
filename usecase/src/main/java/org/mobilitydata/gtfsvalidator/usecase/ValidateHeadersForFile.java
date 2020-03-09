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
import org.mobilitydata.gtfsvalidator.usecase.notice.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;
import java.util.List;

/**
 * Use case to validate the headers of a csv file.
 */
public class ValidateHeadersForFile {

    private final GtfsSpecRepository specRepo;
    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param specRepo    an instance of {@link GtfsSpecRepository} storing information about the GTFS specification
     *                    used
     * @param rawFileInfo an instance of {@link RawFileInfo}
     * @param rawFileRepo an instance of {@link RawFileRepository}
     * @param resultRepo  an instance of {@link ValidationResultRepository} storing information about the validation
     *                    process
     */
    public ValidateHeadersForFile(final GtfsSpecRepository specRepo,
                                  final RawFileInfo rawFileInfo,
                                  final RawFileRepository rawFileRepo,
                                  final ValidationResultRepository resultRepo
    ) {
        this.specRepo = specRepo;
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method.
     * A {@link MissingHeaderNotice} is emitted each time a header marked as "required" is missing.
     * A {@link NonStandardHeaderNotice} is emitted for each header not marked as "required".
     */
    public void execute() {
        List<String> expectedRequiredHeaderList = specRepo.getRequiredHeadersForFile(rawFileInfo);
        List<String> expectedOptionalHeaderList = specRepo.getOptionalHeadersForFile(rawFileInfo);
        Collection<String> actualHeaderList = rawFileRepo.getActualHeadersForFile(rawFileInfo);

        //Missing headers
        expectedRequiredHeaderList.stream()
                .filter(expectedHeader -> !(actualHeaderList.contains(expectedHeader)))
                .forEach(missingHeader -> resultRepo.addNotice(new MissingHeaderNotice(rawFileInfo.getFilename(),
                        missingHeader)));

        //Extra headers
        actualHeaderList.stream()
                .filter(header ->
                        !expectedOptionalHeaderList.contains(header) && !expectedRequiredHeaderList.contains(header))
                .forEach(extraHeader ->
                        resultRepo.addNotice(new NonStandardHeaderNotice(rawFileInfo.getFilename(), extraHeader)));

    }
}