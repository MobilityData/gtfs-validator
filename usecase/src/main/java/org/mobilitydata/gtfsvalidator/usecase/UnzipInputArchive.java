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

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotUnzipInputArchiveNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.warning.InputZipContainsFolderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipInputArchive {

    private final RawFileRepository rawFileRepo;
    private final ZipFile inputZip;
    private final Path zipExtractPath;
    private final ValidationResultRepository resultRepo;

    public UnzipInputArchive(final RawFileRepository fileRepo,
                             final ZipFile inputZip,
                             final Path zipExtractPath,
                             final ValidationResultRepository resultRepo) {
        this.rawFileRepo = fileRepo;
        this.inputZip = inputZip;
        this.zipExtractPath = zipExtractPath;
        this.resultRepo = resultRepo;
    }

    public void execute() {

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
