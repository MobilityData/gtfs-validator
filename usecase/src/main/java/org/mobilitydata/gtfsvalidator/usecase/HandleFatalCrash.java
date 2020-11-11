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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.info.ValidatorCrashNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Arrays;

public class HandleFatalCrash {
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final ExportResultAsFile exportResultAsFile;
    private final Logger logger;

    public HandleFatalCrash(final ValidationResultRepository resultRepo,
                            final ExecParamRepository execParamRepo,
                            final ExportResultAsFile exportResultAsFile,
                            final Logger logger) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.exportResultAsFile = exportResultAsFile;
        this.logger = logger;
    }

    public void execute(final Object exceptionOrError) throws Throwable {
        if (Boolean.parseBoolean(execParamRepo.getExecParamValue(ExecParamRepository.DEBUG_KEY))) {
            throw (Throwable) exceptionOrError;
        } else {
            resultRepo.addNotice(
                    new ValidatorCrashNotice(
                            ((Throwable) exceptionOrError).getMessage(),
                            Arrays.toString(((Throwable) exceptionOrError).getStackTrace()))
            );
            try {
                exportResultAsFile.execute();
            } catch (Exception e) {
                logger.error(String.format("Could not export results as file: %s -- stackTrace: %s",
                        e.getMessage(),
                        Arrays.toString(e.getStackTrace())));
            }
        }
    }
}
