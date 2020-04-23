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
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

/**
 * Use case to log information about the validation process
 */
public class LogExecutionInfo {
    private final Logger logger;
    private final ExecParamRepository execParamRepo;

    /**
     * @param logger        logger used to log information
     * @param execParamRepo Repository holding execution parameters
     */
    public LogExecutionInfo(final Logger logger,
                            final ExecParamRepository execParamRepo) {
        this.logger = logger;
        this.execParamRepo = execParamRepo;
    }

    /**
     * Use case execution method: logs relevant information concerning the validation process.
     */
    public void execute() {
        if (execParamRepo.hasExecParamValue(execParamRepo.URL_KEY) & !execParamRepo
                .hasExecParamValue(execParamRepo.ZIP_KEY)) {
            logger.info("--url provided but no location to place zip (--zip option). Using default: " +
                    execParamRepo.getExecParamValue(execParamRepo.ZIP_KEY));
        }

        if (!execParamRepo.hasExecParamValue(execParamRepo.EXTRACT_KEY)) {
            logger.info("--input not provided. Will extract zip content in: " + execParamRepo
                    .getExecParamValue(ExecParamRepository.EXTRACT_KEY));
        }

        if (!execParamRepo.hasExecParamValue(execParamRepo.OUTPUT_KEY)) {
            logger.info("--output not provided. Will place execution results in: " + execParamRepo
                    .getExecParamValue(execParamRepo.OUTPUT_KEY));
        }
    }
}
