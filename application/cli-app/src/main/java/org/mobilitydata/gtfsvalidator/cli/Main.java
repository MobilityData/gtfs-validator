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

package org.mobilitydata.gtfsvalidator.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.config.DefaultConfig;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.TooManyValidationErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();
        final DefaultConfig config = initConfig(args, logger);
        final Set<String> processedFilenameCollection = new HashSet<>();
        config.createPath().execute(ExecParamRepository.OUTPUT_KEY, false);

        try {
            // use case will inspect parameters and decide if help menu should be displayed or not
            if (!config.printHelp().execute()) {

                // use case will inspect parameters and display relevant information about the validation execution
                // process
                config.logExecutionInfo().execute();

                // use case will inspect parameters and decide if GTFS dataset should be downloaded or not
                config.downloadArchiveFromNetwork().execute();

                config.unzipInputArchive(
                        config.createPath().execute(ExecParamRepository.EXTRACT_KEY, true))
                        .execute();

                final ArrayList<String> filenameListToExclude = config.generateExclusionFilenameList().execute();

                config.validateAllRequiredFilePresence().execute();
                final List<String> gtfsRequiredFilenameList = config.generateGtfsRequiredFilenameList().execute();
                final List<String> gtfsArchiveOptionalFilenameList = config.validateAllOptionalFileName().execute();
                final ArrayList<String> gtfsArchiveValidFilenameList = new ArrayList<>();
                gtfsArchiveValidFilenameList.addAll(gtfsRequiredFilenameList);
                gtfsArchiveValidFilenameList.addAll(gtfsArchiveOptionalFilenameList);

                final List<String> filenameListToProcess =
                        config.generateFilenameListToProcess().execute(filenameListToExclude,
                                gtfsArchiveValidFilenameList);

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
                final ProcessParsedFrequency processParsedFrequency = config.processParsedFrequency();
                final ProcessParsedPathway processParsedPathway = config.processParsedPathway();
                final ProcessParsedAttribution processParsedAttribution = config.processParsedAttribution();
                final ProcessParsedShapePoint processParsedShapePoint = config.processParsedShapePoint();
                final ProcessParsedTranslation processParsedTranslation = config.processParsedTranslation();
                final ProcessParsedStopTime processParsedStopTime = config.processParsedStopTime();
                final PreprocessParsedStop preprocessParsedStop = config.preprocessParsedStop();

                final Map<String, ParsedEntity> preprocessedStopByStopId = new HashMap<>();

                filenameListToProcess.forEach(filename -> {
                    logger.info(System.lineSeparator() + System.lineSeparator() +
                            "Validate CSV structure and field types for file: " + filename);
                    processedFilenameCollection.add(filename);
                    config.validateCsvNotEmptyForFile(filename).execute();
                    config.validateHeadersForFile(filename).execute();
                    config.validateAllRowLengthForFile(filename).execute();

                    final ParseSingleRowForFile parseSingleRowForFile = config.parseSingleRowForFile(filename);
                    while (parseSingleRowForFile.hasNext()) {
                        final ParsedEntity parsedEntity = parseSingleRowForFile.execute();
                        if (parsedEntity != null) {
                            validateGtfsTypes.execute(parsedEntity);

                            // load gtfs entities into memory
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
                                    case "frequencies.txt": {
                                        processParsedFrequency.execute(parsedEntity);
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
                                    case "stops.txt": {
                                        // rows from stops.txt refer each others
                                        // building a map of all rows for further processing
                                        ParsedEntity preprocessedStop = preprocessParsedStop.execute(parsedEntity,
                                                preprocessedStopByStopId.keySet());
                                        if (preprocessedStop != null) {
                                            preprocessedStopByStopId.put(preprocessedStop.getEntityId(), preprocessedStop);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });

                config.processParsedStopAll().execute(preprocessedStopByStopId);
                preprocessedStopByStopId.clear();

                config.validateRouteShortNameLength().execute();
                config.validateRouteColorAndTextContrast().execute();
                config.validateRouteDescriptionAndNameAreDifferent().execute();
                config.validateRouteTypeIsInOptions().execute();
                config.validateBothRouteNamesPresence().execute();
                config.validateRouteLongNameDoesNotContainShortName().execute();
                config.validateCalendarEndDateBeforeStartDate().execute();
                config.validateAgenciesHaveSameAgencyTimezone().execute();
                config.validateTripRouteId().execute();
                config.validateTripServiceId().execute();
                config.validateRouteAgencyId().execute();
                config.stopTimeBasedCrossValidator().execute();
                config.shapeBasedCrossValidator().execute();
                config.validateFeedInfoEndDateAfterStartDate().execute();
                config.validateFeedCoversTheNext7ServiceDays().execute();
                config.validateFeedCoversTheNext30ServiceDays().execute();
                config.validateFeedInfoFeedEndDateIsPresent().execute();
                config.validateFeedInfoFeedStartDateIsPresent().execute();
                config.validateStopTimeDepartureTimeAfterArrivalTime().execute();
                config.validateTripEdgeArrivalDepartureTime().execute();
                config.validateTripTravelSpeed().execute();
                config.validateTripUsage().execute();
                config.validateTripNumberOfStops().execute();
                config.validateFrequencyStartTimeBeforeEndTime().execute();
                config.validateStopTooFarFromTripShape().execute();
                config.validateFrequencyOverlap().execute();
                config.validateNoOverlappingStopTimeInTripBlock().execute();
                config.validateAgencyLangAndFeedInfoFeedLangMatch().execute();
                config.validateRouteLongNameAreUnique().execute();
                config.validateRouteShortNameAreUnique().execute();
                config.validateUniqueRouteLongNameRouteShortNameCombination().execute();
            }
        } catch (IOException e) {
            logger.error("An exception occurred: " + e);
        } catch (TooManyValidationErrorException e) {
            logger.error("Error detected during data validation -- ABORTING");
            logger.info("Set option -" + ExecParamRepository.ABORT_ON_ERROR + " to false for validation process" +
                    " to continue on errors");
        } catch (OutOfMemoryError e) {
            config.handleOutOfMemoryError().execute();
        } catch (Exception e) {
            config.handleUnsupportedException().execute(e);
        } finally {
            try {
                config.generateInfoNotice(
                        TimeUnit.NANOSECONDS.toHours(System.nanoTime() - startTime),
                        processedFilenameCollection).execute();
                config.exportResultAsFile().execute();
            } catch (IOException ioException) {
                logger.error(String.format("Could not export results as file: %s", ioException.getMessage()));
            }
            logProcessingTime(logger, System.nanoTime() - startTime);
        }
    }

    private static DefaultConfig initConfig(String[] args, Logger logger) {
        String executionParametersAsString = null;

        try {
            executionParametersAsString = Files.readString(Paths.get("execution-parameters.json"));
            logger.info("Configuration file execution-parameters.json found in working directory");
        } catch (IOException e) {
            logger.warn("Configuration file execution-parameters.json not found in working directory");
        }
        final DefaultConfig.Builder configBuilder = new DefaultConfig.Builder();
        return configBuilder.logger(logger)
                .args(args)
                .execParamAsString(executionParametersAsString)
                .build();
    }

    private static void logProcessingTime(final Logger logger, final long duration) {
        logger.info("Took " + String.format("%02dh %02dm %02ds", TimeUnit.NANOSECONDS.toHours(duration),
                TimeUnit.NANOSECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(duration)),
                TimeUnit.NANOSECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(duration))));
    }
}
