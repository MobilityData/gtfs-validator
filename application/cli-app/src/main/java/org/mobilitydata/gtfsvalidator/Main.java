/*
 * Copyright (c) 2019. MobilityData IO.
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

package org.mobilitydata.gtfsvalidator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.config.DefaultConfig;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.ParseSingleRowForFile;
import org.mobilitydata.gtfsvalidator.usecase.ProcessParsedAgency;
import org.mobilitydata.gtfsvalidator.usecase.ProcessParsedRoute;
import org.mobilitydata.gtfsvalidator.usecase.ValidateGtfsTypes;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        final long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();
        final DefaultConfig config = new DefaultConfig(logger);

        try {
            config.parseAllExecutionParameter().execute(args);

            // use case will inspect parameters and decide if help menu should be displayed or not
            if (!config.printHelp().execute()) {

                // use case will inspect parameters and display relevant information about the validation execution
                // process
                config.logExecutionInfo().execute();

                // use case will inspect parameters and decide if GTFS dataset should be downloaded or not
                config.downloadArchiveFromNetwork().execute();

                config.unzipInputArchive(
                        config.cleanOrCreatePath().execute(ExecParamRepository.EXTRACT_KEY))
                        .execute();

                final List<String> filenameList = config.validateAllRequiredFilePresence().execute();

                filenameList.addAll(config.validateAllOptionalFileName().execute());

                final List<String> toLoadIntoMemory = new ArrayList<>();
                // at present this list is hard coded for our needs: agency.txt and routes.txt.
                toLoadIntoMemory.add("agency.txt");
                toLoadIntoMemory.add("routes.txt");

                // retrieve use case to be used multiple times
                final ValidateGtfsTypes validateGtfsTypes = config.validateGtfsTypes();
                final ProcessParsedAgency processParsedAgency = config.processParsedAgency();
                final ProcessParsedRoute processParsedRoute = config.processParsedRoute();

                // base validation + build gtfs entities
                filenameList.forEach(filename -> {
                    config.validateHeadersForFile(filename).execute();
                    config.validateAllRowLengthForFile(filename).execute();

                    final ParseSingleRowForFile parseSingleRowForFile = config.parseSingleRowForFile(filename);
                    while (parseSingleRowForFile.hasNext()) {
                        final ParsedEntity parsedEntity = parseSingleRowForFile.execute();
                        validateGtfsTypes.execute(parsedEntity);

                        // load gtfs entities into memory
                        // in the future all filename in filenameList will be processed. For now focusing on routes.txt
                        // and agency.txt.
                        // filenames in filenameList will be determined using a dependency tree defined by a JSON file,
                        // and command lines or configuration file will be used to exclude files from the validation
                        // process.
                        if (toLoadIntoMemory.contains(filename)) {
                            switch (parsedEntity.getRawFileInfo().getFilename()) {
                                case "agency.txt": {
                                    processParsedAgency.execute(parsedEntity);
                                    break;
                                }
                                case "routes.txt": {
                                    processParsedRoute.execute(parsedEntity);
                                    break;
                                }
                            }
                        }
                    }
                });

                config.cleanOrCreatePath().execute(ExecParamRepository.OUTPUT_KEY);

                config.exportResultAsFile().execute();
            }

        } catch (IOException e) {
            if (e.getMessage().contains("execution-parameters.json")) {
                config.printHelp().execute();
            } else {
                logger.error("An exception occurred: " + e);
            }
        }
        logger.info("Took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
    }
}
