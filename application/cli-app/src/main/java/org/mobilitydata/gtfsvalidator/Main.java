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
            final String pathToExecParamFile = "execution-parameters.json";
            config.parseAllExecutionParameter(fromConfigFile, pathToExecParamFile).execute(args);

            ExecParamRepository execParamRepo = config.getExecParamRepo();

            // use case will inspect parameters and decide if help menu should be displayed or not
            config.printHelp().execute();

            // use case will inspect parameters and display relevant information about the validation execution process
            config.logExecutionInfo().execute();

            // use case will inspect parameters and decide if GTFS dataset should be downloaded or not
            config.downloadArchiveFromNetwork().execute();

            config.unzipInputArchive(
                    config.cleanOrCreatePath().execute(execParamRepo.getExecParamValue(execParamRepo.INPUT_KEY)))
                    .execute();

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

            config.cleanOrCreatePath().execute(execParamRepo.getExecParamValue(execParamRepo.OUTPUT_KEY));

            config.exportResultAsFile().execute();

        } catch (IOException e) {
            logger.error("An exception occurred: " + e);
        }
        logger.info("Took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
    }
}
