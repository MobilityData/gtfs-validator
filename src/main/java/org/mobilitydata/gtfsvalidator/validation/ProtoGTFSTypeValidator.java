package org.mobilitydata.gtfsvalidator.validation;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;
import org.mobilitydata.gtfsvalidator.util.GTFSTypeValidationUtils;
import org.mobilitydata.gtfsvalidator.util.RuleUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mobilitydata.gtfsvalidator.rules.ValidationRules.E013;

public class ProtoGTFSTypeValidator {

    public List<OccurrenceModel> validate(PathwaysProto.pathwayCollection pathwayCollection) {

        List<OccurrenceModel> errorAndWarningList = new ArrayList<>();

        for (PathwaysProto.Pathway pathway : pathwayCollection.getPathwaysList()) {

            String pathwayID = pathway.getPathwayId();

            // TODO: have some kind of schema to pass around at least filed names - they also exist in CSVToProtoConverter
            GTFSTypeValidationUtils.validateId(pathwayID,
                    "pathway_id",
                    pathwayID,
                    false,
                    errorAndWarningList);

            if (Strings.isNotEmpty(pathwayID)) {
                pathwayID = "pathway_id: " + pathwayID;
            } else {
                pathwayID = "pathway id: null";
            }

            GTFSTypeValidationUtils.validateId(pathwayID,
                    "from_stop_id",
                    pathway.getFromStopId(),
                    false,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateId(pathwayID,
                    "to_stop_id",
                    pathway.getToStopId(),
                    false,
                    errorAndWarningList);

            if (pathway.getPathwayMode() == PathwaysProto.Pathway.PathwayMode.UNRECOGNIZED) {
                RuleUtils.addOccurrence(E013,
                        formatOccurrencePrefix(pathwayID,
                                "pathway_mode",
                                String.valueOf(pathway.getPathwayModeValue())),
                        errorAndWarningList);
            }

            if (pathway.getIsBidirectional() == PathwaysProto.Pathway.Directionality.UNRECOGNIZED) {
                RuleUtils.addOccurrence(E013,
                        formatOccurrencePrefix(pathwayID,
                                "is_bidirectional",
                                String.valueOf(pathway.getIsBidirectionalValue())),
                        errorAndWarningList);
            }

            GTFSTypeValidationUtils.validateFloat(pathwayID,
                    "length",
                    pathway.getLength(),
                    true,
                    false,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateInteger(pathwayID,
                    "traversal_time",
                    pathway.getTraversalTime(),
                    true,
                    false,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateInteger(pathwayID,
                    "stair_count",
                    pathway.getStairCount(),
                    true,
                    true,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateFloat(pathwayID,
                    "max_slope",
                    pathway.getMaxSlope(),
                    true,
                    true,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateFloat(pathwayID,
                    "min_width",
                    pathway.getMinWidth(),
                    true,
                    false,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateText(pathwayID,
                    "signposted_as",
                    pathway.getSignpostedAs(),
                    true,
                    errorAndWarningList);

            GTFSTypeValidationUtils.validateText(pathwayID,
                    "reversed_signposted_as",
                    pathway.getReversedSignpostedAs(),
                    true,
                    errorAndWarningList);
        }

        return errorAndWarningList;

    }

    //TODO: Factor this
    private static String formatOccurrencePrefix(@NotNull String validatedEntityId,
                                                 @NotNull String fieldName,
                                                 @NotNull String rawValue) {
        return validatedEntityId + " " + fieldName + " is " + rawValue;
    }
}
