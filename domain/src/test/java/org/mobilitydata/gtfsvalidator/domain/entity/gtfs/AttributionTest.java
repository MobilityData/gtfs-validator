package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IllegalFieldValueCombination;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttributionTest {

    @Test
    void createAttributionWithNullOrganizationNameShouldGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        //noinspection ConstantConditions to avoid lint
        final EntityBuildResult<?> entityBuildResult = underTest.organizationName(null)
                .isProducer(1)
                .build();
        final String entityId = "null;null;null;null;null;true;false;false;null;null;null";
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("attributions.txt", notice.getFilename());
        assertEquals("organization_name", notice.getFieldName());
        assertEquals(entityId, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAttributionWithInvalidIsProducerShouldGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.organizationName("organization name")
                .isProducer(4)
                .isAuthority(1)
                .build();
        final String entityId = "null;null;null;null;organization name;false;false;true;null;null;null";
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("attributions.txt", notice.getFilename());
        assertEquals("is_producer", notice.getFieldName());
        assertEquals(entityId, notice.getEntityId());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, notice.getRangeMax());
        assertEquals(4, notice.getActualValue());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAttributionWithInvalidIsOperatorShouldGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.organizationName("organization name")
                .isProducer(1)
                .isOperator(4)
                .build();
        final String entityId = "null;null;null;null;organization name;true;false;false;null;null;null";
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("attributions.txt", notice.getFilename());
        assertEquals("is_operator", notice.getFieldName());
        assertEquals(entityId, notice.getEntityId());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, notice.getRangeMax());
        assertEquals(4, notice.getActualValue());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAttributionWithInvalidIsAuthorityShouldGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.organizationName("organization name")
                .isProducer(1)
                .isAuthority(4)
                .build();
        final String entityId = "null;null;null;null;organization name;true;false;false;null;null;null";
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("attributions.txt", notice.getFilename());
        assertEquals("is_authority", notice.getFieldName());
        assertEquals(entityId, notice.getEntityId());
        assertEquals(0, notice.getRangeMin());
        assertEquals(1, notice.getRangeMax());
        assertEquals(4, notice.getActualValue());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAttributionWithIllegalCombinationOfRoleFieldShouldGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.organizationName("organization name")
                .isProducer(0)
                .isAuthority(0)
                .isOperator(0)
                .build();
        final String entityId = "null;null;null;null;organization name;false;false;false;null;null;null";
        assertTrue(entityBuildResult.getData() instanceof List);
        //noinspection unchecked to avoid lint
        final List<IllegalFieldValueCombination> noticeCollection =
                (List<IllegalFieldValueCombination>) entityBuildResult.getData();
        final IllegalFieldValueCombination notice = noticeCollection.get(0);

        assertEquals("attributions.txt", notice.getFilename());
        assertEquals("is_producer", notice.getFieldName());
        assertEquals(entityId, notice.getEntityId());
        assertEquals("is_authority; is_operator", notice.getConflictingFieldName());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createAttributionWithValidValueShouldNotGenerateNotice() {
        final Attribution.AttributionBuilder underTest = new Attribution.AttributionBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.organizationName("organization name")
                .isProducer(0)
                .isOperator(1)
                .isAuthority(0)
                .build();
        assertTrue(entityBuildResult.getData() instanceof Attribution);
        final Attribution attribution = (Attribution) entityBuildResult.getData();

        assertNull(attribution.getAttributionId());
        assertNull(attribution.getAgencyId());
        assertNull(attribution.getRouteId());
        assertNull(attribution.getTripId());
        assertEquals("organization name", attribution.getOrganizationName());
        assertFalse(attribution.isProducer());
        assertTrue(attribution.isOperator());
        assertFalse(attribution.isAuthority());
        assertNull(attribution.getAttributionUrl());
        assertNull(attribution.getAttributionEmail());
        assertNull(attribution.getAttributionPhone());
    }
}