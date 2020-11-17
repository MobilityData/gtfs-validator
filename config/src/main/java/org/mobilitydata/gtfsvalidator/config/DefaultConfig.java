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

package org.mobilitydata.gtfsvalidator.config;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import org.apache.commons.cli.HelpFormatter;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.db.*;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.fileutils.CustomFileUtilsImpl;
import org.mobilitydata.gtfsvalidator.geoutils.GeospatialUtilsImpl;
import org.mobilitydata.gtfsvalidator.timeutils.TimeUtilsImpl;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.*;
import org.mobilitydata.gtfsvalidator.usecase.usecasevalidator.ShapeBasedCrossValidator;
import org.mobilitydata.gtfsvalidator.usecase.usecasevalidator.StopTimeValidator;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.zip.ZipFile;

import static org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository.*;

/**
 * Configuration calling use cases for the execution of the validation process. This is necessary for the validation
 * process. Hence, this is created before calling the different use case of the validation process in the main method.
 */
public class DefaultConfig {
    private static final String DEFAULT_TIMEZONE_NAME = "America/Montreal";
    private static final TimeZone DEFAULT_TIMEZONE = SimpleTimeZone.getTimeZone(DEFAULT_TIMEZONE_NAME);
    private static final ZoneId DEFAULT_TIMEZONE_ID = DEFAULT_TIMEZONE.toZoneId();
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();
    private final ValidationResultRepository resultRepo;
    private final GtfsDataRepository gtfsDataRepository = new InMemoryGtfsDataRepository();
    private final TimeUtils timeUtils = TimeUtilsImpl.getInstance();
    private final GeospatialUtils geoUtils = GeospatialUtilsImpl.getInstance();
    private final GtfsSpecRepository specRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    private DefaultConfig(final String[] args, final Logger logger) {
        this.logger = logger;

        execParamRepo = new InMemoryExecParamRepository(
                args,
                loadDefaultParameter(),
                logger
        );

        specRepo = new InMemoryGtfsSpecRepository(loadGtfsProtobuf(), loadGtfsRelationshipDescription());

        resultRepo = new InMemoryValidationResultRepository(
                Boolean.parseBoolean(execParamRepo.getExecParamValue(ABORT_ON_ERROR)),
                Boolean.parseBoolean(execParamRepo.getExecParamValue(PROTO_KEY))
        );
    }

    private DefaultConfig(final String executionParametersAsString, final Logger logger) {
        this.logger = logger;

        execParamRepo = new InMemoryExecParamRepository(
                executionParametersAsString,
                loadDefaultParameter(),
                logger
        );

        specRepo = new InMemoryGtfsSpecRepository(loadGtfsProtobuf(), loadGtfsRelationshipDescription());

        resultRepo = new InMemoryValidationResultRepository(
                Boolean.parseBoolean(execParamRepo.getExecParamValue(ABORT_ON_ERROR)),
                Boolean.parseBoolean(execParamRepo.getExecParamValue(PROTO_KEY))
        );
    }

    public static class Builder {
        private Logger logger;
        private String execParamAsString;
        private String[] args;

        public Builder execParamAsString(final String execParamAsString) {
            this.execParamAsString = execParamAsString;
            return this;
        }

        public Builder args(final String[] args) {
            this.args = args;
            return this;
        }

        public Builder logger(final Logger logger) {
            this.logger = logger;
            return this;
        }

        public DefaultConfig build() {
            if (Strings.isNullOrEmpty(execParamAsString) && args.length == 0) {
                // true when json configuration file is not present and no arguments are provided
                logger.info("No configuration file nor arguments provided");
                return new DefaultConfig(execParamAsString, logger);
            } else if (!Strings.isNullOrEmpty(execParamAsString) || args.length == 0) {
                // true when no arguments are provided or when json configuration is provided
                logger.info("Retrieving execution parameters from execution-parameters.json file");
                return new DefaultConfig(execParamAsString, logger);
            } else {
                // true when only arguments are provided
                logger.info("Retrieving execution parameters from command-line");
                return new DefaultConfig(args, logger);
            }
        }
    }


    @SuppressWarnings("UnstableApiUsage")
    private String loadDefaultParameter() {
        String toReturn = null;
        try {
            toReturn = Resources.toString(
                    Resources.getResource("default-execution-parameters.json"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    @SuppressWarnings("UnstableApiUsage")
    private String loadGtfsProtobuf() {
        String toReturn = null;
        try {
            toReturn = Resources.toString(
                    Resources.getResource("gtfs_spec.asciipb"),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @SuppressWarnings("UnstableApiUsage")
    private String loadGtfsRelationshipDescription() {
        String toReturn = null;
        try {
            toReturn = Resources.toString(
                    Resources.getResource("gtfs-relationship-description.json"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public DownloadArchiveFromNetwork downloadArchiveFromNetwork() {
        return new DownloadArchiveFromNetwork(resultRepo, execParamRepo, logger);
    }

    public CreatePath createPath() {
        return new CreatePath(execParamRepo);
    }

    public UnzipInputArchive unzipInputArchive(final Path zipExtractPath) throws IOException {
        return new UnzipInputArchive(
                rawFileRepo,
                zipExtractPath,
                resultRepo,
                logger,
                new ZipFile(execParamRepo.getExecParamValue(execParamRepo.INPUT_KEY)),
                new RawFileInfo.RawFileInfoBuilder());
    }

    public ValidateAllRequiredFilePresence validateAllRequiredFilePresence() {
        return new ValidateAllRequiredFilePresence(specRepo, rawFileRepo, resultRepo);
    }

    public ValidateCsvNotEmptyForFile validateCsvNotEmptyForFile(final String filename) {
        return new ValidateCsvNotEmptyForFile(
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                specRepo, rawFileRepo, resultRepo, logger);
    }

    public ValidateHeadersForFile validateHeadersForFile(final String filename) {
        return new ValidateHeadersForFile(
                specRepo,
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                resultRepo,
                logger
        );
    }

    public ValidateAllRowLengthForFile validateAllRowLengthForFile(final String filename) {
        return new ValidateAllRowLengthForFile(
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                resultRepo
        );
    }

    public ParseSingleRowForFile parseSingleRowForFile(final String filename) {
        return new ParseSingleRowForFile(
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                specRepo,
                resultRepo
        );
    }

    public ValidateGtfsTypes validateGtfsTypes() {
        return new ValidateGtfsTypes(
                specRepo,
                resultRepo
        );
    }

    public GenerateGtfsRequiredFilenameList generateGtfsRequiredFilenameList() {
        return new GenerateGtfsRequiredFilenameList(specRepo);
    }

    public ValidateAllOptionalFilename validateAllOptionalFileName() {
        return new ValidateAllOptionalFilename(specRepo, rawFileRepo, resultRepo);
    }

    public ValidateRouteColorAndTextContrast validateRouteColorAndTextContrast() {
        return new ValidateRouteColorAndTextContrast(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteDescriptionAndNameAreDifferent validateRouteDescriptionAndNameAreDifferent() {
        return new ValidateRouteDescriptionAndNameAreDifferent(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteShortNameLength validateRouteShortNameLength() {
        return new ValidateRouteShortNameLength(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteTypeIsInTypeOptions validateRouteTypeIsInOptions() {
        return new ValidateRouteTypeIsInTypeOptions(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateShortAndLongNameForRoutePresence validateBothRouteNamesPresence() {
        return new ValidateShortAndLongNameForRoutePresence(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteLongNameDoesNotContainOrEqualShortName validateRouteLongNameDoesNotContainShortName() {
        return new ValidateRouteLongNameDoesNotContainOrEqualShortName(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteLongNameAreUnique validateRouteLongNameAreUnique() {
        return new ValidateRouteLongNameAreUnique(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteShortNameAreUnique validateRouteShortNameAreUnique() {
        return new ValidateRouteShortNameAreUnique(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateUniqueRouteLongNameRouteShortNameCombination validateUniqueRouteLongNameRouteShortNameCombination() {
        return new ValidateUniqueRouteLongNameRouteShortNameCombination(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateCalendarEndDateBeforeStartDate validateCalendarEndDateBeforeStartDate() {
        return new ValidateCalendarEndDateBeforeStartDate(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateFeedInfoEndDateAfterStartDate validateFeedInfoEndDateAfterStartDate() {
        return new ValidateFeedInfoEndDateAfterStartDate(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateFeedCoversTheNext7ServiceDays validateFeedCoversTheNext7ServiceDays() {
        return new ValidateFeedCoversTheNext7ServiceDays(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateFeedCoversTheNext30ServiceDays validateFeedCoversTheNext30ServiceDays() {
        return new ValidateFeedCoversTheNext30ServiceDays(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateFeedInfoFeedEndDateIsPresent validateFeedInfoFeedEndDateIsPresent() {
        return new ValidateFeedInfoFeedEndDateIsPresent(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateFeedInfoFeedStartDateIsPresent validateFeedInfoFeedStartDateIsPresent() {
        return new ValidateFeedInfoFeedStartDateIsPresent(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateAgencyLangAndFeedInfoFeedLangMatch validateAgencyLangAndFeedInfoFeedLangMatch() {
        return new ValidateAgencyLangAndFeedInfoFeedLangMatch(gtfsDataRepository, resultRepo, logger);
    }

    public ExportResultAsFile exportResultAsFile() {
        return new ExportResultAsFile(resultRepo,
                execParamRepo,
                gtfsDataRepository,
                Timestamp.valueOf(LocalDateTime.now(DEFAULT_TIMEZONE_ID)),
                logger);
    }

    public GenerateInfoNotice generateInfoNotice(final long processingTimeSecs,
                                                 final Set<String> processedFilenameCollection) {
        return new GenerateInfoNotice(
                resultRepo,
                execParamRepo,
                gtfsDataRepository,
                Timestamp.valueOf(LocalDateTime.now(DEFAULT_TIMEZONE_ID)),
                processingTimeSecs,
                processedFilenameCollection,
                CustomFileUtilsImpl.getInstance(),
                Path.of(execParamRepo.getExecParamValue(ExecParamRepository.INPUT_KEY)),
                Path.of(execParamRepo.getExecParamValue(ExecParamRepository.EXTRACT_KEY))
        );
    }

    public LogExecutionInfo logExecutionInfo() {
        return new LogExecutionInfo(logger, execParamRepo);
    }

    public PrintHelp printHelp() {
        return new PrintHelp(execParamRepo, new HelpFormatter());
    }

    public ProcessParsedAgency processParsedAgency() {
        return new ProcessParsedAgency(resultRepo, gtfsDataRepository, new Agency.AgencyBuilder());
    }

    public ProcessParsedRoute processParsedRoute() {
        return new ProcessParsedRoute(resultRepo, gtfsDataRepository, new Route.RouteBuilder());
    }

    public ProcessParsedCalendarDate processCalendarDate() {
        return new ProcessParsedCalendarDate(resultRepo, gtfsDataRepository, new CalendarDate.CalendarDateBuilder());
    }

    public ProcessParsedLevel processParsedLevel() {
        return new ProcessParsedLevel(resultRepo, gtfsDataRepository, new Level.LevelBuilder());
    }

    public ProcessParsedCalendar processParsedCalendar() {
        return new ProcessParsedCalendar(resultRepo, gtfsDataRepository, new Calendar.CalendarBuilder());
    }

    public ProcessParsedTrip processParsedTrip() {
        return new ProcessParsedTrip(resultRepo, gtfsDataRepository, new Trip.TripBuilder());
    }

    public ProcessParsedTransfer processParsedTransfer() {
        return new ProcessParsedTransfer(resultRepo, gtfsDataRepository, new Transfer.TransferBuilder());
    }

    public ProcessParsedFeedInfo processParsedFeedInfo() {
        return new ProcessParsedFeedInfo(resultRepo, gtfsDataRepository, new FeedInfo.FeedInfoBuilder());
    }

    public ProcessParsedFareAttribute processParsedFareAttribute() {
        return new ProcessParsedFareAttribute(resultRepo, gtfsDataRepository, new FareAttribute.FareAttributeBuilder());
    }

    public ProcessParsedFareRule processParsedFareRule() {
        return new ProcessParsedFareRule(resultRepo, gtfsDataRepository, new FareRule.FareRuleBuilder());
    }

    public ProcessParsedPathway processParsedPathway() {
        return new ProcessParsedPathway(resultRepo, gtfsDataRepository, new Pathway.PathwayBuilder());
    }

    public ProcessParsedAttribution processParsedAttribution() {
        return new ProcessParsedAttribution(resultRepo, gtfsDataRepository, new Attribution.AttributionBuilder());
    }

    public ProcessParsedShapePoint processParsedShapePoint() {
        return new ProcessParsedShapePoint(resultRepo, gtfsDataRepository, new ShapePoint.ShapeBuilder());
    }

    public ProcessParsedStopTime processParsedStopTime() {
        return new ProcessParsedStopTime(resultRepo, gtfsDataRepository, timeUtils,
                new StopTime.StopTimeBuilder());
    }

    public ProcessParsedTranslation processParsedTranslation() {
        return new ProcessParsedTranslation(resultRepo, gtfsDataRepository, new Translation.TranslationBuilder());
    }

    public PreprocessParsedStop preprocessParsedStop() {
        return new PreprocessParsedStop(resultRepo);
    }

    public ProcessParsedStopAll processParsedStopAll() {
        return new ProcessParsedStopAll(resultRepo, gtfsDataRepository,
                new StopOrPlatform.StopOrPlatformBuilder(),
                new Station.StationBuilder(),
                new Entrance.EntranceBuilder(),
                new GenericNode.GenericNodeBuilder(),
                new BoardingArea.BoardingAreaBuilder());
    }

    public ProcessParsedFrequency processParsedFrequency() {
        return new ProcessParsedFrequency(resultRepo, gtfsDataRepository, timeUtils, new Frequency.FrequencyBuilder());
    }

    public GenerateExclusionFilenameList generateExclusionFilenameList() {
        return new GenerateExclusionFilenameList(specRepo, execParamRepo, logger);
    }

    public GenerateFilenameListToProcess generateFilenameListToProcess() {
        return new GenerateFilenameListToProcess(logger);
    }

    public ValidateAgenciesHaveSameAgencyTimezone validateAgenciesHaveSameAgencyTimezone() {
        return new ValidateAgenciesHaveSameAgencyTimezone(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripRouteId validateTripRouteId() {
        return new ValidateTripRouteId(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripServiceId validateTripServiceId() {
        return new ValidateTripServiceId(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripEdgeArrivalDepartureTime validateTripEdgeArrivalDepartureTime() {
        return new ValidateTripEdgeArrivalDepartureTime(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripTravelSpeed validateTripTravelSpeed() {
        return new ValidateTripTravelSpeed(gtfsDataRepository, resultRepo, geoUtils, logger);
    }

    public ValidateTripUsage validateTripUsage() {
        return new ValidateTripUsage(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripNumberOfStops validateTripNumberOfStops() {
        return new ValidateTripNumberOfStops(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteAgencyId validateRouteAgencyId() {
        return new ValidateRouteAgencyId(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateNoOverlappingStopTimeInTripBlock validateNoOverlappingStopTimeInTripBlock() {
        return new ValidateNoOverlappingStopTimeInTripBlock(gtfsDataRepository, resultRepo, logger, timeUtils);
    }

    public StopTimeValidator stopTimeBasedCrossValidator() {
        return new StopTimeValidator(gtfsDataRepository, resultRepo, logger, timeUtils,
                new ValidateShapeIdReferenceInStopTime(),
                new ValidateStopTimeTripId(),
                new ValidateBackwardsTimeTravelForStops(),
                new ValidateStopTimeIncreasingDistance(resultRepo));
    }

    public ValidateFrequencyStartTimeBeforeEndTime validateFrequencyStartTimeBeforeEndTime() {
        return new ValidateFrequencyStartTimeBeforeEndTime(gtfsDataRepository, resultRepo, timeUtils, logger);
    }

    public ValidateTripFrequenciesOverlap validateFrequencyOverlap() {
        return new ValidateTripFrequenciesOverlap(gtfsDataRepository, resultRepo, timeUtils, logger);
    }

    public ValidateStopTimeDepartureTimeAfterArrivalTime validateStopTimeDepartureTimeAfterArrivalTime() {
        return new ValidateStopTimeDepartureTimeAfterArrivalTime(gtfsDataRepository, resultRepo, timeUtils, logger);
    }

    public ShapeBasedCrossValidator shapeBasedCrossValidator() {
        return new ShapeBasedCrossValidator(
                gtfsDataRepository,
                resultRepo,
                logger,
                new ValidateShapeUsage(),
                new ValidateShapeIncreasingDistance());
    }

    public ValidateStopTooFarFromTripShape validateStopTooFarFromTripShape() {
        return new ValidateStopTooFarFromTripShape(gtfsDataRepository, resultRepo, geoUtils, logger);
    }

    public String getExecParamValue(final String execParamKey) {
        return execParamRepo.getExecParamValue(execParamKey);
    }
}
