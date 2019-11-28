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
            RuleUtils.addOccurrence(E001, "File Pathways.txt", errorAndWarningList);
            return errorAndWarningList;
        }

        while (rawEntities.hasNext()) {
            Map<String, String> rawEntity = rawEntities.next();

            PathwaysProto.Pathway.Builder toAddBuilder = PathwaysProto.Pathway.newBuilder();

            String pathwayId = GTFSTypeValidationUtils.validateString("pathway_id",
                    rawEntity.get("pathway_id"),
                    false,
                    true,
                    errorAndWarningList);

            if (pathwayId != null) {
                toAddBuilder.setPathwayId(pathwayId);
            }

            String fromStopId = GTFSTypeValidationUtils.validateString("from_stop_id",
                    rawEntity.get("from_stop_id"),
                    false,
                    true,
                    errorAndWarningList);

            if (fromStopId != null)
                toAddBuilder.setFromStopId(fromStopId);

            String toStopId = GTFSTypeValidationUtils.validateString("to_stop_id",
                    rawEntity.get("to_stop_id"),
                    false,
                    true,
                    errorAndWarningList);

            if (toStopId != null)
                toAddBuilder.setToStopId(toStopId);

            Integer pathwayMode = GTFSTypeValidationUtils.parseAndValidateInteger("pathway_mode",
                    rawEntity.get("pathway_mode"),
                    false,
                    false,
                    errorAndWarningList);

            if (pathwayMode != null) {
                toAddBuilder.setPathwayModeValue(pathwayMode);
            }

            Integer isBidirectional = GTFSTypeValidationUtils.parseAndValidateInteger("is_bidirectional",
                    rawEntity.get("is_bidirectional"),
                    false,
                    false,
                    errorAndWarningList);

            if (isBidirectional != null) {
                toAddBuilder.setIsBidirectionalValue(isBidirectional);
            }

            Float length = GTFSTypeValidationUtils.parseAndValidateFloat("length",
                    rawEntity.get("length"),
                    true,
                    false,
                    errorAndWarningList);

            if (length != null) {
                toAddBuilder.setLength(length);
            }

            Integer traversalTime = GTFSTypeValidationUtils.parseAndValidateInteger("traversal_time",
                    rawEntity.get("traversal_time"),
                    true,
                    false,
                    errorAndWarningList);

            if (traversalTime != null) {
                toAddBuilder.setTraversalTime(traversalTime);
            }

            Integer stairCount = GTFSTypeValidationUtils.parseAndValidateInteger("stair_count",
                    rawEntity.get("stair_count"),
                    true,
                    true,
                    errorAndWarningList);

            if (stairCount != null) {
                toAddBuilder.setStairCount(stairCount);
            }

            Float maxSlope = GTFSTypeValidationUtils.parseAndValidateFloat("max_slope",
                    rawEntity.get("max_slope"),
                    true,
                    true,
                    errorAndWarningList);

            if (maxSlope != null) {
                toAddBuilder.setMaxSlope(maxSlope);
            }

            Float minWidth = GTFSTypeValidationUtils.parseAndValidateFloat("min_width",
                    rawEntity.get("min_width"),
                    true,
                    false,
                    errorAndWarningList);

            if (minWidth != null) {
                toAddBuilder.setMinWidth(minWidth);
            }

            String signpostedAs = GTFSTypeValidationUtils.validateString("signposted_as",
                    rawEntity.get("signposted_as"),
                    true,
                    false,
                    errorAndWarningList);

            toAddBuilder.setSignpostedAs(signpostedAs);

            String reversedSignpostedAs = GTFSTypeValidationUtils.validateString("reversed_signposted_as",
                    rawEntity.get("reversed_signposted_as"),
                    true,
                    false,
                    errorAndWarningList);

            protoCollectionBuilder.addPathways(toAddBuilder);
        }

        // Write the pathway collection to disk.
        FileOutputStream output = new FileOutputStream(protoBinOutFilePath);
        protoCollectionBuilder.build().writeTo(output);
        output.close();

        return errorAndWarningList;
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
