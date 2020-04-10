/*
 * Copyright (c) 2020. MobilityData IO.
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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TranslationTableBase;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryGtfsDataRepositoryTest {
    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void getAgencyCollectionShouldReturnRouteCollection() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        final Map<String, Agency> toVerify = underTest.getAgencyCollection();

        assertEquals("test_id0", toVerify.get("test_id0").getAgencyId());
        assertEquals("test_id1", toVerify.get("test_id1").getAgencyId());
    }

    @Test
    void callToAddEntityShouldAddAgencyToRepoAndReturnSameEntity() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Agency agency00 = mockBuilder.build();
        Agency toCheck = underTest.addEntity(agency00);

        assertEquals(1, underTest.getAgencyCollection().size());
        assertEquals(agency00, toCheck);

        mockBuilder.agencyId("test_id1");

        final Agency agency01 = mockBuilder.build();
        toCheck = underTest.addEntity(agency01);

        assertEquals(2, underTest.getAgencyCollection().size());
        assertEquals(toCheck, agency01);
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id1");

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_id0", underTest.getAgencyById("test_id0").getAgencyId());
        assertEquals("test_id1", underTest.getAgencyById("test_id1").getAgencyId());
    }

    @Test
    public void isPresentShouldReturnTrueIfAgencyIsAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        assertTrue(underTest.isPresent(agency00));
        assertTrue(underTest.isPresent(agency01));
    }

    @Test
    public void isPresentShouldReturnFalseIfAgencyIsNotAlreadyPresentInRepository()
            throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId("test_id1");

        Agency agency01 = mockBuilder.build();
        assertFalse(underTest.isPresent(agency01));
    }

    @Test
    public void tryToAddTwiceTheSameAgencyShouldThrowError() throws SQLIntegrityConstraintViolationException {
        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        mockBuilder.agencyId("test_id0")
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId("test_id0");

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    void callToAddEntityShouldAddRouteToRepoAndReturnEntity() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Route route00 = mockBuilder.build();
        Route toCheck = underTest.addEntity(route00);

        assertEquals(1, underTest.getRouteCollection().size());
        assertEquals(toCheck, route00);

        mockBuilder.routeId("test_id_1")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);


        final Route route01 = mockBuilder.build();
        toCheck = underTest.addEntity(route01);

        assertEquals(2, underTest.getRouteCollection().size());
        assertEquals(toCheck, route01);
    }

    @Test
    void getRouteCollectionShouldReturnRouteCollection() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Route route00 = mockBuilder.build();
        underTest.addEntity(route00);

        mockBuilder.routeId("test_id_1")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final Route route01 = mockBuilder.build();
        underTest.addEntity(route01);

        final Map<String, Route> toVerify = underTest.getRouteCollection();

        assertEquals("test_id_0", toVerify.get("test_id_0").getRouteId());
        assertEquals("test_id_1", toVerify.get("test_id_1").getRouteId());
    }

    @Test
    void getRouteByIdShouldReturnRelatedRoute() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockBuilder.build());

        mockBuilder.routeId("test_id_1")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_id_0", underTest.getRouteById("test_id_0").getRouteId());
        assertEquals("test_id_1", underTest.getRouteById("test_id_1").getRouteId());
    }

    @Test
    public void tryToAddTwiceTheSameRouteShouldThrowException() throws SQLIntegrityConstraintViolationException {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);
        when(mockBuilder.routeId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.routeShortName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeLongName(anyString())).thenCallRealMethod();
        when(mockBuilder.routeDesc(anyString())).thenCallRealMethod();
        when(mockBuilder.routeType(anyInt())).thenCallRealMethod();
        when(mockBuilder.routeUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.routeColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeTextColor(anyString())).thenCallRealMethod();
        when(mockBuilder.routeSortOrder(anyInt())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.routeId("test_id_0")
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(3)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(1);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockBuilder.build());

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> underTest.addEntity(mockBuilder.build()));
    }

    @Test
    public void getTranslationTableCollectionShouldReturnTranslationTableCollection() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockTranslation0);
        underTest.addEntity(mockTranslation1);

        Map<TableName, TranslationTableBase> toCheck = underTest.getTranslationTableCollection();

        assertEquals(2, toCheck.size());

        assertTrue(toCheck.containsKey(TableName.AGENCY));
        assertTrue(toCheck.containsKey(TableName.STOPS));
    }

    @Test
    public void getTranslationTableByTableNameShouldReturnRelatedTranslationTableEntity() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockTranslation0);
        underTest.addEntity(mockTranslation1);

        TranslationTableBase toCheck = underTest.getTranslationTableByTableName("agency");
        assertEquals(TableName.AGENCY, toCheck.getTableName());

        toCheck = underTest.getTranslationTableByTableName("stops");
        assertEquals(TableName.STOPS, toCheck.getTableName());
    }

    @Test
    public void addEntityShouldAddEntityToRepoAndReturnSameEntity() {

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        TranslationTableBase mockTranslation1 = mock(TranslationTableBase.class);
        when(mockTranslation1.getTableName()).thenReturn(TableName.STOPS);

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase toCheck = underTest.addEntity(mockTranslation0);

        assertEquals(mockTranslation0, toCheck);
        assertEquals(1, underTest.getTranslationTableCollection().size());

        toCheck = underTest.addEntity(mockTranslation1);

        assertEquals(mockTranslation1, toCheck);
        assertEquals(2, underTest.getTranslationTableCollection().size());
    }

    @Test
    public void isPresentShouldReturnFalseIfEntityIsNotInRepo() {

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        assertFalse(underTest.isPresent(mockTranslation0));
    }

    @Test
    public void isPresentShouldReturnTrueIfEntityIsInRepo() {

        GtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        TranslationTableBase mockTranslation0 = mock(TranslationTableBase.class);
        when(mockTranslation0.getTableName()).thenReturn(TableName.AGENCY);

        underTest.addEntity(mockTranslation0);

        assertTrue(underTest.isPresent(mockTranslation0));
    }
}