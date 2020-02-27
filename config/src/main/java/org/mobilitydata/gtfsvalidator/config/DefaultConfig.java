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

import org.mobilitydata.gtfsvalidator.db.InMemoryGtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryRawFileRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryValidationResultRepository;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.zip.ZipFile;

public class DefaultConfig {
    private final GtfsSpecRepository specRepo = new InMemoryGtfsSpecRepository("gtfs_spec.asciipb");
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();
    private final ValidationResultRepository resultRepo = new InMemoryValidationResultRepository();

    public DefaultConfig() throws IOException {
    }

    public DownloadArchiveFromNetwork downloadArchiveFromNetwork(final String url, final String targetPath) throws MalformedURLException {
        return new DownloadArchiveFromNetwork(new URL(url), targetPath, resultRepo);
    }

    public CleanOrCreatePath cleanOrCreatePath(final String toCleanOrCreate) {
        return new CleanOrCreatePath(toCleanOrCreate, resultRepo);
    }

    public UnzipInputArchive unzipInputArchive(final String zipInputPath, final Path zipExtractPath) throws IOException {
        return new UnzipInputArchive(rawFileRepo, new ZipFile(zipInputPath), zipExtractPath, resultRepo);
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

    public ParseAllRowForFile parseAllRowForFile(String filename) {
        return new ParseAllRowForFile(
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
}
