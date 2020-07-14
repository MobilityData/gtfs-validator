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
import org.mobilitydata.gtfsvalidator.usecase.*;
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

                final ArrayList<String> filenameListToExclude = config.generateExclusionFilenameList().execute();

                final ArrayList<String> datasetFilenameList = config.validateAllRequiredFilePresence().execute();
                datasetFilenameList.addAll(config.validateAllOptionalFileName().execute());

                final List<String> filenameListToProcess =
                        config.generateFilenameListToProcess().execute(filenameListToExclude, datasetFilenameList);

                // retrieve use case to be used multiple times
                final ValidateGtfsTypes validateGtfsTypes = config.validateGtfsTypes();
                final ProcessParsedAgency processParsedAgency = config.processParsedAgency();
                final ProcessParsedRoute processParsedRoute = config.processParsedRoute();
                final ProcessParsedCalendarDate processCalendarDate = config.processCalendarDate();
                final ProcessParsedLevel processParsedLevel = config.processParsedLevel();
                final ProcessParsedCalendar processParsedCalendar = config.processParsedCalendar();
                final ProcessParsedTrip processParsedTrip = config.processParsedTrip();
                final ProcessParsedTransfer processParsedTransfer = config.processParsedTransfer();
                final ProcessParsedFeedInfo processParsedFeedInfo = config.processParsedFeedInfo();
                final ProcessParsedFareAttribute processParsedFareAttribute = config.processParsedFareAttribute();
                final ProcessParsedFareRule processParsedFareRule = config.processParsedFareRule();
                final ProcessParsedPathway processParsedPathway = config.processParsedPathway();
                final ProcessParsedAttribution processParsedAttribution = config.processParsedAttribution();
                final ProcessParsedShapePoint processParsedShapePoint = config.processParsedShapePoint();
                final ProcessParsedTranslation processParsedTranslation = config.processParsedTranslation();
                final ProcessParsedStopTime processParsedStopTime = config.processParsedStopTime();

                // base validation + build gtfs entities
                filenameListToProcess.forEach(filename -> {
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
                        if (filenameListToProcess.contains(filename)) {
                            switch (filename) {
                                case "agency.txt": {
                                    processParsedAgency.execute(parsedEntity);
                                    break;
                                }
                                case "routes.txt": {
                                    processParsedRoute.execute(parsedEntity);
                                    break;
                                }
                                case "calendar_dates.txt": {
                                    processCalendarDate.execute(parsedEntity);
                                    break;
                                }
                                case "levels.txt": {
                                    processParsedLevel.execute(parsedEntity);
                                    break;
                                }
                                case "attributions.txt": {
                                    processParsedAttribution.execute(parsedEntity);
                                    break;
                                }
                                case "calendar.txt": {
                                    processParsedCalendar.execute(parsedEntity);
                                    break;
                                }
                                case "trips.txt": {
                                    processParsedTrip.execute(parsedEntity);
                                    break;
                                }
                                case "transfers.txt": {
                                    processParsedTransfer.execute(parsedEntity);
                                    break;
                                }
                                case "feed_info.txt": {
                                    processParsedFeedInfo.execute(parsedEntity);
                                    break;
                                }
                                case "pathways.txt": {
                                    processParsedPathway.execute(parsedEntity);
                                    break;
                                }
                                case "fare_attributes.txt": {
                                    processParsedFareAttribute.execute(parsedEntity);
                                    break;
                                }
                                case "fare_rules.txt": {
                                    processParsedFareRule.execute(parsedEntity);
                                    break;
                                }
                                case "shapes.txt": {
                                    processParsedShapePoint.execute(parsedEntity);
                                    break;
                                }
                                case "translations.txt": {
                                    processParsedTranslation.execute(parsedEntity);
                                    break;
                                }
                                case "stop_times.txt": {
                                    processParsedStopTime.execute(parsedEntity);
                                    break;
                                }
                            }
                        }
                    }
                });

                config.validateRouteShortNameLength().execute();
                config.validateRouteColorAndTextContrast().execute();
                config.validateRouteDescriptionAndNameAreDifferent().execute();
                config.validateRouteTypeIsInOptions().execute();
                config.validateBothRouteNamesPresence().execute();
                config.validateRouteLongNameDoesNotContainShortName().execute();
                config.validateCalendarEndDateBeforeStartDate().execute();
                config.validateAgenciesHaveSameAgencyTimezone().execute();
                config.validateTripRouteId().execute();
                config.validateRouteAgencyId().execute();

                config.cleanOrCreatePath().execute(ExecParamRepository.OUTPUT_KEY);

                config.exportResultAsFile().execute();
            }
        } catch (IOException e) {
            logger.error("An exception occurred: " + e);
        }
        final long duration = System.nanoTime() - startTime;
        logger.info("Took " + String.format("%02dh %02dm %02ds", TimeUnit.NANOSECONDS.toHours(duration),
                TimeUnit.NANOSECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(duration)),
                TimeUnit.NANOSECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(duration))));
    }
}
