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

import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;

/**
 * Use case to parse execution parameters from a .json file or from an Apache command line
 */
public class ParseAllExecParam {
    private final boolean fromConfigFile;
    private final String pathToExecParamFile;
    private final ExecParamRepository execParamRepository;

    /**
     * @param fromConfigFile      boolean specifying if the execution parameters should be retrieved from a .json file
     *                            of from an Apache command line.
     * @param pathToExecParamFile the path to the .json file to parse execution parameters from
     * @param execParamRepository the repository containing execution parameters and their values
     */
    public ParseAllExecParam(final boolean fromConfigFile,
                             final String pathToExecParamFile,
                             final ExecParamRepository execParamRepository) {
        this.fromConfigFile = fromConfigFile;
        this.pathToExecParamFile = pathToExecParamFile;
        this.execParamRepository = execParamRepository;
    }

    /**
     * Use case execution method: parses execution parameters from a .json file or from an Apache command line and adds
     * the resultant {@code ExecParam} to the repository provided to the constructor.
     * If the execution parameters are to be parsed for a .json file, the path to that file is specified in
     * {@param pathToExecParamFile}.
     * If the execution parameter are to be parsed from Apache command line, {@param args} holds the information to be
     * parsed.
     * This method throws {@link IOException} if the parsing operation could not be executed.
     * This method throws {@link IllegalArgumentException} if an {@link ExecParamRepository} could not be added to the
     * repository provided to the constructor.
     *
     * @param args the command line execution parameters to parse if {@param fromConfigFile} is false
     * @throws IOException              if the parsing operation could not be executed.
     * @throws IllegalArgumentException if an {@link ExecParamRepository} could not be added to the repository provided
     *                                  to the constructor.
     */
    public void execute(final String[] args) throws IllegalArgumentException, IOException {
        execParamRepository
                .getParser(fromConfigFile, pathToExecParamFile, args)
                .parse()
                .forEach((s, execParam) -> execParamRepository.addExecParam(execParam));
    }
}