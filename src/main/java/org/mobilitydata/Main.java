package org.mobilitydata;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import org.mobilitydata.conversion.PathwaysConverter;
import org.mobilitydata.conversion.StopTimesConverter;
import org.mobilitydata.conversion.StopsConverter;
import org.mobilitydata.conversion.TripsConverter;

public class Main {

    public static void main(String[] args) {

        long timeBeforeMillis = System.currentTimeMillis();

        // convert GTFS text files to .proto files on disk
        StopsConverter.convert();
        PathwaysConverter.convert();
        TripsConverter.convert();
        StopTimesConverter.convert();

        System.out.println("Took " + (System.currentTimeMillis() - timeBeforeMillis) + "ms");
    }
}
