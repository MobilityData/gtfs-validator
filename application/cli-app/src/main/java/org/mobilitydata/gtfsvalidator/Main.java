/*
 * Copyright (c) 2019. MobilityData IO.
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

package org.mobilitydata.gtfsvalidator;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.config.DefaultConfig;
import org.mobilitydata.gtfsvalidator.usecase.ParseSingleRowForFile;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();

        try {
            final DefaultConfig config = new DefaultConfig(logger);

            final boolean fromConfigFile = args.length == 0;
            final String pathToConfigFile = "config.json";
            config.parseAllExecutionParameter(fromConfigFile, pathToConfigFile).execute(args);

            ExecParamRepository execParamRepo = config.getExecParamRepo();

            if (execParamRepo.hasExecParam(execParamRepo.HELP_KEY)) {
                printHelp(execParamRepo.getOptions());
                return;
            }

            if (!execParamRepo.hasExecParamValue(execParamRepo.URL_KEY) & !execParamRepo
                    .hasExecParamValue(execParamRepo.ZIP_KEY)) {
                logger.info("--url provided but no location to place zip (--zip option). Using default: " +
                        execParamRepo.getExecParamValue(execParamRepo.ZIP_KEY));
            }

            if (!execParamRepo.hasExecParamValue(execParamRepo.INPUT_KEY)) {
                logger.info("--input not provided. Will extract zip content in: " + execParamRepo
                        .getExecParamValue(ExecParamRepository.INPUT_KEY));
            }

            if (!execParamRepo.hasExecParamValue(execParamRepo.OUTPUT_KEY)) {
                logger.info("--output not provided. Will place execution results in: " + execParamRepo
                        .getExecParamValue(execParamRepo.OUTPUT_KEY));
            }

            if (execParamRepo.hasExecParamValue(execParamRepo.URL_KEY)) {
                logger.info("Downloading archive");
                config.downloadArchiveFromNetwork().execute(execParamRepo.getExecParamValue(execParamRepo.URL_KEY),
                        execParamRepo.getExecParamValue(execParamRepo.ZIP_KEY));
            }

            logger.info("Unzipping archive");
            config.unzipInputArchive(config.cleanOrCreatePath()
                    .execute(execParamRepo.getExecParamValue(execParamRepo.INPUT_KEY)))
                    .execute(execParamRepo.getExecParamValue(execParamRepo.ZIP_KEY));

            final List<String> filenameList = config.validateAllRequiredFilePresence().execute();

            filenameList.addAll(config.validateAllOptionalFileName().execute());

            // base validation
            filenameList.forEach(filename -> {
                config.validateHeadersForFile(filename).execute();
                config.validateAllRowLengthForFile(filename).execute();

                ParseSingleRowForFile parseSingleRowForFile = config.parseSingleRowForFile(filename);
                while (parseSingleRowForFile.hasNext()) {
                    config.validateGtfsTypes().execute(parseSingleRowForFile.execute());
                }
            });


            if (execParamRepo.hasExecParamValue(execParamRepo.PROTO_KEY)) {
                logger.info("Results are exported as proto");
            } else {
                logger.info("Results are exported as JSON by default");
            }

            logger.info("Exporting validation repo content:" + config.getValidationResult());
            config.cleanOrCreatePath().execute(execParamRepo.getExecParamValue(execParamRepo.OUTPUT_KEY));

            config.exportResultAsFile().execute(execParamRepo.getExecParamValue(execParamRepo.OUTPUT_KEY),
                    Boolean.parseBoolean(execParamRepo.getExecParamValue(execParamRepo.PROTO_KEY)));

        } catch (IOException e) {
            logger.error("An exception occurred: " + e.getMessage());
        }
        logger.info("Took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
    }

    //TODO: make a use case out of this
    private static void printHelp(Options options) {
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
    }
}
