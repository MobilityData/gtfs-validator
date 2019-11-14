package org.mobilitydata.conversion;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import org.mobilitydata.gtfsproto.StopTimesProto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class StopTimesConverter {

    public static void convert(){

        BufferedReader br;
        String line;

        try {
            br = new BufferedReader(new FileReader("input/stop_times.txt"));

            StopTimesProto.stopTimeCollection.Builder collectionBuilder = StopTimesProto.stopTimeCollection.newBuilder();

            while ((line = br.readLine()) != null){
                if(line.contains("trip_id")) //skipping headers
                    continue;

                String[] stopTime = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                StopTimesProto.StopTime.Builder toAddBuilder = StopTimesProto.StopTime.newBuilder()
                        .setTripId(stopTime[0])
                        .setStopId(stopTime[3])
                        .setStopSequence(Integer.parseInt(stopTime[4]));

                try {
                    toAddBuilder.setDepartureTime(stopTime[1]);
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setDepartureTime(stopTime[2]);
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setStopHeadsign(stopTime[5]);
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setPickupTypeValue(Integer.parseInt(stopTime[6]));
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setDropOffTypeValue(Integer.parseInt(stopTime[7]));
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setShapeDistTraveled(Float.parseFloat(stopTime[8]));
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTimepointValue(Integer.parseInt(stopTime[9]));
                } catch (NumberFormatException  | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                collectionBuilder.addStopTimes(toAddBuilder);
            }

            // Write the stop collection to disk.
            FileOutputStream output = new FileOutputStream("stop_times.pb");
            collectionBuilder.build().writeTo(output);
            output.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
