package org.mobilitydata.gtfsvalidator;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.conversion.CSVtoProtoConverter;

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
            if(Files.exists(out)){
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

        System.out.println("Took " + (System.nanoTime() - startTime) / 100000 + "ms");
    }
}
