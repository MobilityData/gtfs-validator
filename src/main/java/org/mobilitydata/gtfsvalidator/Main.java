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

import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.conversion.CSVtoProtoConverter;
import org.mobilitydata.gtfsvalidator.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        long startTime = System.nanoTime();

        // download data source from URL and unzip
        //TODO: configurable through command line options: url, zip path, extraction path, output path
        String url = "https://transitfeeds.com/p/mbta/64/latest/download";
        String zipInputPath = "input.zip";
        String zipExtractTargetPath = "input";
        String outputPath = "output";

        try {
            Files.copy(
                    new URL(url).openStream(),
                    Paths.get(zipInputPath),
                    StandardCopyOption.REPLACE_EXISTING
            );

            ZipUtils.unzip(zipInputPath, zipExtractTargetPath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVtoProtoConverter pathwaysConverter = new CSVtoProtoConverter();

        try {

            Path out = Path.of(outputPath);

            // to empty any already existing directory
            if (Files.exists(out)) {
                //noinspection ResultOfMethodCallIgnored
                Files.walk(out).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                Files.createDirectory(out);
            } else {
                Files.createDirectory(out);
            }

            // convert GTFS text files to .proto files on disk
            pathwaysConverter.convert("input/pathways.txt",
                    "output/pathways.pb",
                    PathwaysProto.pathwayCollection.newBuilder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Took " + (System.nanoTime() - startTime) / 1000000 + "ms");
    }
}
