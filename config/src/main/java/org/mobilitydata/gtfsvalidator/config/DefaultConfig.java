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
import org.mobilitydata.gtfsvalidator.db.InMemoryExecParamRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryGtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryRawFileRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryValidationResultRepository;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

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
    private final GtfsSpecRepository specRepo = new InMemoryGtfsSpecRepository("gtfs_spec.asciipb");
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();
    private final ValidationResultRepository resultRepo = new InMemoryValidationResultRepository();
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    @SuppressWarnings("UnstableApiUsage")
    public DefaultConfig(final Logger logger) {
        this.logger = logger;
        String defaultParameterJsonString;
        try {
            defaultParameterJsonString = Resources.toString(
                    Resources.getResource("default-execution-parameters.json"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            defaultParameterJsonString = "[]";
        }
        execParamRepo = new InMemoryExecParamRepository(defaultParameterJsonString, this.logger);
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

    public ExportResultAsFile exportResultAsFile() {
        return new ExportResultAsFile(resultRepo, execParamRepo, logger);
    }

    public ParseAllExecParam parseAllExecutionParameter() throws IOException {
        return new ParseAllExecParam(Files.readString(Paths.get("execution-parameters.json")), execParamRepo,
                logger);
    }

    public LogExecutionInfo logExecutionInfo() {
        return new LogExecutionInfo(logger, execParamRepo);
    }

    public PrintHelp printHelp() {
        return new PrintHelp(execParamRepo, new HelpFormatter());
    }
}