package org.mobilitydata.gtfsvalidator.conversion;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.proto.StopTimesProto;
import org.mobilitydata.gtfsvalidator.proto.StopsProto;
import org.mobilitydata.gtfsvalidator.proto.TripsProto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class CSVtoProtoConverter {

    public void convert(String csvInFilePath, String protoBinOutFilePath,
                        PathwaysProto.pathwayCollection.Builder protoCollectionBuilder)
            throws IOException {

        var it = getRows(csvInFilePath);

        while (it.hasNext()){
            Map<String, String> rowAsMap = it.next();

            PathwaysProto.Pathway.Builder toAddBuilder = PathwaysProto.Pathway.newBuilder();

            toAddBuilder.setPathwayId(rowAsMap.get("pathway_id"));
            toAddBuilder.setFromStopId(rowAsMap.get("from_stop_id"));
            toAddBuilder.setToStopId(rowAsMap.get("to_stop_id"));

            toAddBuilder.setIsBidirectionalValue(Integer.parseInt(rowAsMap.get("is_bidirectional")));

            try {
                //TODO: mandatory but mtba dataset don't provide them
                toAddBuilder.setPathwayModeValue(Integer.parseInt(rowAsMap.get("pathway_mode")));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try{
                toAddBuilder.setLength(Float.parseFloat(rowAsMap.get("length")));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setTraversalTime(Integer.parseInt(rowAsMap.get("traversal_time")));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setStairCount(Integer.parseInt(rowAsMap.get("stair_count")));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setMaxSlope(Float.parseFloat(rowAsMap.get("max_slope")));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setMinWidth(Float.parseFloat(rowAsMap.get("min_width")));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setSignpostedAs(rowAsMap.get("signposted_as"));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            try{
                toAddBuilder.setReversedSignpostedAs(rowAsMap.get("reversed_signposted_as"));

            } catch (NumberFormatException e) {
                //TODO: collect errors
                e.printStackTrace();
            }
            catch (NullPointerException e){
                //not an error - optional value not provided
                e.printStackTrace();
            }

            protoCollectionBuilder.addPathways(toAddBuilder);
        }

        // Write the pathway collection to disk.
        FileOutputStream output = new FileOutputStream(protoBinOutFilePath);
        protoCollectionBuilder.build().writeTo(output);
        output.close();
    }

    public void convert(String csvInFilePath, String protoBinOutFilePath,
                        StopTimesProto.stopTimeCollection.Builder protoCollectionBuilder)
            throws IOException {
        //TODO
    }

    public void convert(String csvInFilePath, String protoBinOutFilePath,
                        StopsProto.stopCollection.Builder protoCollectionBuilder)
            throws IOException {
        //TODO
    }

    public void convert(String csvInFilePath, String protoBinOutFilePath,
                        TripsProto.tripCollection.Builder protoCollectionBuilder)
            throws IOException {
        //TODO
    }

    private MappingIterator<Map<String, String>> getRows(String csvInFilePath) throws IOException {
        //use jackson to open the file and see what API we have
        File csvFile = new File(csvInFilePath);
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return mapper.readerFor(Map.class)
                .with(schema)
                .readValues(csvFile);

    }
}
