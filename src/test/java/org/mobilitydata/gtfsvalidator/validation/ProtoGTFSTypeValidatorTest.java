package org.mobilitydata.gtfsvalidator.validation;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.proto.PathwaysProto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoGTFSTypeValidatorTest {

    @Test
    void validate() {
        //pathways
        ProtoGTFSTypeValidator validator = new ProtoGTFSTypeValidator();

        var pathwayCollectionBuilder = PathwaysProto.pathwayCollection.newBuilder();
        var pathwayBuilder = PathwaysProto.Pathway.newBuilder();

        // Valid data for all fields except enums as other type are validated in GTFSTypeValidationUtilsTest
        pathwayBuilder.setPathwayId("testId")
                .setFromStopId("fromTestId")
                .setToStopId("toTestId")
                .setPathwayModeValue(100)
                .setIsBidirectionalValue(101)
                .setLength(1.0f)
                .setTraversalTime(10)
                .setStairCount(5)
                .setMaxSlope(0)
                .setMinWidth(5.f)
                .setSignpostedAs("signpostedAsTest")
                .setReversedSignpostedAs("reverseSignpostedAs");

        pathwayCollectionBuilder.addPathways(pathwayBuilder);

        List<OccurrenceModel> result = validator.validate(pathwayCollectionBuilder.build());

        assertEquals(2, result.size());
        OccurrenceModel error = result.get(0);
        assertEquals("pathway_id: testId pathway_mode is 100", error.getPrefix());
        assertEquals("E013", error.getRule().getErrorId());
        error = result.get(1);
        assertEquals("pathway_id: testId is_bidirectional is 101", error.getPrefix());
        assertEquals("E013", error.getRule().getErrorId());
    }
}