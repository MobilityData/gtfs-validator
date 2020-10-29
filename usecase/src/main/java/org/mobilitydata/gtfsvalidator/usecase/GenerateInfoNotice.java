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

import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.ValidationProcessInfoNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Set;

public class GenerateInfoNotice {
    final static String GTFS_VALIDATOR_VERSION = "TODO";
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final GtfsDataRepository gtfsDataRepo;
    private final Timestamp timestamp;
    private final long processingTimeSecs;
    private final Set<String> processedFilenameCollection;
    private final CustomFileUtils customFileUtils;
    private final Path inputPath;
    private final Path extractPath;


    public GenerateInfoNotice(final ValidationResultRepository resultRepo,
                              final ExecParamRepository execParamRepo,
                              final GtfsDataRepository gtfsDataRepo,
                              final Timestamp timestamp,
                              final long processingTimeSecs,
                              final Set<String> processedFilenameCollection,
                              final CustomFileUtils customFileUtils,
                              final Path inputPath,
                              final Path extractPath) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.gtfsDataRepo = gtfsDataRepo;
        this.timestamp = timestamp;
        this.processingTimeSecs = processingTimeSecs;
        this.processedFilenameCollection = processedFilenameCollection;
        this.customFileUtils = customFileUtils;
        this.inputPath = inputPath;
        this.extractPath = extractPath;
    }

    public void execute() {
        String feedPublisherNameOrAgencyName = gtfsDataRepo.getFeedPublisherName();

        if (feedPublisherNameOrAgencyName.isEmpty() || feedPublisherNameOrAgencyName.isBlank()) {
            if (gtfsDataRepo.getAgencyCount() > 0) {
                feedPublisherNameOrAgencyName = gtfsDataRepo.getAgencyAll().values().iterator().next().getAgencyName();
            } else {
                feedPublisherNameOrAgencyName = "no agency or feed publisher found";
            }
        }

        final String urlOrPathToGtfsArchive = execParamRepo.hasExecParamValue(ExecParamRepository.URL_KEY) ?
                execParamRepo.getExecParamValue(ExecParamRepository.URL_KEY) :
                execParamRepo.getExecParamValue(ExecParamRepository.INPUT_KEY);

        resultRepo.addNotice(
                new ValidationProcessInfoNotice(
                        feedPublisherNameOrAgencyName,
                        timestamp.toString(),
                        resultRepo.getWarningNoticeCount(),
                        resultRepo.getErrorNoticeCount(),
                        urlOrPathToGtfsArchive,
                        customFileUtils.sizeOf(inputPath),
                        customFileUtils.sizeOfDirectory(extractPath),
                        GTFS_VALIDATOR_VERSION,
                        processedFilenameCollection.toString(),
                        processingTimeSecs)
        );
    }
}
