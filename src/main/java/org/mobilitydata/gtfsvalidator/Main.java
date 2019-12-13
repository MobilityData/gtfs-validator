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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        final Logger logger = LogManager.getLogger();

        //TODO: configurable through command line options: url, zip path, extraction path, output path
        String url = "https://transitfeeds.com/p/mbta/64/latest/download";
        //String url = "http://gtfs.ovapi.nl/nl/gtfs-nl.zip";
        String zipInputPath = "input.zip";
        String zipExtractTargetPath = "input";
        String outputPath = "output";

        try {

            FileUtils.copyZipFromNetwork(url, zipInputPath);
            FileUtils.unzip(zipInputPath, zipExtractTargetPath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVtoProtoConverter pathwaysConverter = new CSVtoProtoConverter();
        CSVtoProtoConverter stopsConverter = new CSVtoProtoConverter();

        ProtoGTFSTypeValidator pathwayBasicValidator = new ProtoGTFSTypeValidator();

        try {

            FileUtils.cleanOrCreatePath(outputPath);

            // convert GTFS text files to .proto files on disk
            List<OccurrenceModel> conversionResult  = pathwaysConverter.convert("input/pathways.txt",
                    "output/pathways.pb",
                    PathwaysProto.pathwayCollection.newBuilder());

            conversionResult.addAll(stopsConverter.convert("input/stops.txt",
                    "output/stops.pb",
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
    }
}
