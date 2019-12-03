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

            String pathwayId = GTFSTypeValidationUtils.validateId("pathway_id",
                    rawEntity.get("pathway_id"),
                    false,
                    errorAndWarningList);

            if (pathwayId != null) {
                toAddBuilder.setPathwayId(pathwayId);
            } else {
                pathwayId = "pathway_id: null";
            }

            pathwayId = "pathway_id: " + pathwayId;

            String fromStopId = GTFSTypeValidationUtils.validateId("from_stop_id",
                    rawEntity.get("from_stop_id"),
                    false,
                    errorAndWarningList);

            if (fromStopId != null)
                toAddBuilder.setFromStopId(fromStopId);

            String toStopId = GTFSTypeValidationUtils.validateId("to_stop_id",
                    rawEntity.get("to_stop_id"),
                    false,
                    errorAndWarningList);

            if (toStopId != null)
                toAddBuilder.setToStopId(toStopId);

            Integer pathwayMode = GTFSTypeValidationUtils.parseAndValidateInteger(pathwayId,
                    "pathway_mode",
                    rawEntity.get("pathway_mode"),
                    false,
                    false,
                    errorAndWarningList);

            if (pathwayMode != null) {
                toAddBuilder.setPathwayModeValue(pathwayMode);
            }

            Integer isBidirectional = GTFSTypeValidationUtils.parseAndValidateInteger(pathwayId,
                    "is_bidirectional",
                    rawEntity.get("is_bidirectional"),
                    false,
                    false,
                    errorAndWarningList);

            if (isBidirectional != null) {
                toAddBuilder.setIsBidirectionalValue(isBidirectional);
            }

            Float length = GTFSTypeValidationUtils.parseAndValidateFloat(pathwayId,
                    "length",
                    rawEntity.get("length"),
                    true,
                    false,
                    errorAndWarningList);

            if (length != null) {
                toAddBuilder.setLength(length);
            }

            Integer traversalTime = GTFSTypeValidationUtils.parseAndValidateInteger(pathwayId,
                    "traversal_time",
                    rawEntity.get("traversal_time"),
                    true,
                    false,
                    errorAndWarningList);

            if (traversalTime != null) {
                toAddBuilder.setTraversalTime(traversalTime);
            }

            Integer stairCount = GTFSTypeValidationUtils.parseAndValidateInteger(pathwayId,
                    "stair_count",
                    rawEntity.get("stair_count"),
                    true,
                    true,
                    errorAndWarningList);

            if (stairCount != null) {
                toAddBuilder.setStairCount(stairCount);
            }

            Float maxSlope = GTFSTypeValidationUtils.parseAndValidateFloat(pathwayId,
                    "max_slope",
                    rawEntity.get("max_slope"),
                    true,
                    true,
                    errorAndWarningList);

            if (maxSlope != null) {
                toAddBuilder.setMaxSlope(maxSlope);
            }

            Float minWidth = GTFSTypeValidationUtils.parseAndValidateFloat(pathwayId,
                    "min_width",
                    rawEntity.get("min_width"),
                    true,
                    false,
                    errorAndWarningList);

            if (minWidth != null) {
                toAddBuilder.setMinWidth(minWidth);
            }

            String signpostedAs = GTFSTypeValidationUtils.validateText(pathwayId,
                    "signposted_as",
                    rawEntity.get("signposted_as"),
                    true,
                    errorAndWarningList);

            toAddBuilder.setSignpostedAs(signpostedAs);

            String reversedSignpostedAs = GTFSTypeValidationUtils.validateText(pathwayId,
                    "reversed_signposted_as",
                    rawEntity.get("reversed_signposted_as"),
                    true,
                    errorAndWarningList);

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

            String stopId = GTFSTypeValidationUtils.validateId("stop_id",
                    rawEntity.get("stop_id"),
                    false,
                    errorAndWarningList);

            if (stopId != null) {
                toAddBuilder.setId(stopId);
            }

            String stopCode = GTFSTypeValidationUtils.validateText("stop_code",
                    rawEntity.get("stop_code"),
                    true,
                    errorAndWarningList);

            if (stopCode != null) {
                toAddBuilder.setCode(stopCode);
            }

            String stopName = GTFSTypeValidationUtils.validateText("stop_name",
                    rawEntity.get("stop_name"),
                    true,
                    errorAndWarningList);

            if (stopName != null) {
                toAddBuilder.setName(stopName);
            }

            String stopDesc = GTFSTypeValidationUtils.validateText("stop_desc",
                    rawEntity.get("stop_desc"),
                    true,
                    errorAndWarningList);

            if (stopDesc != null) {
                toAddBuilder.setDesc(stopDesc);
            }

            Float stopLatitude = GTFSTypeValidationUtils.parseAndValidateLatitude("stop_lat",
                    rawEntity.get("stop_lat"),
                    true,
                    errorAndWarningList);

            Float stopLongitude = GTFSTypeValidationUtils.parseAndValidateLongitude("stop_lon",
                    rawEntity.get("stop_lon"),
                    true,
                    errorAndWarningList);

            if (stopLatitude != null && stopLongitude != null) {
                toAddBuilder.setLatLng(LatLng.newBuilder().setLatitude(stopLatitude).setLongitude(stopLongitude).build());
            }

            String zoneId = GTFSTypeValidationUtils.validateId("zone_id",
                    rawEntity.get("zone_id"),
                    true,
                    errorAndWarningList);

            if (zoneId != null) {
                toAddBuilder.setZoneId(zoneId);
            }

            String stopUrl = GTFSTypeValidationUtils.validateUrl("stop_url",
                    rawEntity.get("stop_url"),
                    true,
                    errorAndWarningList);

            if (stopUrl != null) {
                toAddBuilder.setUrl(stopUrl);
            }

            Integer locationType = GTFSTypeValidationUtils.parseAndValidateInteger("location_type",
                    rawEntity.get("location_type"),
                    true,
                    false,
                    errorAndWarningList);

            if (locationType != null) {
                toAddBuilder.setLocationTypeValue(locationType);
            }

            String parentStationId = GTFSTypeValidationUtils.validateId("parent_station",
                    rawEntity.get("parent_station"),
                    true,
                    errorAndWarningList);

            if (parentStationId != null) {
                toAddBuilder.setParentStation(parentStationId);
            }

            String timezone = GTFSTypeValidationUtils.parseAndValidateTimeZone("stop_timezone",
                    rawEntity.get("stop_timezone"),
                    errorAndWarningList);

            if (timezone != null) {
                toAddBuilder.setTimezone(timezone);
            }

            Integer wheelchairBoarding = GTFSTypeValidationUtils.parseAndValidateInteger("wheelchair_boarding",
                    rawEntity.get("wheelchair_boarding"),
                    true,
                    false,
                    errorAndWarningList);

            if (wheelchairBoarding != null) {
                toAddBuilder.setWheelchairBoardingValue(wheelchairBoarding);
            }

            String levelId = GTFSTypeValidationUtils.validateId("level_id",
                    rawEntity.get("level_id"),
                    true,
                    errorAndWarningList);

            if (levelId != null) {
                toAddBuilder.setLevelId(levelId);
            }

            String platformCode = GTFSTypeValidationUtils.validateText("platform_code",
                    rawEntity.get("platform_code"),
                    true,
                    errorAndWarningList);

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
