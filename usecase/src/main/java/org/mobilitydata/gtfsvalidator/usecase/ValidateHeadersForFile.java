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
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;
import java.util.List;

/**
 * Use case to validate the headers of a csv file. This checks that headers marked as required by the GTFS specification
 * are present in the expected files. This use case is triggered after the validation of required files.
 */
public class ValidateHeadersForFile {

    private final GtfsSpecRepository specRepo;
    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param specRepo    a repository storing information about the GTFS specification used
     * @param rawFileInfo an object containing information regarding a file location and expected content
     * @param rawFileRepo a repository storing information about a GTFS dataset
     * @param resultRepo  a repository storing information about the validation process
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
     * Use case execution method: for a file, checks the presence of all headers marked as "required" in the
     * GTFS specification. A {@link MissingHeaderNotice} is generated each time a required header is missing.
     * A {@link NonStandardHeaderNotice} is generated for each header not marked as "required". These notices are
     * then added to the {@link ValidationResultRepository} provided in the constructor.
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