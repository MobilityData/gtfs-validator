/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.springbootapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.config.DefaultConfig;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.TooManyValidationErrorException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
/**
 * This class implements the methods called from {@code GtfsValidatorController}. The methods of this class interact
 * with the different use cases defined in `usecase` layer via the {@code DefaultConfig} provided in this class
 * constructor.
 */
public class ServiceManager {
    private final Logger logger = LogManager.getLogger();
    private DefaultConfig config;

    /**
     * Initiates the {@code DefaultConfig} needed to proceed to GTFS archive validation. {@link DefaultConfig} is
     * instantiate from a set of execution parameters provided by the user via interaction with the web ui.
     *
     * @param execParamAsString the set of execution parameters as a Json string
     * @return null (for requirement of the testing framework)
     */
    public String initializeConfig(final String execParamAsString) throws Exception {
        try {
            this.config = new DefaultConfig.Builder()
                    .execParamAsString(execParamAsString)
                    .logger(logger)
                    .build();
        } catch (Exception e) {
            if (execParamAsString == null) {
                throw (new IOException("Configuration file not provided"));
            } else {
                throw e;
            }
        }
        return null;
    }

    /**
     * Checks if DefaultConfig has been initialized i.e execution parameter configuration file has been provided by user
     *
     * @return true if the user provided configuration file, else returns false.
     */
    public boolean isConfigInitialized() {
        return config != null;
    }

    /**
     * Run all use cases needed to validate a GTFS archive. Returns a String representing the validation status
     *
     * @return the validation status as a String
     */
    public String runValidator() throws IOException {
        if (!isConfigInitialized()) {
            throw (new IOException("Configuration file not provided"));
        }
        try {
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
                    logger.info("Validate CSV structure and field types for file: " + filename);
                    config.validateCsvNotEmptyForFile(filename).execute();
                    config.validateHeadersForFile(filename).execute();
                    config.validateAllRowLengthForFile(filename).execute();

                    final ParseSingleRowForFile parseSingleRowForFile = config.parseSingleRowForFile(filename);
                    while (parseSingleRowForFile.hasNext()) {
                        final ParsedEntity parsedEntity = parseSingleRowForFile.execute();
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
                config.validateFrequencyOverlap().execute();
                config.validateNoOverlappingStopTimeInTripBlock().execute();
                config.validateAgencyLangAndFeedInfoFeedLangMatch().execute();
                config.validateRouteLongNameAreUnique().execute();
                config.validateRouteShortNameAreUnique().execute();
                config.validateUniqueRouteLongNameRouteShortNameCombination().execute();

                config.cleanOrCreatePath().execute(ExecParamRepository.OUTPUT_KEY);

                config.exportResultAsFile().execute();

                return "Validation success";
            }
        } catch (IOException e) {
            logger.error("An exception occurred: " + e);
            throw new IOException("An exception occurred: " + e);

        } catch (TooManyValidationErrorException e) {
            logger.error("Error detected -- ABORTING");
            config.cleanOrCreatePath().execute(ExecParamRepository.OUTPUT_KEY);

            try {
                config.exportResultAsFile().execute();

                logger.info("Set option -" + ExecParamRepository.ABORT_ON_ERROR + " to false for validation process" +
                        " to continue on errors");
                return "Validation process aborted. Set option -" + ExecParamRepository.ABORT_ON_ERROR + " to false for validation process" +
                        " to continue on errors. Check validation report for more details.";
            } catch (IOException ioException) {
                logger.error("An exception occurred: " + e);
                throw new IOException("An exception occurred: " + e);
            }
        }
        return null;
    }

    /**
     * Executes shell command to open validation report in default text editor. Returns null.
     *
     * @return null and executes shell command to open validation report in default text editor
     */
    public String openReport() throws IOException {
        final Runtime runTime = Runtime.getRuntime();
        runTime.exec("open -t " + config.getExecParamValue(ExecParamRepository.OUTPUT_KEY) + "/results.json");
        return null;
    }
}
