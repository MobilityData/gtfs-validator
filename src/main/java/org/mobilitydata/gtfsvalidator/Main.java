package org.mobilitydata.gtfsvalidator;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
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

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.conversion.CSVtoProtoConverter;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.proto.StopsProto;
import org.mobilitydata.gtfsvalidator.util.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();
        final Options options = new Options();

        options.addOption("u", "url", true, "URL to GTFS zipped archive");
        options.addOption("z", "zip", true, "if -url is used, where to place the downloaded archive," +
                "otherwise must point to a valid GTFS zipped archive on disk");
        options.addOption("i", "input", true, "Relative path where to extract the zip content");
        options.addOption("o", "output", true, "Relative path where to place output files");
        options.addOption("h", "help", false, "Print this message");

        //TODO: add configurable warning threshold for GTFS time type validation

        final CommandLineParser parser = new DefaultParser();


        try {
            final CommandLine cmd = parser.parse(options, args);

            if (args.length == 0) {
                System.out.println("This program can not be runned without arguments. For help, run binary file with -h argument");
                System.exit(0);
            }
            if (cmd.hasOption("h")) {
                printHelp(options);
                return;
            }

            String url = cmd.getOptionValue("u");
            String zipInputPath = cmd.getOptionValue("z");
            String zipExtractTargetPath = cmd.getOptionValue("i");
            String outputPath = cmd.getOptionValue("o");

            if (cmd.hasOption("u")) {
                FileUtils.copyZipFromNetwork(url, zipInputPath);
            }

            FileUtils.unzip(zipInputPath, zipExtractTargetPath);

            CSVtoProtoConverter pathwaysConverter = new CSVtoProtoConverter();
            CSVtoProtoConverter stopsConverter = new CSVtoProtoConverter();

            FileUtils.cleanOrCreatePath(outputPath);

            // convert GTFS text files to .proto files on disk
            List<OccurrenceModel> result  = pathwaysConverter.convert(zipExtractTargetPath + "/pathways.txt",
                    outputPath + "/pathways.pb",
                    PathwaysProto.pathwayCollection.newBuilder());

            result.addAll(stopsConverter.convert(zipExtractTargetPath + "/stops.txt",
                    outputPath + "/stops.pb",
                    StopsProto.stopCollection.newBuilder()));

            logger.debug("validation result: " + result);

        } catch (ParseException e) {

            logger.error("Could not parse command line arguments: " + e.getMessage());
        } catch (IOException e) {
            logger.error("An exception occurred: " + e.getMessage());
        }

        logger.info("Took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
    }

    private static void printHelp(Options options) {
        final String HELP = String.join("\n",
                "Loads input GTFS feed from url or disk.",
                "Checks files integrity, and converts CSV to proto file on disk");
        HelpFormatter formatter = new HelpFormatter();
        System.out.println(); // blank line for legibility
        formatter.printHelp(HELP, options);
        System.out.println(); // blank line for legibility
    }

}
