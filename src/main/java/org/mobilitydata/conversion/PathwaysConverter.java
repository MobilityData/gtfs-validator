
package org.mobilitydata.conversion;

import org.mobilitydata.gtfsproto.PathwaysProto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class PathwaysConverter {

    public static void convert(){

        BufferedReader br;
        String line;

        try {
            br = new BufferedReader(new FileReader("pathways.txt"));

            PathwaysProto.pathwayCollection.Builder collectionBuilder = PathwaysProto.pathwayCollection.newBuilder();

            while ((line = br.readLine()) != null){
                if(line.contains("pathway_id")) //skipping headers - TODO: there must be a better way
                    continue;

                String[] pathway = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                PathwaysProto.Pathway.Builder toAddBuilder = PathwaysProto.Pathway.newBuilder()
                        .setPathwayId(pathway[0])
                        .setFromStopId(pathway[1])
                        .setToStopId(pathway[2])

                        .setIsBidirectionalValue(Integer.parseInt(pathway[4]));

                try {
                    //TODO: mandatory but mtba dataset don't provide them
                    toAddBuilder.setPathwayModeValue(Integer.parseInt(pathway[3]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setLength(Float.parseFloat(pathway[5]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setTraversalTime(Integer.parseInt(pathway[6]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setStairCount(Integer.parseInt(pathway[7]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setMaxSlope(Float.parseFloat(pathway[8]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setMinWidth(Float.parseFloat(pathway[9]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setSignpostedAs(pathway[10]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                try {
                    toAddBuilder.setReversedSignpostedAs(pathway[11]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    //e.printStackTrace();
                }

                collectionBuilder.addPathways(toAddBuilder);
            }

            // Write the pathway collection to disk.
            FileOutputStream output = new FileOutputStream("pathways.pb");
            collectionBuilder.build().writeTo(output);
            output.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
