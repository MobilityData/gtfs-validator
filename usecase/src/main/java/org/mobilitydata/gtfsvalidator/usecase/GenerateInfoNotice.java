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

import org.apache.commons.io.FileUtils;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.ValidationProcessInfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.sql.Timestamp;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GenerateInfoNotice {
    final static String GTFS_VALIDATOR_VERSION = "v1.3.0-SNAPSHOT";
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final GtfsDataRepository gtfsDataRepo;
    private final Timestamp timestamp;
    private final long startTime;
    private final Set<String> processedFilenameCollection;
    private final File zippedGtfsArchive;
    private final File unzippedGtfsArchive;

    public GenerateInfoNotice(final ValidationResultRepository resultRepo,
                              final ExecParamRepository execParamRepo,
                              final GtfsDataRepository gtfsDataRepo,
                              final Timestamp timestamp,
                              final long startTime,
                              final Set<String> processedFilenameCollection,
                              final File zippedGtfsArchive,
                              final File unzippedGtfsArchive) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.gtfsDataRepo = gtfsDataRepo;
        this.timestamp = timestamp;
        this.startTime = startTime;
        this.processedFilenameCollection = processedFilenameCollection;
        this.zippedGtfsArchive = zippedGtfsArchive;
        this.unzippedGtfsArchive = unzippedGtfsArchive;
    }

    public void execute() {
        String reportName = gtfsDataRepo.getFeedPublisherName();

        if ((reportName.isEmpty() || reportName.isBlank()) && gtfsDataRepo.getAgencyCount() > 0) {
            reportName = gtfsDataRepo.getAgencyAll().values().iterator().next().getAgencyName();
        }

        final String pathToUnzippedArchive = execParamRepo.getExecParamValue(ExecParamRepository.EXTRACT_KEY);
        final String pathToRawZip = execParamRepo.getExecParamValue(ExecParamRepository.INPUT_KEY);
        final long processingTimeSecs =  TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
        final String urlOrPathToGtfsArchive = execParamRepo.hasExecParamValue(ExecParamRepository.URL_KEY)?
                execParamRepo.getExecParamValue(ExecParamRepository.URL_KEY):
                execParamRepo.getExecParamValue(ExecParamRepository.INPUT_KEY);

        System.out.println(urlOrPathToGtfsArchive);
        System.out.println(pathToRawZip);
        System.out.println(pathToUnzippedArchive);
        resultRepo.addNotice(
                new ValidationProcessInfoNotice(
                        reportName,
                        timestamp.toString(),
                        resultRepo.getWarningNoticeCount(),
                        resultRepo.getErrorNoticeCount(),
                        urlOrPathToGtfsArchive,
                        FileUtils.sizeOf(zippedGtfsArchive),
                        FileUtils.sizeOfDirectory(unzippedGtfsArchive),
                        GTFS_VALIDATOR_VERSION,
                        processedFilenameCollection.toString(),
                        processingTimeSecs)
        );
    }
}
