package org.mobilitydata.gtfsvalidator.conversion;

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

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.type.LatLng;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.proto.StopTimesProto;
import org.mobilitydata.gtfsvalidator.proto.StopsProto;
import org.mobilitydata.gtfsvalidator.proto.TripsProto;
import org.mobilitydata.gtfsvalidator.util.RuleUtils;
import org.mobilitydata.gtfsvalidator.util.GTFSTypeValidationUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mobilitydata.gtfsvalidator.rules.ValidationRules.*;

public class CSVtoProtoConverter {

    public List<OccurrenceModel> convert(String csvInFilePath, String protoBinOutFilePath,
                                         PathwaysProto.pathwayCollection.Builder protoCollectionBuilder) throws IOException {

        List<OccurrenceModel> errorAndWarningList = new ArrayList<>();

        MappingIterator<Map<String, String>> rawEntities;
        try {
            rawEntities = getRows(csvInFilePath);
        } catch (IOException e) {
            RuleUtils.addOccurrence(E001, "File pathways.txt", errorAndWarningList);
            return errorAndWarningList;
        }

        while (rawEntities.hasNext()) {
            Map<String, String> rawEntity = rawEntities.next();

            PathwaysProto.Pathway.Builder toAddBuilder = PathwaysProto.Pathway.newBuilder();

            String pathwayId = rawEntity.get("pathway_id");

            if (pathwayId != null) {
                toAddBuilder.setPathwayId(pathwayId);
                pathwayId = "pathway_id: " + pathwayId;
            } else {
                pathwayId = "pathway_id: null";
            }

            String fromStopId = rawEntity.get("from_stop_id");

            if (fromStopId != null)
                toAddBuilder.setFromStopId(fromStopId);

            String toStopId = rawEntity.get("to_stop_id");

            if (toStopId != null)
                toAddBuilder.setToStopId(toStopId);

            toAddBuilder.setPathwayModeValue(GTFSTypeValidationUtils.parseInteger(pathwayId,
                    "pathway_mode",
                    rawEntity.get("pathway_mode"),
                    errorAndWarningList));

            toAddBuilder.setIsBidirectionalValue(GTFSTypeValidationUtils.parseInteger(pathwayId,
                    "is_bidirectional",
                    rawEntity.get("is_bidirectional"),
                    errorAndWarningList));

            toAddBuilder.setLength(GTFSTypeValidationUtils.parseFloat(pathwayId,
                    "length",
                    rawEntity.get("length"),
                    errorAndWarningList));

            toAddBuilder.setTraversalTime(GTFSTypeValidationUtils.parseInteger(pathwayId,
                    "traversal_time",
                    rawEntity.get("traversal_time"),
                    errorAndWarningList));

            toAddBuilder.setStairCount(GTFSTypeValidationUtils.parseInteger(pathwayId,
                    "stair_count",
                    rawEntity.get("stair_count"),
                    errorAndWarningList));

            toAddBuilder.setMaxSlope(GTFSTypeValidationUtils.parseFloat(pathwayId,
                    "max_slope",
                    rawEntity.get("max_slope"),
                    errorAndWarningList));

            toAddBuilder.setMinWidth(GTFSTypeValidationUtils.parseFloat(pathwayId,
                    "min_width",
                    rawEntity.get("min_width"),
                    errorAndWarningList));

            String signpostedAs = rawEntity.get("signposted_as");

            if (signpostedAs != null) {
                toAddBuilder.setSignpostedAs(signpostedAs);
            }

            String reversedSignpostedAs = rawEntity.get("reversed_signposted_as");

            if (reversedSignpostedAs != null) {
                toAddBuilder.setReversedSignpostedAs(reversedSignpostedAs);
            }

            protoCollectionBuilder.addPathways(toAddBuilder);
        }

        // Write the pathway collection to disk.
        FileOutputStream output = new FileOutputStream(protoBinOutFilePath);
        protoCollectionBuilder.build().writeTo(output);
        output.close();

        return errorAndWarningList;
    }

    public List<OccurrenceModel> convert(String csvInFilePath, String protoBinOutFilePath,
                        StopTimesProto.stopTimeCollection.Builder protoCollectionBuilder)
            throws IOException {

        List<OccurrenceModel> errorAndWarningList = new ArrayList<>();

        MappingIterator<Map<String, String>> rawEntities;

        try {
            rawEntities = getRows(csvInFilePath);
        } catch (IOException e) {
            RuleUtils.addOccurrence(E001, "File stop_times.txt", errorAndWarningList);
            return errorAndWarningList;
        }


        while (rawEntities.hasNext()) {
            Map<String, String> rawEntity = rawEntities.next();

            StopTimesProto.StopTime.Builder toAddBuilder = StopTimesProto.StopTime.newBuilder();


            protoCollectionBuilder.addStopTimes(toAddBuilder);
        }

        // Write the stop collection to disk.
        FileOutputStream output = new FileOutputStream(protoBinOutFilePath);
        protoCollectionBuilder.build().writeTo(output);
        output.close();

        return errorAndWarningList;
    }

    public List<OccurrenceModel> convert(String csvInFilePath, String protoBinOutFilePath,
                        StopsProto.stopCollection.Builder protoCollectionBuilder)
            throws IOException {
        List<OccurrenceModel> errorAndWarningList = new ArrayList<>();

        MappingIterator<Map<String, String>> rawEntities;

        try {
            rawEntities = getRows(csvInFilePath);
        } catch (IOException e) {
            RuleUtils.addOccurrence(E001, "File stops.txt", errorAndWarningList);
            return errorAndWarningList;
        }


        while (rawEntities.hasNext()) {
            Map<String, String> rawEntity = rawEntities.next();

            StopsProto.Stop.Builder toAddBuilder = StopsProto.Stop.newBuilder();

            String stopId = rawEntity.get("stop_id");

            if (stopId != null) {
                toAddBuilder.setId(stopId);
                stopId = "stop_id: " + stopId;
            } else {
                stopId = "stop_id: null";
            }

            String stopCode = rawEntity.get("stop_code");

            if (stopCode != null) {
                toAddBuilder.setCode(stopCode);
            }

            String stopName = rawEntity.get("stop_name");

            if (stopName != null) {
                toAddBuilder.setName(stopName);
            }

            String stopDesc = rawEntity.get("stop_desc");

            if (stopDesc != null) {
                toAddBuilder.setDesc(stopDesc);
            }

            float stopLatitude = GTFSTypeValidationUtils.parseFloat(stopId,
                    "stop_lat",
                    rawEntity.get("stop_lat"),
                    errorAndWarningList);

            float stopLongitude = GTFSTypeValidationUtils.parseFloat(stopId,
                    "stop_lon",
                    rawEntity.get("stop_lon"),
                    errorAndWarningList);

            toAddBuilder.setLatLng(LatLng.newBuilder().setLatitude(stopLatitude).setLongitude(stopLongitude).build());

            String zoneId = rawEntity.get("zone_id");

            if (zoneId != null) {
                toAddBuilder.setZoneId(zoneId);
            }

            String stopUrl = rawEntity.get("stop_url");

            if (stopUrl != null) {
                toAddBuilder.setUrl(stopUrl);
            }

            toAddBuilder.setLocationTypeValue(GTFSTypeValidationUtils.parseInteger(stopId,
                    "location_type",
                    rawEntity.get("location_type"),
                    errorAndWarningList));


            String parentStationId = rawEntity.get("parent_station");

            if (parentStationId != null) {
                toAddBuilder.setParentStation(parentStationId);
            }

            String timezone = rawEntity.get("stop_timezone");

            if (timezone != null) {
                toAddBuilder.setTimezone(timezone);
            }

            toAddBuilder.setWheelchairBoardingValue(GTFSTypeValidationUtils.parseInteger(stopId,
                    "wheelchair_boarding",
                    rawEntity.get("wheelchair_boarding"),
                    errorAndWarningList));

            String levelId = rawEntity.get("level_id");

            if (levelId != null) {
                toAddBuilder.setLevelId(levelId);
            }

            String platformCode = rawEntity.get("platform_code");

            if (platformCode != null) {
                toAddBuilder.setPlatformCode(platformCode);
            }

            protoCollectionBuilder.addStops(toAddBuilder);
        }

        // Write the stop collection to disk.
        FileOutputStream output = new FileOutputStream(protoBinOutFilePath);
        protoCollectionBuilder.build().writeTo(output);
        output.close();

        return errorAndWarningList;
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
