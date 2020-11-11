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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OutOfMemoryNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;

import java.nio.file.Path;
import java.util.Arrays;

public class HandleFatalCrash {
    private final ValidationResultRepository resultRepo;
    private final ExportResultAsFile exportResultAsFile;
    private final CustomFileUtils customFileUtils;
    private final Path inputPath;
    private final Logger logger;

    public HandleFatalCrash(final ValidationResultRepository resultRepo,
                            final ExportResultAsFile exportResultAsFile,
                            final CustomFileUtils customFileUtils,
                            final Path inputPath,
                            final Logger logger) {
        this.resultRepo = resultRepo;
        this.exportResultAsFile = exportResultAsFile;
        this.customFileUtils = customFileUtils;
        this.inputPath = inputPath;
        this.logger = logger;
    }

    public void execute(final Object exceptionOrError) {
        final float datasetSizeMegaBytes = customFileUtils.sizeOf(inputPath);
        final int noticeCount = resultRepo.getErrorNoticeCount() + resultRepo.getWarningNoticeCount() + resultRepo.getInfoNoticeCount();
        if (exceptionOrError instanceof OutOfMemoryError) {
            resultRepo.addNotice(new OutOfMemoryNotice(datasetSizeMegaBytes, noticeCount));
        } else {
            resultRepo.addNotice(
                    new ValidatorCrashNotice(
                            ((Throwable) exceptionOrError).getMessage(),
                            Arrays.toString(((Throwable) exceptionOrError).getStackTrace())
                    )
            );
        }
        try {
            exportResultAsFile.execute();
        } catch (Exception e) {
            logger.error(String.format("Could not export results as file: %s -- stackTrace: %s",
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace())));
        }
    }
}
