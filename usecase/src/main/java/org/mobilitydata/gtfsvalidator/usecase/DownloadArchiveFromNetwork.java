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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CannotDownloadArchiveFromNetworkNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;

/**
 * Use case to download archive from network. This is the first step of the validation process.
 */
public class DownloadArchiveFromNetwork {

    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final Logger logger;

    /**
     * @param resultRepo a repository storing information about the validation process
     * @param logger     logger used to log relevant information about the downloading process
     */
    public DownloadArchiveFromNetwork(final ValidationResultRepository resultRepo,
                                      final ExecParamRepository execParamRepo,
                                      final Logger logger) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: downloads a GTFS archive at the URL provided in the constructor. If the process fails
     * a {@link CannotDownloadArchiveFromNetworkNotice} is generated and added to the {@link ValidationResultRepository}
     * provided in the constructor.
     */
    public void execute() throws IOException {
        //TODO: does using File class break clean architecture (make business logic dependant on a framework)?
        //Should the call to File happen in outside layers?
        if (execParamRepo.hasExecParamValue(execParamRepo.URL_KEY)) {
            logger.info("Downloading archive");
            final String url = execParamRepo.getExecParamValue(execParamRepo.URL_KEY);
            final String targetPath = execParamRepo.getExecParamValue(execParamRepo.INPUT_KEY);
            InputStream inputStream;

            try {
                final URL sourceUrl = new URL(url);
                final HttpURLConnection httpConnection = (HttpURLConnection) sourceUrl.openConnection();
                final String newUrlAsString = httpConnection.getHeaderField("Location");
                if (newUrlAsString != null) {
                    // use redirection instead of original url
                    final URLConnection connection = new URL(newUrlAsString).openConnection();
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = sourceUrl.openStream();
                }

                Files.copy(
                        inputStream, // TODO: think about how to remove dependency on Files. FileCopier interface?
                        Paths.get(targetPath),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                resultRepo.addNotice
                        (new CannotDownloadArchiveFromNetworkNotice(
                                new URL(url)));
                throw e;
            }
        }
    }
}
