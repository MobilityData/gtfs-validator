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

import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OutOfMemoryNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;

import java.nio.file.Path;
import java.util.Arrays;

public class HandleFatalCrash {
    private final ValidationResultRepository resultRepo;
    private final CustomFileUtils customFileUtils;
    private final Path inputPath;

    public HandleFatalCrash(final ValidationResultRepository resultRepo,
                            final CustomFileUtils customFileUtils,
                            final Path inputPath) {
        this.resultRepo = resultRepo;
        this.customFileUtils = customFileUtils;
        this.inputPath = inputPath;
    }

    public void execute() {
        final float datasetSizeMegaBytes = customFileUtils.sizeOf(inputPath, CustomFileUtils.MEGABYTES);
        final int noticeCount = resultRepo.getNoticeCount();
        resultRepo.addNotice(new OutOfMemoryNotice(datasetSizeMegaBytes, noticeCount));
    }

    public void execute(final Exception exception) {
        resultRepo.addNotice(
                new ValidatorCrashNotice(
                        exception.getMessage(),
                        Arrays.toString((exception.getStackTrace()))
                ));
    }
}
