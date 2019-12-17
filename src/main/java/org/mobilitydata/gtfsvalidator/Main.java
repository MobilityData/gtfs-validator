package org.mobilitydata.gtfsvalidator;

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

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.conversion.CSVtoProtoConverter;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.proto.StopsProto;
import org.mobilitydata.gtfsvalidator.util.FileUtils;
import org.mobilitydata.gtfsvalidator.validation.ProtoGTFSTypeValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();
        final Options options = new Options();

        options.addOption("u", "url", true, "URL to GTFS zipped archive");
        options.addOption("z", "zip", true, "if --url is used, where to place the downloaded archive." +
                "Otherwise, relative path pointing to a valid GTFS zipped archive on disk");
        options.addOption("i", "input", true, "Relative path where to extract the zip content");
        options.addOption("o", "output", true, "Relative path where to place output files");
        options.addOption("h", "help", false, "Print this message");

        //TODO: add configurable warning threshold for GTFS time type validation

        final CommandLineParser parser = new DefaultParser();


        try {
            final CommandLine cmd = parser.parse(options, args);

            if (args.length == 0) {
                printHelp(options);
            } else if (cmd.hasOption("h")) {
                return;
            }

            String zipInputPath = cmd.getOptionValue("z") != null ? cmd.getOptionValue("z") : System.getProperty("user.dir");
            String zipExtractTargetPath = cmd.getOptionValue("i") != null ? cmd.getOptionValue("i") : System.getProperty("user.dir") + "/input";
            String outputPath = cmd.getOptionValue("o") != null ? cmd.getOptionValue("o"): System.getProperty("user.dir") + "/output";

            if (cmd.hasOption("u") & !cmd.hasOption("z")) {
                logger.info("--url provided but no location to place zip (--zip option). Using default: " + zipInputPath);
            }

            if (!cmd.hasOption("u") & !cmd.hasOption("z")) {
                logger.info("--url and relative path to zip file(--zip option) not provided. Trying to find zip in: " + zipInputPath);
                List<String> zipList = Files.walk(Paths.get(zipInputPath))
                        .map(Path::toString)
                        .filter(f -> f.endsWith(".zip"))
                        .collect(Collectors.toUnmodifiableList());

                if (zipList.isEmpty()) {
                    logger.error("no zip file found - exiting");
                    System.exit(0);
                } else if (zipList.size() > 1) {
                    logger.error("multiple zip files found - exiting");
                    System.exit(0);
                } else {
                    logger.info("zip file found: "+ zipList.get(0));
                    zipInputPath = zipList.get(0);
                }
            } else if (!cmd.hasOption("z")) {
                zipInputPath += "/input.zip";
            }

            if (!cmd.hasOption("i")) {
                logger.info("--input not provided. Will extract zip content in: " + zipExtractTargetPath);
            }

            if (!cmd.hasOption("o")) {
                logger.info("--output not provided. Will place execution results in: " + outputPath);
            }

            if (cmd.getOptionValue("u") != null) {
                FileUtils.copyZipFromNetwork(cmd.getOptionValue("u"), zipInputPath);
            }

            FileUtils.unzip(zipInputPath, zipExtractTargetPath);

            CSVtoProtoConverter pathwaysConverter = new CSVtoProtoConverter();
            CSVtoProtoConverter stopsConverter = new CSVtoProtoConverter();

            ProtoGTFSTypeValidator pathwayBasicValidator = new ProtoGTFSTypeValidator();

            FileUtils.cleanOrCreatePath(outputPath);

            // convert GTFS text files to .proto files on disk
            List<OccurrenceModel> conversionResult  = pathwaysConverter.convert(zipExtractTargetPath + "/pathways.txt",
                    outputPath + "/pathways.pb",
                    PathwaysProto.pathwayCollection.newBuilder());

            conversionResult.addAll(stopsConverter.convert(zipExtractTargetPath + "/stops.txt",
                    outputPath + "/stops.pb",
                    StopsProto.stopCollection.newBuilder()));

            logger.info("conversion result: " + conversionResult);

            if(conversionResult.isEmpty()) {
                // validate proto files in terms of GTFS types conformance
                List<OccurrenceModel> typeValidationResult = pathwayBasicValidator.validate(
                        PathwaysProto.pathwayCollection.parseFrom(new FileInputStream("output/pathways.pb"))
                );

                logger.info("GTFS type validation result: " + typeValidationResult);
            } else {
                logger.error("ABORTED -- Warning or Errors detected at conversion step. Please fix warnings and errors and retry");
            }

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
