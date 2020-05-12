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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.InvalidLangNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Locale;
import java.util.Map;


/**
 * Use case to validate the headers of a csv file. This checks that headers marked as required by the GTFS specification
 * are present in the expected files. This use case is triggered after the validation of required files.
 */
public class ValidateAgencyLanguage {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param dataRepo   a repository storing information about the GTFS data
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateAgencyLanguage(final GtfsDataRepository dataRepo,
                                  final ValidationResultRepository resultRepo
    ) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Use case execution method: for a file, checks the presence of all headers marked as "required" in the
     * GTFS specification. A {@link MissingHeaderNotice} is generated each time a required header is missing.
     * A {@link NonStandardHeaderNotice} is generated for each header not marked as "required". These notices are
     * then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        Map<String, Agency> feedAgencies = dataRepo.getAgencies();
        Locale locale = new Locale("en");

        //Missing headers
        feedAgencies.values().stream()
                .filter(agency -> !agency.getAgencyLang().equals("") &&
                        !(locale.getISO3Language().contains(agency.getAgencyLang())))
                .forEach(agency -> resultRepo.addNotice(new InvalidLangNotice("agency.txt", agency.getAgencyLang())));

    }

}

