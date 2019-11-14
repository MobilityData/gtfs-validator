
package org.mobilitydata.conversion;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import org.mobilitydata.gtfsproto.TripsProto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class TripsConverter {

    public static void convert(){

        BufferedReader br;
        String line;

        try {
            br = new BufferedReader(new FileReader("input/trips.txt"));

            TripsProto.tripCollection.Builder collectionBuilder = TripsProto.tripCollection.newBuilder();

            while ((line = br.readLine()) != null){
                if(line.contains("trip_id")) //skipping headers - TODO: there must be a better way
                    continue;

                String[] trip = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                TripsProto.Trip.Builder toAddBuilder = TripsProto.Trip.newBuilder()
                        .setRouteId(trip[0])
                        .setServiceId(trip[1])
                        .setTripId(trip[2]);

                try {
                    toAddBuilder.setTripHeadsign(trip[3]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTripShortName(trip[5]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTripShortName(trip[6]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setDirectionIdValue(Integer.parseInt(trip[7]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setBlockId(trip[8]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setShapeId(trip[9]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setWheelchairAccessibleValue(Integer.parseInt(trip[10]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    toAddBuilder.setWheelchairAccessibleValue(0);
                }

                try {
                    toAddBuilder.setBikesAllowedValue(Integer.parseInt(trip[11]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    toAddBuilder.setBikesAllowedValue(0);
                }

                collectionBuilder.addTrips(toAddBuilder);
            }

            // Write the pathway collection to disk.
            FileOutputStream output = new FileOutputStream("trips.pb");
            collectionBuilder.build().writeTo(output);
            output.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
