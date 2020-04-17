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

import org.mobilitydata.gtfsvalidator.usecase.notice.error.CannotDownloadArchiveFromNetworkNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Use case to download archive from network. This is the first step of the validation process.
 */
public class DownloadArchiveFromNetwork {

    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;

    /**
     * @param resultRepo a repository storing information about the validation process
     */
    public DownloadArchiveFromNetwork(final ValidationResultRepository resultRepo,
                                      final ExecParamRepository execParamRepo) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
    }

    /**
     * Use case execution method: downloads a GTFS archive at the URL provided in the constructor. If the process fails
     * a {@link CannotDownloadArchiveFromNetworkNotice} is generated and added to the {@link ValidationResultRepository}
     * provided in the constructor.
     */
    public void execute() throws IOException {
        //TODO: does using File class break clean architecture (make business logic dependant on a framework)?
        //Should the call to File happen in outside layers?
        if (execParamRepo.getExecParamValue("url") != null) {

            String zipInputPath = execParamRepo.getExecParamValue("zip") != null
                    ? execParamRepo.getExecParamValue("zip")
                    : System.getProperty("user.dir") + File.separator + "input.zip";

            try {
                URL sourceUrl = new URL(execParamRepo.getExecParamValue("url"));
                Files.copy(
                        sourceUrl.openStream(), // TODO: think about how to remove dependency on Files. FileCopier interface?
                        Paths.get(zipInputPath),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                resultRepo.addNotice
                        (new CannotDownloadArchiveFromNetworkNotice(
                                new URL(execParamRepo.getExecParamValue("url"))));
                throw e;
            }
        }
    }
}