package org.mobilitydata.conversion;

import com.google.type.LatLng;
import org.mobilitydata.gtfsproto.StopsProto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class StopsConverter {

    public static void convert(){

        BufferedReader br;
        String line;

        try {
            br = new BufferedReader(new FileReader("stops.txt"));

            StopsProto.stopCollection.Builder collectionBuilder = StopsProto.stopCollection.newBuilder();

            while ((line = br.readLine()) != null){
                if(line.contains("stop_id")) //skipping headers
                    continue;

                String[] stop = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                StopsProto.Stop.Builder toAddBuilder = StopsProto.Stop.newBuilder()
                        .setId(stop[0])
                        .setCode(stop[1])
                        .setName(stop[2])
                        .setDesc(stop[3])
                        .setLatLng(
                                LatLng.newBuilder()
                                        .setLatitude(Double.parseDouble(stop[4].replace("\"", "")))
                                        .setLongitude(Double.parseDouble(stop[5].replace("\"", "")))
                                        .build())
                        .setLocationTypeValue(Integer.parseInt(stop[6]))
                        .setPlatformCode(stop[9]);

                try {
                    toAddBuilder.setWheelchairBoardingValue(Integer.parseInt(stop[8]));
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setParentStation(stop[7]);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setZoneId(stop[10]);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }

                collectionBuilder.addStops(toAddBuilder);
            }

            // Write the stop collection to disk.
            FileOutputStream output = new FileOutputStream("stops.pb");
            collectionBuilder.build().writeTo(output);
            output.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
