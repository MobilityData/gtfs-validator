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
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadArchiveFromNetwork {

    private final URL sourceUrl;
    private final String targetPath;
    private final ValidationResultRepository resultRepo;


    public DownloadArchiveFromNetwork(final URL url,
                                      final String targetPath,
                                      final ValidationResultRepository resultRepo) {
        this.sourceUrl = url;
        this.targetPath = targetPath;
        this.resultRepo = resultRepo;
    }

    public void execute() {
        //TODO: does using File class break clean architecture (make business logic dependant on a framework)?
        //Should the call to File happen in outside layers?
        try {
            Files.copy(
                    sourceUrl.openStream(), // TODO: think about how to remove dependency on Files. FileCopier interface?
                    Paths.get(targetPath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            resultRepo.addNotice(new CannotDownloadArchiveFromNetworkNotice(sourceUrl));
        }
    }
}