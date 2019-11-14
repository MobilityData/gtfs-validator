package org.mobilitydata;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import org.mobilitydata.conversion.PathwaysConverter;
import org.mobilitydata.conversion.StopTimesConverter;
import org.mobilitydata.conversion.StopsConverter;
import org.mobilitydata.conversion.TripsConverter;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {

    public static void main(String[] args) {

        long timeBeforeMillis = System.currentTimeMillis();

        // download data source from URL and unzip
        //TODO: configurable through command line options: url, zip path, extraction path
        String url = "https://transitfeeds.com/p/mbta/64/latest/download";
        String zipInputPath = "input.zip";
        String zipExtractTargetPath = "input";

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

        // convert GTFS text files to .proto files on disk
        StopsConverter.convert();
        PathwaysConverter.convert();
        TripsConverter.convert();
        StopTimesConverter.convert();

        System.out.println("Took " + (System.currentTimeMillis() - timeBeforeMillis) + "ms");
    }
}
