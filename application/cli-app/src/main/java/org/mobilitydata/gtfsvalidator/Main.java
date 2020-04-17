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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();

        try {
            final DefaultConfig config = new DefaultConfig();

            final boolean fromConfigFile = args.length == 0;
            final String pathToConfigFile = "config.json";
            final String pathToDefaultConfigFile = "default-config.json";

            config.parseAllExecutionParameter(fromConfigFile, pathToConfigFile, pathToDefaultConfigFile).execute(args);

            config.downloadArchiveFromNetwork().execute();

            config.unzipInputArchive(config.cleanOrCreatePath().zipExtractTargetPath()).execute();

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

            config.cleanOrCreatePath().outputPath();

            config.exportResultAsFile().execute();

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
