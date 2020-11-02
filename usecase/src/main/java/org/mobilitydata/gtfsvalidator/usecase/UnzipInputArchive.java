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

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Use case to unzip an archive containing the GTFS dataset to validate. This step intervenes after the said archive
 * has been downloaded from the network.
 */
public class UnzipInputArchive {
    private static final String INVALID_FILENAME_PREFIX_STRING = "__MACOSX";
    private final RawFileRepository rawFileRepo;
    private final Path zipExtractPath;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;
    private final ZipFile inputZip;
    private final RawFileInfo.RawFileInfoBuilder rawFileInfoBuilder;
    /**
     * @param fileRepo       a repository storing information about a GTFS dataset
     * @param zipExtractPath a path pointing to the target directory
     * @param resultRepo     a repository storing information about the validation process
     */
    public UnzipInputArchive(final RawFileRepository fileRepo,
                             final Path zipExtractPath,
                             final ValidationResultRepository resultRepo,
                             final Logger logger,
                             final ZipFile inputZip,
                             final RawFileInfo.RawFileInfoBuilder rawFileInfoBuilder) {
        this.rawFileRepo = fileRepo;
        this.zipExtractPath = zipExtractPath;
        this.resultRepo = resultRepo;
        this.logger = logger;
        this.inputZip = inputZip;
        this.rawFileInfoBuilder = rawFileInfoBuilder;
    }

    /**
     * Use case execution method. Tries to unzip an archive, if the process fails then
     * a {@link CannotUnzipInputArchiveNotice} is generated and added to the {@link ValidationResultRepository} provided
     * in the constructor.
     */
    public void execute() {

        logger.info("Unzipping archive");

        final Enumeration<? extends ZipEntry> zipEntries = inputZip.entries();
        zipEntries.asIterator().forEachRemaining(entry -> {
            if (!entry.getName().contains(INVALID_FILENAME_PREFIX_STRING)) {
                try {
                    if (entry.isDirectory()) {
                        resultRepo.addNotice(new InputZipContainsFolderNotice(inputZip.getName(), entry.getName()));
                    } else {
                        final Path fileToCreate = zipExtractPath.resolve(entry.getName());
                        Files.copy(inputZip.getInputStream(entry), fileToCreate);
                        rawFileRepo.create(
                                rawFileInfoBuilder.filename(entry.getName())
                                        .path(zipExtractPath.toAbsolutePath().toString())
                                        .build()
                        );
                    }
                } catch (IOException e) {
                    //TODO: should CannotUnzipInputArchiveNotice be made a warning instead of an error?
                    resultRepo.addNotice(new CannotUnzipInputArchiveNotice(inputZip.getName()));
                }
            }
        });
    }
}
