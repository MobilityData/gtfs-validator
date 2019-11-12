
package org.mobilitydata.conversion;

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
            br = new BufferedReader(new FileReader("trips.txt"));

            TripsProto.tripCollection.Builder collectionBuilder = TripsProto.tripCollection.newBuilder();

            while ((line = br.readLine()) != null){
                if(line.contains("trip_id")) //skipping headers - TODO: there must be a better way
                    continue;

                String[] pathway = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                TripsProto.Trip.Builder toAddBuilder = TripsProto.Trip.newBuilder()
                        .setRouteId(pathway[0])
                        .setServiceId(pathway[1])
                        .setTripId(pathway[2]);

                try {
                    toAddBuilder.setTripHeadsign(pathway[3]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTripShortName(pathway[5]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTripShortName(pathway[6]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setDirectionIdValue(Integer.parseInt(pathway[7]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setBlockId(pathway[8]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setShapeId(pathway[9]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setWheelchairAccessibleValue(Integer.parseInt(pathway[10]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    toAddBuilder.setWheelchairAccessibleValue(0);
                }

                try {
                    toAddBuilder.setBikesAllowedValue(Integer.parseInt(pathway[11]));
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
