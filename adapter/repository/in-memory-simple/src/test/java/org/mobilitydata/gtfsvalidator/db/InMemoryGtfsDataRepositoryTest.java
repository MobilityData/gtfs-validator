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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.*;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.ExceptionType;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways.Pathway;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers.Transfer;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.TableName;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations.Translation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryGtfsDataRepositoryTest {

    @Test
    void callToAddAgencyShouldAddAgencyToRepoAndReturnSameEntity() {
        final Agency mockAgency = mock(Agency.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency.getAgencyId()).thenReturn("agency id");

        assertEquals(underTest.addAgency(mockAgency, mockBuilder), mockAgency);
    }

    @Test
    void addSameAgencyTwiceShouldReturnNull() {
        final Agency mockAgency = mock(Agency.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency.getAgencyId()).thenReturn("agency id");

        underTest.addAgency(mockAgency, mockBuilder);

        assertNull(underTest.addAgency(mockAgency, mockBuilder));
    }

    @Test
    void addNullAgencyShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        //noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addAgency(null, mockBuilder));
        assertEquals("Cannot add null agency to data repository", exception.getMessage());
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() {
        final Agency mockAgency00 = mock(Agency.class);
        final Agency mockAgency01 = mock(Agency.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAgency00.getAgencyId()).thenReturn("agency id0");
        when(mockAgency01.getAgencyId()).thenReturn("agency id1");

        underTest.addAgency(mockAgency00, mockBuilder);
        underTest.addAgency(mockAgency01, mockBuilder);

        assertEquals(mockAgency00, underTest.getAgencyById("agency id0"));
        assertEquals(mockAgency01, underTest.getAgencyById("agency id1"));
    }

    @Test
    void callToGetAgencyAllShouldReturnAgencyCollection() {
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id0");
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id1");
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addAgency(mockAgency00, mockBuilder);
        underTest.addAgency(mockAgency01, mockBuilder);

        final Map<String, Agency> toCheck = underTest.getAgencyAll();
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey(mockAgency00.getAgencyId()));
        assertTrue(toCheck.containsKey(mockAgency01.getAgencyId()));
    }

    @Test
    void getAgencyCountShouldReturnExactNumberOfAgenciesInDataRepo() {
        final Agency mockAgency00 = mock(Agency.class);
        when(mockAgency00.getAgencyId()).thenReturn("agency id0");
        final Agency mockAgency01 = mock(Agency.class);
        when(mockAgency01.getAgencyId()).thenReturn("agency id1");
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        assertEquals(0, underTest.getAgencyCount());
        underTest.addAgency(mockAgency00, mockBuilder);
        assertEquals(1, underTest.getAgencyCount());
        underTest.addAgency(mockAgency01, mockBuilder);
        assertEquals(2, underTest.getAgencyCount());
    }

    @Test
    void callToAddRouteShouldAddRouteToRepoAndReturnEntity() {
        final Route mockRoute = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute.getAgencyId()).thenReturn("route id");

        assertEquals(underTest.addRoute(mockRoute), mockRoute);
    }

    @Test
    void addSameRouteTwiceShouldReturnNull() {
        final Route mockRoute = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute.getRouteId()).thenReturn("route id");

        underTest.addRoute(mockRoute);

        assertNull(underTest.addRoute(mockRoute));
    }

    @Test
    void addNullRouteShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        //noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addRoute(null));
        assertEquals("Cannot add null route to data repository", exception.getMessage());
    }

    @Test
    void getRouteAllShouldReturnAllRoutes() {
        final Route mockRoute00 = mock(Route.class);
        final Route mockRoute01 = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute00.getRouteId()).thenReturn("route id0");
        when(mockRoute01.getRouteId()).thenReturn("route id1");

        underTest.addRoute(mockRoute00);
        underTest.addRoute(mockRoute01);

        assertTrue(underTest.getRouteAll().containsKey("route id0"));
        assertTrue(underTest.getRouteAll().containsKey("route id1"));
    }

    @Test
    void getRouteByIdShouldReturnRelatedRoute() {
        final Route mockRoute00 = mock(Route.class);
        final Route mockRoute01 = mock(Route.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockRoute00.getRouteId()).thenReturn("route id0");
        when(mockRoute01.getRouteId()).thenReturn("route id1");

        underTest.addRoute(mockRoute00);
        underTest.addRoute(mockRoute01);

        assertEquals(mockRoute00, underTest.getRouteById("route id0"));
        assertEquals(mockRoute01, underTest.getRouteById("route id1"));
    }

    @Test
    void addSameCalendarDateShouldReturnNull() {
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);
        final LocalDate mockDate = mock(LocalDate.class);
        when(mockCalendarDate.getServiceId()).thenReturn("service id");
        when(mockCalendarDate.getDate()).thenReturn(mockDate);
        when(mockCalendarDate.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addCalendarDate(mockCalendarDate);
        assertNull(underTest.addCalendarDate(mockCalendarDate));
    }

    @Test
    void addNullCalendarDateShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        //for the purpose of the test addCalendarDate is called with null parameter. A warning is emitted since this
        // parameter is annotated as non null
        // noinspection ConstantConditions
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addCalendarDate(null));

        assertEquals("Cannot add null calendar date to data repository", exception.getMessage());
    }

    @Test
    void getCalendarDateAllShouldReturnCalendarDateCollection() {
        final CalendarDate calendarDate00 = mock(CalendarDate.class);
        final CalendarDate calendarDate01 = mock(CalendarDate.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final LocalDate date = LocalDate.now();

        when(calendarDate00.getServiceId()).thenReturn("service id 00");
        when(calendarDate00.getDate()).thenReturn(date);
        when(calendarDate00.getExceptionType()).thenReturn(ExceptionType.ADDED_SERVICE);

        when(calendarDate01.getServiceId()).thenReturn("service id 01");
        when(calendarDate01.getDate()).thenReturn(date);
        when(calendarDate01.getExceptionType()).thenReturn(ExceptionType.REMOVED_SERVICE);

        underTest.addCalendarDate(calendarDate00);
        underTest.addCalendarDate(calendarDate01);

        final Map<String, Map<String, CalendarDate>> toCheck = underTest.getCalendarDateAll();
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey("service id 00"));
        assert (toCheck.get("service id 00").containsKey(date.toString()));
        assertTrue(toCheck.containsKey("service id 01"));
        assert (toCheck.get("service id 01").containsKey(date.toString()));
    }

    @Test
    void addSameLevelTwiceShouldReturnNull() {
        final Level mockLevel = mock(Level.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockLevel.getLevelId()).thenReturn("level id");

        underTest.addLevel(mockLevel);
        assertNull(underTest.addLevel(mockLevel));
    }

    @Test
    void addNullLevelShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addLevel(null));
        assertEquals("Cannot add null level to data repository", exception.getMessage());
    }

    @Test
    void addLevelAndGetLevelByIdShouldReturnSameEntity() {
        final Level mockLevel00 = mock(Level.class);
        final Level mockLevel01 = mock(Level.class);
        when(mockLevel00.getLevelId()).thenReturn("level id 0");
        when(mockLevel01.getLevelId()).thenReturn("level id 1");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertEquals(mockLevel00, underTest.addLevel(mockLevel00));
        assertEquals(mockLevel01, underTest.addLevel(mockLevel01));

        assertEquals(mockLevel00, underTest.getLevelById("level id 0"));
        assertEquals(mockLevel01, underTest.getLevelById("level id 1"));
    }

    @Test
    void addSameCalendarTwiceShouldReturnNull() {
        final Calendar mockCalendar = mock(Calendar.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockCalendar.getServiceId()).thenReturn("service id");

        underTest.addCalendar(mockCalendar);

        assertNull(underTest.addCalendar(mockCalendar));
    }

    @Test
    void addNullCalendarShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertThrows(IllegalArgumentException.class, () -> underTest.addCalendar(null));
    }

    @Test
    void getCalendarByServiceIdShouldReturnRelatedCalendar() {
        final Calendar mockCalendar00 = mock(Calendar.class);
        final Calendar mockCalendar01 = mock(Calendar.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        when(mockCalendar00.getServiceId()).thenReturn("service id00");
        when(mockCalendar01.getServiceId()).thenReturn("service id01");

        underTest.addCalendar(mockCalendar00);
        underTest.addCalendar(mockCalendar01);

        assertEquals(mockCalendar00, underTest.getCalendarByServiceId("service id00"));
        assertEquals(mockCalendar01, underTest.getCalendarByServiceId("service id01"));
    }

    @Test
    void getCalendarAllShouldReturnCalendarCollection() {
        final Calendar mockCalendar00 = mock(Calendar.class);
        final Calendar mockCalendar01 = mock(Calendar.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        when(mockCalendar00.getServiceId()).thenReturn("service id00");
        when(mockCalendar01.getServiceId()).thenReturn("service id01");

        underTest.addCalendar(mockCalendar00);
        underTest.addCalendar(mockCalendar01);

        final Map<String, Calendar> toCheck = underTest.getCalendarAll();
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey("service id00"));
        assertTrue(toCheck.containsKey("service id01"));
    }

    @Test
    void addSameTripTwiceShouldReturnNull() {
        final Trip mockTrip = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip.getTripId()).thenReturn("trip id");
        when(mockTrip.getBlockId()).thenReturn("block id");

        underTest.addTrip(mockTrip);

        assertNull(underTest.addTrip(mockTrip));
    }

    @Test
    void addNullTripShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertThrows(IllegalArgumentException.class, () -> underTest.addTrip(null));
    }

    @Test
    void addTripShouldReturnSameEntityAndCallToGetTripByIdShouldReturnRelatedTrip() {
        final Trip mockTrip00 = mock(Trip.class);
        final Trip mockTrip01 = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip00.getTripId()).thenReturn("trip id00");
        when(mockTrip00.getBlockId()).thenReturn("block id00");
        when(mockTrip01.getTripId()).thenReturn("trip id01");
        when(mockTrip01.getBlockId()).thenReturn("block id01");

        assertEquals(mockTrip00, underTest.addTrip(mockTrip00));
        assertEquals(mockTrip01, underTest.addTrip(mockTrip01));

        assertEquals(mockTrip00, underTest.getTripById("trip id00"));
        assertEquals(mockTrip01, underTest.getTripById("trip id01"));
    }

    @Test
    void addTripShouldReturnSameEntityAndCallToGetAllTripByBlockIdShouldReturnRelatedTrip() {
        final Trip mockTrip00 = mock(Trip.class);
        final Trip mockTrip01 = mock(Trip.class);
        final Trip mockTrip02 = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip00.getTripId()).thenReturn("trip id00");
        when(mockTrip00.getBlockId()).thenReturn("block id00");

        when(mockTrip01.getTripId()).thenReturn("trip id01");
        when(mockTrip01.getBlockId()).thenReturn("block id01");

        when(mockTrip02.getTripId()).thenReturn("trip id02");
        when(mockTrip02.getBlockId()).thenReturn("block id01");

        assertEquals(mockTrip00, underTest.addTrip(mockTrip00));
        assertEquals(mockTrip01, underTest.addTrip(mockTrip01));
        assertEquals(mockTrip02, underTest.addTrip(mockTrip02));

        assertEquals(List.of(mockTrip00), underTest.getAllTripByBlockId().get("block id00"));
        assertEquals(List.of(mockTrip01, mockTrip02), underTest.getAllTripByBlockId().get("block id01"));
    }

    @Test
    void getTripAllShouldReturnTripCollection() {
        final Trip mockTrip00 = mock(Trip.class);
        final Trip mockTrip01 = mock(Trip.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTrip00.getTripId()).thenReturn("trip id00");
        when(mockTrip00.getBlockId()).thenReturn("block id00");
        when(mockTrip01.getTripId()).thenReturn("trip id01");
        when(mockTrip01.getBlockId()).thenReturn("block id01");

        underTest.addTrip(mockTrip00);
        underTest.addTrip(mockTrip01);

        final Map<String, Trip> toCheck = underTest.getTripAll();
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey("trip id00"));
        assertTrue(toCheck.containsKey("trip id01"));
    }

    @Test
    void addSameTransferTwiceShouldReturnNull() {
        final Transfer mockTransfer = mock(Transfer.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTransfer.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer.getToStopId()).thenReturn("stop id 1");

        underTest.addTransfer(mockTransfer);

        assertNull(underTest.addTransfer(mockTransfer));
    }

    @Test
    void addNullTransferShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTransfer(null));
        assertEquals("Cannot add null transfer to data repository", exception.getMessage());
    }

    @Test
    void addTransferAndGetTransferByStopIdPairShouldReturnSameEntity() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Transfer mockTransfer00 = mock(Transfer.class);
        when(mockTransfer00.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer00.getToStopId()).thenReturn("stop id 1");

        final Transfer mockTransfer01 = mock(Transfer.class);
        when(mockTransfer01.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer01.getToStopId()).thenReturn("stop id 2");

        final Transfer mockTransfer02 = mock(Transfer.class);
        when(mockTransfer02.getFromStopId()).thenReturn("stop id 4");
        when(mockTransfer02.getToStopId()).thenReturn("stop id 5");

        final Transfer mockTransfer03 = mock(Transfer.class);
        when(mockTransfer03.getFromStopId()).thenReturn("stop id 0");
        when(mockTransfer03.getToStopId()).thenReturn("stop id 5");

        assertEquals(mockTransfer00, underTest.addTransfer(mockTransfer00));
        assertEquals(mockTransfer01, underTest.addTransfer(mockTransfer01));
        assertEquals(mockTransfer02, underTest.addTransfer(mockTransfer02));
        assertEquals(mockTransfer03, underTest.addTransfer(mockTransfer03));

        assertEquals(mockTransfer00, underTest.getTransferByStopPair("stop id 0", "stop id 1"));
        assertEquals(mockTransfer01, underTest.getTransferByStopPair("stop id 0", "stop id 2"));
        assertEquals(mockTransfer02, underTest.getTransferByStopPair("stop id 4", "stop id 5"));
        assertEquals(mockTransfer03, underTest.getTransferByStopPair("stop id 0", "stop id 5"));
    }

    @Test
    void addSameFeedInfoTwiceShouldReturnNull() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("feed publisher name");

        underTest.addFeedInfo(mockFeedInfo);

        assertNull(underTest.addFeedInfo(mockFeedInfo));
    }

    @Test
    void addNullFeedInfoShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addFeedInfo(null));
        assertEquals("Cannot add null feedInfo to data repository", exception.getMessage());
    }

    @Test
    void getFeedInfoByPublisherNameShouldReturnRelatedEntity() {
        final FeedInfo mockFeedInfo00 = mock(FeedInfo.class);
        final FeedInfo mockFeedInfo01 = mock(FeedInfo.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFeedInfo00.getFeedPublisherName()).thenReturn("feed publisher 0");
        when(mockFeedInfo01.getFeedPublisherName()).thenReturn("feed publisher 1");

        assertEquals(mockFeedInfo00, underTest.addFeedInfo(mockFeedInfo00));
        assertEquals(mockFeedInfo01, underTest.addFeedInfo(mockFeedInfo01));

        assertEquals(mockFeedInfo00, underTest.getFeedInfoByFeedPublisherName("feed publisher 0"));
        assertEquals(mockFeedInfo01, underTest.getFeedInfoByFeedPublisherName("feed publisher 1"));
    }

    @Test
    void addSameFareAttributeTwiceShouldReturnNull() {
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFareAttribute.getFareId()).thenReturn("fare attribute id");

        underTest.addFareAttribute(mockFareAttribute);

        assertNull(underTest.addFareAttribute(mockFareAttribute));
    }

    @Test
    void addNullFareAttributeShouldThrowException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addFareAttribute(null));
        assertEquals("Cannot add null fare attribute to data repository", exception.getMessage());
    }

    @Test
    void addFareAttributeAndGetFareAttributeByIdShouldReturnSameEntity() {
        final FareAttribute mockFareAttribute00 = mock(FareAttribute.class);
        final FareAttribute mockFareAttribute01 = mock(FareAttribute.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        when(mockFareAttribute00.getFareId()).thenReturn("fare attribute id 00");
        when(mockFareAttribute01.getFareId()).thenReturn("fare attribute id 01");

        assertEquals(underTest.addFareAttribute(mockFareAttribute00),
                underTest.getFareAttributeById("fare attribute id 00"));
        assertEquals(underTest.addFareAttribute(mockFareAttribute01),
                underTest.getFareAttributeById("fare attribute id 01"));
    }

    @Test
    void addNullFareRuleShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addFareRule(null));
        assertEquals("Cannot add null FareRule to data repository", exception.getMessage());
    }

    @Test
    void addSameFareRuleTwiceShouldReturnNull() {
        final FareRule mockFareRule = mock(FareRule.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFareRule.getFareId()).thenReturn("fare id");

        underTest.addFareRule(mockFareRule);

        assertNull(underTest.addFareRule(mockFareRule));
    }

    @Test
    void addFareRuleAndGetFareRuleShouldReturnSameEntity() {
        final FareRule mockFareRule00 = mock(FareRule.class);
        final FareRule mockFareRule01 = mock(FareRule.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFareRule00.getFareId()).thenReturn("fare id0");
        when(mockFareRule01.getFareId()).thenReturn("fare id1");
        when(mockFareRule00.getFareRuleMappingKey()).thenReturn("fare id0" + "null" + "null" + "null" + "null");
        when(mockFareRule01.getFareRuleMappingKey()).thenReturn("fare id1" + "null" + "null" + "null" + "null");

        assertEquals(mockFareRule00, underTest.addFareRule(mockFareRule00));
        assertEquals(mockFareRule01, underTest.addFareRule(mockFareRule01));

        assertEquals(mockFareRule00, underTest.getFareRule("fare id0", null, null,
                null, null));
        assertEquals(mockFareRule01, underTest.getFareRule("fare id1", null, null,
                null, null));
    }

    @Test
    void addNullFrequencyShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addFrequency(null));
        assertEquals("Cannot add null frequency to data repository", exception.getMessage());
    }

    @Test
    void addSameFrequencyTwiceShouldReturnNull() {
        final Frequency mockFrequency00 = mock(Frequency.class);
        final Frequency mockFrequency01 = mock(Frequency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFrequency00.getTripId()).thenReturn("trip id");
        when(mockFrequency00.getStartTime()).thenReturn(0);
        when(mockFrequency01.getTripId()).thenReturn("trip id");
        when(mockFrequency01.getStartTime()).thenReturn(0);

        underTest.addFrequency(mockFrequency00);

        assertNull(underTest.addFrequency(mockFrequency00));
        assertNull(underTest.addFrequency(mockFrequency01));
    }

    @Test
    void addFrequencyAndGetFrequencyShouldReturnSameEntity() {
        final Frequency mockFrequency00 = mock(Frequency.class);
        final Frequency mockFrequency01 = mock(Frequency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFrequency00.getTripId()).thenReturn("trip id0");
        when(mockFrequency01.getTripId()).thenReturn("trip id1");
        when(mockFrequency00.getFrequencyMappingKey()).thenReturn("fare id0" + "null");
        when(mockFrequency01.getFrequencyMappingKey()).thenReturn("fare id1" + "null");

        assertEquals(mockFrequency00, underTest.addFrequency(mockFrequency00));
        assertEquals(mockFrequency01, underTest.addFrequency(mockFrequency01));

        assertEquals(mockFrequency00, underTest.getFrequency("fare id0", null));
        assertEquals(mockFrequency01, underTest.getFrequency("fare id1", null));
    }

    @Test
    void getFrequencyAllByTripIdShouldReturnAllFrequencyEntitiesGroupedByTripId() {
        final Frequency mockFrequency00 = mock(Frequency.class);
        final Frequency mockFrequency01 = mock(Frequency.class);
        final Frequency mockFrequency02 = mock(Frequency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFrequency00.getTripId()).thenReturn("first trip id");
        when(mockFrequency00.getStartTime()).thenReturn(0);
        when(mockFrequency01.getTripId()).thenReturn("first trip id");
        when(mockFrequency01.getStartTime()).thenReturn(1);
        when(mockFrequency02.getTripId()).thenReturn("second trip id");
        when(mockFrequency02.getStartTime()).thenReturn(1);
        when(mockFrequency00.getFrequencyMappingKey()).thenReturn("first trip id" + "0");
        when(mockFrequency01.getFrequencyMappingKey()).thenReturn("first trip id" + "1");
        when(mockFrequency02.getFrequencyMappingKey()).thenReturn("second trip id" + "1");

        underTest.addFrequency(mockFrequency00);
        underTest.addFrequency(mockFrequency01);
        underTest.addFrequency(mockFrequency02);

        assertEquals(2, underTest.getFrequencyAllByTripId().size());
        assertTrue(underTest.getFrequencyAllByTripId().containsKey("first trip id"));
        assertTrue(underTest.getFrequencyAllByTripId().containsKey("second trip id"));

        assertEquals(2, underTest.getFrequencyAllByTripId().get("first trip id").size());
        assertEquals(1, underTest.getFrequencyAllByTripId().get("second trip id").size());

        assertTrue(underTest.getFrequencyAllByTripId().get("first trip id").contains(mockFrequency00));
        assertTrue(underTest.getFrequencyAllByTripId().get("first trip id").contains(mockFrequency01));
        assertTrue(underTest.getFrequencyAllByTripId().get("second trip id").contains(mockFrequency02));
    }

    @Test
    void getFrequencyAllShouldReturnFrequencyCollection() {
        final Frequency mockFrequency00 = mock(Frequency.class);
        final Frequency mockFrequency01 = mock(Frequency.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFrequency00.getTripId()).thenReturn("trip id00");
        when(mockFrequency00.getStartTime()).thenReturn(235);
        when(mockFrequency00.getFrequencyMappingKey()).thenReturn("trip id00235");
        when(mockFrequency01.getTripId()).thenReturn("trip id01");
        when(mockFrequency00.getStartTime()).thenReturn(8985);
        when(mockFrequency01.getFrequencyMappingKey()).thenReturn("trip id018985");

        underTest.addFrequency(mockFrequency00);
        underTest.addFrequency(mockFrequency01);

        assertTrue(underTest.getFrequencyAll().containsKey("trip id00235"));
        assertTrue(underTest.getFrequencyAll().containsKey("trip id018985"));
    }

    @Test
    void getFrequencyAllShouldReturnImmutableFrequencyCollection() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertThrows(UnsupportedOperationException.class, () ->
                underTest.getFrequencyAll().put("test id", mock(Frequency.class)));
    }

    @Test
    void addSamePathwayTwiceShouldReturnNull() {
        final Pathway mockPathway = mock(Pathway.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockPathway.getPathwayId()).thenReturn("pathway id");
        underTest.addPathway(mockPathway);

        assertNull(underTest.addPathway(mockPathway));
    }

    @Test
    void addNullPathwayShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addPathway(null));
        assertEquals("Cannot add null pathway to data repository", exception.getMessage());
    }

    @Test
    void addPathwayAndGetPathwayByIdShouldReturnSameEntity() {
        final Pathway mockPathway00 = mock(Pathway.class);
        final Pathway mockPathway01 = mock(Pathway.class);
        when(mockPathway00.getPathwayId()).thenReturn("pathway id 00");
        when(mockPathway01.getPathwayId()).thenReturn("pathway id 01");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        assertEquals(mockPathway00, underTest.addPathway(mockPathway00));
        assertEquals(mockPathway01, underTest.addPathway(mockPathway01));

        assertEquals(mockPathway00, underTest.getPathwayById("pathway id 00"));
        assertEquals(mockPathway01, underTest.getPathwayById("pathway id 01"));
    }

    @Test
    void addNullAttributionShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addAttribution(null));
        assertEquals("Cannot add null attribution to data repository", exception.getMessage());
    }

    @Test
    void addSameAttributionTwiceShouldReturnNull() {
        final Attribution mockAttribution = mock(Attribution.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAttribution.getOrganizationName()).thenReturn("organization name");
        when(mockAttribution.getAttributionMappingKey()).thenReturn("mock key");
        underTest.addAttribution(mockAttribution);

        assertNull(underTest.addAttribution(mockAttribution));
        verify(mockAttribution, times(2)).getAttributionMappingKey();
        verifyNoMoreInteractions(mockAttribution);
    }

    @Test
    void addAttributionAndGetAttributionShouldReturnSameEntity() {
        final Attribution mockAttribution00 = mock(Attribution.class);
        when(mockAttribution00.getOrganizationName()).thenReturn("organization name 0");
        final Attribution mockAttribution01 = mock(Attribution.class);
        when(mockAttribution01.getOrganizationName()).thenReturn("organization name 1");

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockAttribution00.getAttributionMappingKey())
                .thenReturn("nullnullnullnullorganization name 0falsefalsefalsenullnullnull");
        when(mockAttribution01.getAttributionMappingKey())
                .thenReturn("nullnullnullnullorganization name 1falsefalsefalsenullnullnull");

        assertEquals(mockAttribution00, underTest.addAttribution(mockAttribution00));
        assertEquals(mockAttribution01, underTest.addAttribution(mockAttribution01));

        assertEquals(mockAttribution00, underTest.getAttribution(null, null, null,
                null, "organization name 0", false, false, false,
                null, null, null));
        assertEquals(mockAttribution01, underTest.getAttribution(null, null, null,
                null, "organization name 1", false, false, false,
                null, null, null));
        verify(mockAttribution00, times(1)).getAttributionMappingKey();
        verify(mockAttribution01, times(1)).getAttributionMappingKey();
        verifyNoMoreInteractions(mockAttribution00, mockAttribution01);
    }

    @Test
    void addTwiceSameShapePointShouldReturnNull() {
        final ShapePoint mockShapePoint = mock(ShapePoint.class);
        when(mockShapePoint.getShapeId()).thenReturn("test id");
        when(mockShapePoint.getShapePtLat()).thenReturn(50f);
        when(mockShapePoint.getShapePtLon()).thenReturn(100f);
        when(mockShapePoint.getShapePtSequence()).thenReturn(4);
        when(mockShapePoint.getShapeDistTraveled()).thenReturn(56f);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addShapePoint(mockShapePoint);
        assertNull(underTest.addShapePoint(mockShapePoint));
        assertEquals("test id", underTest.getShapeById("test id").get(4).getShapeId());
        assertEquals(50f, underTest.getShapeById("test id").get(4).getShapePtLat());
        assertEquals(100f, underTest.getShapeById("test id").get(4).getShapePtLon());
        assertEquals(4, underTest.getShapeById("test id").get(4).getShapePtSequence());
        assertEquals(56, underTest.getShapeById("test id").get(4).getShapeDistTraveled());
    }

    @Test
    void addShapePointWithSameDataShouldReturnNull() {
        final ShapePoint firstShapePoint = mock(ShapePoint.class);
        when(firstShapePoint.getShapeId()).thenReturn("test id00");
        when(firstShapePoint.getShapePtLat()).thenReturn(50f);
        when(firstShapePoint.getShapePtLon()).thenReturn(100f);
        when(firstShapePoint.getShapePtSequence()).thenReturn(4);
        when(firstShapePoint.getShapeDistTraveled()).thenReturn(56f);

        final ShapePoint duplicateShapePoint = mock(ShapePoint.class);
        when(duplicateShapePoint.getShapeId()).thenReturn("test id00");
        when(duplicateShapePoint.getShapePtLat()).thenReturn(50f);
        when(duplicateShapePoint.getShapePtLon()).thenReturn(100f);
        when(duplicateShapePoint.getShapePtSequence()).thenReturn(4);
        when(duplicateShapePoint.getShapeDistTraveled()).thenReturn(56f);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        assertEquals(firstShapePoint, underTest.addShapePoint(firstShapePoint));
        assertNull(underTest.addShapePoint(duplicateShapePoint));
    }

    @Test
    void addNullShapePointShouldThrowException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addShapePoint(null));
        assertEquals("Cannot add null shape point to data repository", exception.getMessage());
    }

    @Test
    void addShapeAndGetShapeByIdShouldReturnSameEntity() {
        final ShapePoint mockShapePoint00 = mock(ShapePoint.class);
        when(mockShapePoint00.getShapeId()).thenReturn("test id00");
        when(mockShapePoint00.getShapePtSequence()).thenReturn(4);

        final ShapePoint mockShapePoint01 = mock(ShapePoint.class);
        when(mockShapePoint01.getShapeId()).thenReturn("test id01");
        when(mockShapePoint01.getShapePtSequence()).thenReturn(8);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addShapePoint(mockShapePoint00);
        underTest.addShapePoint(mockShapePoint01);

        final Map<Integer, ShapePoint> firstMapToCheck = underTest.getShapeById("test id00");

        final Map<Integer, ShapePoint> secondMapToCheck = underTest.getShapeById("test id01");

        assertEquals(firstMapToCheck, underTest.getShapeById("test id00"));
        assertEquals(secondMapToCheck, underTest.getShapeById("test id01"));
    }

    @Test
    void getShapeByIdShouldReturnNullIfShapeIdIsNull() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertNull(underTest.getShapeById(null));
    }

    @Test
    void addShapePointShouldMaintainOrder() {
        final ShapePoint firstShapePointInSequence = mock(ShapePoint.class);
        when(firstShapePointInSequence.getShapeId()).thenReturn("test id00");
        when(firstShapePointInSequence.getShapePtSequence()).thenReturn(4);

        final ShapePoint secondShapePointInSequence = mock(ShapePoint.class);
        when(secondShapePointInSequence.getShapeId()).thenReturn("test id00");
        when(secondShapePointInSequence.getShapePtSequence()).thenReturn(8);

        final ShapePoint thirdShapePointInSequence = mock(ShapePoint.class);
        when(thirdShapePointInSequence.getShapeId()).thenReturn("test id00");
        when(thirdShapePointInSequence.getShapePtSequence()).thenReturn(12);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addShapePoint(thirdShapePointInSequence);
        underTest.addShapePoint(secondShapePointInSequence);

        final List<ShapePoint> toCheck = new ArrayList<>();

        underTest.getShapeById("test id00").forEach((key, value) -> toCheck.add(value));
        assertEquals(secondShapePointInSequence, toCheck.get(0));
        assertEquals(thirdShapePointInSequence, toCheck.get(1));

        underTest.addShapePoint(firstShapePointInSequence);

        toCheck.clear();
        underTest.getShapeById("test id00").forEach((key, value) -> toCheck.add(value));

        assertEquals(firstShapePointInSequence, toCheck.get(0));
        assertEquals(secondShapePointInSequence, toCheck.get(1));
        assertEquals(thirdShapePointInSequence, toCheck.get(2));
    }

    @Test
    void getShapeAllShouldReturnShapeCollection() {
        final ShapePoint mockShapePoint00 = mock(ShapePoint.class);
        when(mockShapePoint00.getShapeId()).thenReturn("test id00");
        when(mockShapePoint00.getShapePtSequence()).thenReturn(4);

        final ShapePoint mockShapePoint01 = mock(ShapePoint.class);
        when(mockShapePoint01.getShapeId()).thenReturn("test id01");
        when(mockShapePoint01.getShapePtSequence()).thenReturn(8);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addShapePoint(mockShapePoint00);
        underTest.addShapePoint(mockShapePoint01);

        final Map<String, SortedMap<Integer, ShapePoint>> toCheck = underTest.getShapeAll();

        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey(mockShapePoint00.getShapeId()));
        assertTrue(toCheck.containsKey(mockShapePoint01.getShapeId()));
    }

    @Test
    void addNullStopTimeShouldThrowException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addStopTime(null));
        assertEquals("Cannot add null StopTime to data repository", exception.getMessage());
    }

    @Test
    void addSameStopTimeTwiceShouldReturnNull() {
        final StopTime mockStopTime = mock(StopTime.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockStopTime.getTripId()).thenReturn("trip id");
        when(mockStopTime.getStopSequence()).thenReturn(3);

        underTest.addStopTime(mockStopTime);

        assertNull(underTest.addStopTime(mockStopTime));
    }

    @Test
    void addStopTimeWithSameDataShouldReturnNull() {
        final StopTime firstStopTime = mock(StopTime.class);
        when(firstStopTime.getTripId()).thenReturn("trip id");
        when(firstStopTime.getStopSequence()).thenReturn(3);

        final StopTime duplicateStopTime = mock(StopTime.class);
        when(duplicateStopTime.getTripId()).thenReturn("trip id");
        when(duplicateStopTime.getStopSequence()).thenReturn(3);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        assertEquals(firstStopTime, underTest.addStopTime(firstStopTime));
        assertNull(underTest.addStopTime(duplicateStopTime));
    }

    @Test
    void getStopTimeByTripIdAndAddStopTimeShouldReturnSameEntity() {
        final StopTime mockStopTime00 = mock(StopTime.class);
        when(mockStopTime00.getTripId()).thenReturn("trip id00");
        when(mockStopTime00.getStopSequence()).thenReturn(3);

        final StopTime mockStopTime01 = mock(StopTime.class);
        when(mockStopTime01.getTripId()).thenReturn("trip id01");
        when(mockStopTime01.getStopSequence()).thenReturn(4);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addStopTime(mockStopTime00);
        underTest.addStopTime(mockStopTime01);

        final TreeMap<Integer, StopTime> firstMapToCheck = new TreeMap<>();
        firstMapToCheck.put(3, mockStopTime00);

        final TreeMap<Integer, StopTime> secondMapToCheck = new TreeMap<>();
        secondMapToCheck.put(4, mockStopTime01);

        assertEquals(firstMapToCheck, underTest.getStopTimeByTripId("trip id00"));
        assertEquals(secondMapToCheck, underTest.getStopTimeByTripId("trip id01"));
    }

    @Test
    void addStopTimeShouldMaintainOrder() {
        final StopTime firstStopTimeInSequence = mock(StopTime.class);
        when(firstStopTimeInSequence.getTripId()).thenReturn("trip id00");
        when(firstStopTimeInSequence.getStopSequence()).thenReturn(4);

        final StopTime secondStopTimeInSequence = mock(StopTime.class);
        when(secondStopTimeInSequence.getTripId()).thenReturn("trip id00");
        when(secondStopTimeInSequence.getStopSequence()).thenReturn(8);

        final StopTime thirdStopTimeInSequence = mock(StopTime.class);
        when(thirdStopTimeInSequence.getTripId()).thenReturn("trip id00");
        when(thirdStopTimeInSequence.getStopSequence()).thenReturn(12);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addStopTime(thirdStopTimeInSequence);
        underTest.addStopTime(secondStopTimeInSequence);

        final List<StopTime> toCheck = new ArrayList<>();

        underTest.getStopTimeByTripId("trip id00").forEach((key, value) -> toCheck.add(value));
        assertEquals(secondStopTimeInSequence, toCheck.get(0));
        assertEquals(thirdStopTimeInSequence, toCheck.get(1));

        underTest.addStopTime(firstStopTimeInSequence);

        toCheck.clear();
        underTest.getStopTimeByTripId("trip id00").forEach((key, value) -> toCheck.add(value));

        assertEquals(firstStopTimeInSequence, toCheck.get(0));
        assertEquals(secondStopTimeInSequence, toCheck.get(1));
        assertEquals(thirdStopTimeInSequence, toCheck.get(2));
    }

    @Test
    void getStopTimeAllShouldReturnStopTimeCollection() {
        final StopTime firstStopTimeInSequenceOfTrip00 = mock(StopTime.class);
        when(firstStopTimeInSequenceOfTrip00.getTripId()).thenReturn("trip id00");
        when(firstStopTimeInSequenceOfTrip00.getStopSequence()).thenReturn(4);

        final StopTime secondStopTimeInSequenceOfTrip00 = mock(StopTime.class);
        when(secondStopTimeInSequenceOfTrip00.getTripId()).thenReturn("trip id00");
        when(secondStopTimeInSequenceOfTrip00.getStopSequence()).thenReturn(8);

        final StopTime firstStopTimeInSequenceOfTrip01 = mock(StopTime.class);
        when(firstStopTimeInSequenceOfTrip01.getTripId()).thenReturn("trip id01");
        when(firstStopTimeInSequenceOfTrip01.getStopSequence()).thenReturn(12);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addStopTime(firstStopTimeInSequenceOfTrip00);
        underTest.addStopTime(secondStopTimeInSequenceOfTrip00);
        underTest.addStopTime(firstStopTimeInSequenceOfTrip01);

        final Map<String, TreeMap<Integer, StopTime>> toCheck = underTest.getStopTimeAll();
        assertEquals(2, toCheck.size());
        assertEquals(2, toCheck.get("trip id00").size());
        assertTrue(toCheck.get("trip id00").containsKey(4));
        assertTrue(toCheck.get("trip id00").containsKey(8));
        assertEquals(1, toCheck.get("trip id01").size());
        assertTrue(toCheck.get("trip id01").containsKey(12));
    }

    @Test
    void addNullTranslationShouldThrowIllegalArgumentException() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.addTranslation(null));
        assertEquals("Cannot add null Translation to data repository", exception.getMessage());
    }

    @Test
    void addSameTranslationTwiceShouldReturnNull() {
        final Translation mockTranslation = mock(Translation.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTranslation.getTableName()).thenReturn(TableName.AGENCY);
        when(mockTranslation.getFieldName()).thenReturn("field name");
        when(mockTranslation.getLanguage()).thenReturn("language");

        underTest.addTranslation(mockTranslation);

        assertNull(underTest.addTranslation(mockTranslation));
    }

    @Test
    void addTranslationWithSameDataShouldReturnNul() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        final Translation mockTranslation = mock(Translation.class);
        when(mockTranslation.getTableName()).thenReturn(TableName.AGENCY);
        when(mockTranslation.getFieldName()).thenReturn("field name");
        when(mockTranslation.getLanguage()).thenReturn("language");

        final Translation duplicateTranslation = mock(Translation.class);
        when(duplicateTranslation.getTableName()).thenReturn(TableName.AGENCY);
        when(duplicateTranslation.getFieldName()).thenReturn("field name");
        when(duplicateTranslation.getLanguage()).thenReturn("language");

        underTest.addTranslation(mockTranslation);

        assertNull(underTest.addTranslation(duplicateTranslation));
    }

    @Test
    void addTranslationAndGetTranslationShouldReturnSameEntity() {
        final Translation mockTranslation00 = mock(Translation.class);
        final Translation mockTranslation01 = mock(Translation.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockTranslation00.getTableName()).thenReturn(TableName.AGENCY);
        when(mockTranslation00.getFieldName()).thenReturn("field");
        when(mockTranslation00.getLanguage()).thenReturn("french");
        when(mockTranslation01.getTableName()).thenReturn(TableName.STOP_TIMES);
        when(mockTranslation01.getFieldName()).thenReturn("field");
        when(mockTranslation01.getLanguage()).thenReturn("spanish");

        assertEquals(mockTranslation00, underTest.addTranslation(mockTranslation00));
        assertEquals(mockTranslation01, underTest.addTranslation(mockTranslation01));

        assertEquals(mockTranslation00,
                underTest.getTranslationByTableNameFieldValueLanguage("agency",
                        "field", "french"));
        assertEquals(mockTranslation01,
                underTest.getTranslationByTableNameFieldValueLanguage("stop_times",
                        "field", "spanish"));
    }

    @Test
    void callToGetFeedInfoAllShouldReturnFeedInfoCollection() {
        final FeedInfo mockFeedInfo00 = mock(FeedInfo.class);
        final FeedInfo mockFeedInfo01 = mock(FeedInfo.class);
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        when(mockFeedInfo00.getFeedPublisherName()).thenReturn("feed publisher 0");
        when(mockFeedInfo01.getFeedPublisherName()).thenReturn("feed publisher 1");

        assertEquals(mockFeedInfo00, underTest.addFeedInfo(mockFeedInfo00));
        assertEquals(mockFeedInfo01, underTest.addFeedInfo(mockFeedInfo01));

        final Map<String, FeedInfo> toCheck = underTest.getFeedInfoAll();
        assertEquals(2, toCheck.size());
        assertTrue(toCheck.containsKey(mockFeedInfo00.getFeedPublisherName()));
        assertTrue(toCheck.containsKey(mockFeedInfo01.getFeedPublisherName()));
    }

    @Test
    void shouldReturnEmptyStringWhenFeedInfoIsNotProvided() {
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        assertEquals("", underTest.getFeedPublisherName());
    }

    @Test
    void shouldReturnNonEmptyStringWhenFeedInfoIsProvided() {
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        when(mockFeedInfo.getFeedPublisherName()).thenReturn("feed publisher");
        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();
        underTest.addFeedInfo(mockFeedInfo);
        assertEquals("feed publisher", underTest.getFeedPublisherName());
    }
}
