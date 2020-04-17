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

import org.mobilitydata.gtfsvalidator.db.InMemoryExecParamRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryGtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryRawFileRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryValidationResultRepository;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Configuration calling use cases for the execution of the validation process. This is necessary for the validation
 * process. Hence, this is created before calling the different use case of the validation process in the main method.
 */
public class DefaultConfig {
    private final GtfsSpecRepository specRepo = new InMemoryGtfsSpecRepository("gtfs_spec.asciipb");
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();
    private final ValidationResultRepository resultRepo = new InMemoryValidationResultRepository();
    private final ExecParamRepository executionParameterRepo = new InMemoryExecParamRepository();

    public DefaultConfig() throws IOException {
    }

    public DownloadArchiveFromNetwork downloadArchiveFromNetwork() {
        return new DownloadArchiveFromNetwork(resultRepo, executionParameterRepo);
    }

    public CleanOrCreatePath cleanOrCreatePath() {
        return new CleanOrCreatePath(resultRepo, executionParameterRepo);
    }

    public UnzipInputArchive unzipInputArchive(final Path zipExtractPath) {
        return new UnzipInputArchive(rawFileRepo, zipExtractPath, resultRepo, executionParameterRepo);
    }

    public ValidateAllRequiredFilePresence validateAllRequiredFilePresence() {
        return new ValidateAllRequiredFilePresence(specRepo, rawFileRepo, resultRepo);
    }

    public ValidateHeadersForFile validateHeadersForFile(String filename) {
        return new ValidateHeadersForFile(
                specRepo,
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                resultRepo
        );
    }

    public ValidateAllRowLengthForFile validateAllRowLengthForFile(String filename) {
        return new ValidateAllRowLengthForFile(
                rawFileRepo.findByName(filename).orElse(RawFileInfo.builder().build()),
                rawFileRepo,
                resultRepo
        );
    }

    public ParseSingleRowForFile parseSingleRowForFile(String filename) {
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

    public Collection<Notice> getValidationResult() {
        return resultRepo.getAll();
    }

    public ValidateAllOptionalFilename validateAllOptionalFileName() {
        return new ValidateAllOptionalFilename(specRepo, rawFileRepo, resultRepo);
    }

    public ExportResultAsFile exportResultAsFile() {
        return new ExportResultAsFile(resultRepo, executionParameterRepo);
    }

    public ParseAllExecParam parseAllExecutionParameter(final boolean fromConfigFile, final String pathToConfigFile,
                                                        final String pathToDefaultConfigFile) throws
            IllegalArgumentException {
        return new ParseAllExecParam(fromConfigFile, pathToConfigFile, executionParameterRepo, pathToDefaultConfigFile);
    }
}