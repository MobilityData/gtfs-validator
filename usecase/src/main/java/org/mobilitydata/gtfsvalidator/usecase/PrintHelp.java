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

/**
 * Use case to print help
 */
public class PrintHelp {
    private final ExecParamRepository execParamRepo;

    /**
     * Use case to print help
     *
     * @param execParamRepo repository containing execution parameters
     */
    public PrintHelp(final ExecParamRepository execParamRepo) {
        this.execParamRepo = execParamRepo;
    }

    /**
     * Use case execution method: prints help menu if execution parameter with key HELP_KEY="help" is contained in the
     * repository provided in the constructor.
     */
    public void execute() {
        if (execParamRepo.hasExecParam(execParamRepo.HELP_KEY)) {

            Options options = execParamRepo.getOptions();

            final String HELP = String.join("\n",
                    "Loads input GTFS feed from url or disk.",
                    "Checks files integrity, numeric type parsing and ranges as well as " +
                            "string format according to GTFS spec",
                    "Validation results are exported to " +
                            "JSON file by default");
            HelpFormatter formatter = new HelpFormatter();
            System.out.println(); // blank line for legibility
            formatter.printHelp(HELP, options);
            System.out.println(); // blank line for legibility
            System.exit(0);
        }
    }
}
