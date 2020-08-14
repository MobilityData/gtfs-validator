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

import java.io.IOException;

/**
 * Use case to parse execution parameters from a .json file or from an Apache command line
 */
public class ParseAllExecParam {
    private final String parameterJsonString;
    private final ExecParamRepository execParamRepository;
    private final Logger logger;

    /**
     * @param parameterJsonString the .json file content to extract execution parameters from
     * @param execParamRepository the repository containing execution parameters and their values
     */
    public ParseAllExecParam(final String parameterJsonString,
                             final ExecParamRepository execParamRepository,
                             final Logger logger) {
        this.parameterJsonString = parameterJsonString;
        this.execParamRepository = execParamRepository;
        this.logger = logger;
    }

    /**
     * Use case execution method: parses execution parameters from a .json file or from an Apache command line and adds
     * the resultant {@code ExecParam} to the repository provided to the constructor.
     * If the execution parameter are to be parsed from Apache command line, {args} holds the information to be
     * parsed.
     * This method throws {@link IOException} if the parsing operation could not be executed.
     * This method throws {@link IllegalArgumentException} if an {@link ExecParamRepository} could not be added to the
     * repository provided to the constructor.
     *
     * @throws IOException              if the parsing operation could not be executed.
     * @throws IllegalArgumentException if an {@link ExecParamRepository} could not be added to the repository provided
     *                                  to the constructor.
     */
    public void execute() throws IllegalArgumentException, IOException {
        execParamRepository
                .getParser()
                .parse()
                .forEach((s, execParam) -> execParamRepository.addExecParam(execParam));
    }
}