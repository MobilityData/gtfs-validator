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

import com.google.common.io.Resources;
import org.apache.commons.cli.HelpFormatter;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.db.*;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.timeutils.TimeConversionUtils;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.*;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration calling use cases for the execution of the validation process. This is necessary for the validation
 * process. Hence, this is created before calling the different use case of the validation process in the main method.
 */
public class DefaultConfig {
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();
    private final ValidationResultRepository resultRepo = new InMemoryValidationResultRepository();
    private final GtfsDataRepository gtfsDataRepository = new InMemoryGtfsDataRepository();
    private final TimeUtils timeUtils = TimeConversionUtils.getInstance();
    private final GtfsSpecRepository specRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;
    private String executionParametersAsString;

    @SuppressWarnings("UnstableApiUsage")
    public DefaultConfig(final Logger logger) {
        this.logger = logger;
        String defaultParameterJsonString = null;
        try {
            defaultParameterJsonString = Resources.toString(
                    Resources.getResource("default-execution-parameters.json"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        execParamRepo = new InMemoryExecParamRepository(defaultParameterJsonString, this.logger);

        String gtfsSpecProtobufString = null;

        try {
            gtfsSpecProtobufString = Resources.toString(
                    Resources.getResource("gtfs_spec.asciipb"),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        String gtfsSchemaAsString = null;

        try {
            gtfsSchemaAsString = Resources.toString(
                    Resources.getResource("gtfs-relationship-description.json"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.executionParametersAsString = null;
        try {
            this.executionParametersAsString = Files.readString(Paths.get("execution-parameters.json"));
            logger.info("Configuration file execution-parameters.json found in working directory" +
                    System.lineSeparator());
        } catch (IOException e) {
            logger.warn("Configuration file execution-parameters.json not found in working directory" +
                    System.lineSeparator());
        }
        specRepo = new InMemoryGtfsSpecRepository(gtfsSpecProtobufString, gtfsSchemaAsString);
    }

    public DownloadArchiveFromNetwork downloadArchiveFromNetwork() {
        return new DownloadArchiveFromNetwork(resultRepo, execParamRepo, logger);
    }

    public CleanOrCreatePath cleanOrCreatePath() {
        return new CleanOrCreatePath(execParamRepo);
    }

    public UnzipInputArchive unzipInputArchive(final Path zipExtractPath) {
        return new UnzipInputArchive(rawFileRepo, zipExtractPath, resultRepo, execParamRepo, logger);
    }

    public ValidateAllRequiredFilePresence validateAllRequiredFilePresence() {
        return new ValidateAllRequiredFilePresence(specRepo, rawFileRepo, resultRepo);
    }

    public ValidateHeadersForFile validateHeadersForFile(final String filename) {
        return new ValidateHeadersForFile(
                specRepo,
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                resultRepo
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

    public ValidateCalendarEndDateBeforeStartDate validateCalendarEndDateBeforeStartDate() {
        return new ValidateCalendarEndDateBeforeStartDate(gtfsDataRepository, resultRepo, logger);
    }

    public ExportResultAsFile exportResultAsFile() {
        return new ExportResultAsFile(resultRepo, execParamRepo, logger);
    }

    public ParseAllExecParam parseAllExecutionParameter() {
        return new ParseAllExecParam(executionParametersAsString, execParamRepo,
                logger);
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

    public GenerateExclusionFilenameList generateExclusionFilenameList() {
        return new GenerateExclusionFilenameList(specRepo, execParamRepo, logger);
    }

    public GenerateFilenameListToProcess generateFilenameListToProcess() {
        return new GenerateFilenameListToProcess(logger);
    }

    public ValidateAgencyIdRequirement validateAgencyIdRequirement() {
        return new ValidateAgencyIdRequirement(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateAgenciesHaveSameAgencyTimezone validateAgenciesHaveSameAgencyTimezone() {
        return new ValidateAgenciesHaveSameAgencyTimezone(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateTripRouteId validateTripRouteId() {
        return new ValidateTripRouteId(gtfsDataRepository, resultRepo, logger);
    }

    public ValidateRouteAgencyId validateRouteAgencyId() {
        return new ValidateRouteAgencyId(gtfsDataRepository, resultRepo, logger);
    }
}
