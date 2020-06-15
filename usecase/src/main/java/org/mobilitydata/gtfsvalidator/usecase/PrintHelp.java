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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.File;

/**
 * Use case to print help
 */
public class PrintHelp {
    private final ExecParamRepository execParamRepo;
    private final HelpFormatter helpFormatter;

    /**
     * Use case to print help
     *
     * @param execParamRepo repository containing execution parameters
     * @param helpFormatter formatter for help menu
     */
    public PrintHelp(final ExecParamRepository execParamRepo, final HelpFormatter helpFormatter) {
        this.execParamRepo = execParamRepo;
        this.helpFormatter = helpFormatter;
    }

    /**
     * Use case execution method: prints help menu if execution parameter with key HELP_KEY="help" is contained in the
     * repository provided in the constructor or if the ExecParamRepository provided in the constructor is empty.
     */
    public boolean execute() {
        if (Boolean.parseBoolean(execParamRepo.getExecParamValue(execParamRepo.HELP_KEY)) || execParamRepo.isEmpty()) {

            Options options = execParamRepo.getOptions();

            final String HELP = String.join(System.lineSeparator(),
                    "Loads input GTFS feed from url or disk.",
                    "Checks files integrity, numeric type parsing and ranges as well as string format according" +
                            " to GTFS spec", "Validation results are exported to JSON file by default",
                    "If no command line is passed as argument of main method, execution parameters will be retrieved " +
                            "from .json file located at " +
                            System.getProperty("user.dir") + File.separator + "execution-parameters.json",
                    "In the case said file could not be found, gtfs-validator will use default values.",
                    "If command line is passed as argument of main method, execution parameters will be retrieved " +
                            "from this command line.",
                    "In both cases: if the value for an execution parameter could not be found, gtfs-validator will" +
                            " use defined default values ");
            System.out.println(); // blank line for legibility
            helpFormatter.printHelp(HELP, options);
            System.out.println(); // blank line for legibility

            return true;
        }
        return false;
    }
}