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
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
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

    private final RawFileRepository rawFileRepo;
    private final Path zipExtractPath;
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    /**
     * @param fileRepo       a repository storing information about a GTFS dataset
     * @param zipExtractPath a path pointing to the target directory
     * @param resultRepo     a repository storing information about the validation process
     */
    public UnzipInputArchive(final RawFileRepository fileRepo,
                             final Path zipExtractPath,
                             final ValidationResultRepository resultRepo,
                             final ExecParamRepository execParamRepo,
                             final Logger logger) {
        this.rawFileRepo = fileRepo;
        this.zipExtractPath = zipExtractPath;
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method. Tries to unzip an archive, if the process fails then
     * a {@link CannotUnzipInputArchiveNotice} is generated and added to the {@link ValidationResultRepository} provided
     * in the constructor.
     */
    public void execute() throws IOException {

        logger.info("Unzipping archive");

        final String zipInputPath = execParamRepo.getExecParamValue(execParamRepo.ZIP_KEY);
        final ZipFile inputZip = new ZipFile(zipInputPath);

        Enumeration<? extends ZipEntry> zipEntries = inputZip.entries();
        zipEntries.asIterator().forEachRemaining(entry -> {
            try {
                if (entry.isDirectory()) {
                    resultRepo.addNotice(new InputZipContainsFolderNotice(inputZip.getName(), entry.getName()));
                } else {
                    Path fileToCreate = zipExtractPath.resolve(entry.getName());
                    Files.copy(inputZip.getInputStream(entry), fileToCreate);
                    rawFileRepo.create(
                            new RawFileInfo.RawFileInfoBuilder()
                                    .filename(entry.getName())
                                    .path(zipExtractPath.toAbsolutePath().toString())
                                    .build()
                    );
                }
            } catch (IOException e) {
                //TODO: should CannotUnzipInputArchiveNotice be made a warning instead of an error?
                resultRepo.addNotice(new CannotUnzipInputArchiveNotice(inputZip.getName()));
            }
        });
    }
}
