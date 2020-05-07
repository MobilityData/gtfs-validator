package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FareRuleTest {

    @Test
    void createFareRuleWithNullFareIdShouldGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        //noinspection ConstantConditions to avoid lint
        underTest.fareId(null);
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_rules.txt", notice.getFilename());
        assertEquals("fare_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareRuleWithValidNonNullValuesShouldNotGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        underTest.fareId("fare id")
                .routeId("route id")
                .originId("origin id")
                .destinationId("destination id")
                .containsId("contains id");
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof FareRule);
    }

    @Test
    void createFareRuleWithValidNullValuesShouldNotGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        underTest.fareId("fare id");
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof FareRule);
    }
}