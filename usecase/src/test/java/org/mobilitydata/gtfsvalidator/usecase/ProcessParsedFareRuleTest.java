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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FareRule;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedFareRuleTest {
    private static final String ROUTE_ID = "route_id";
    private static final String FARE_ID = "fare_id";
    private static final String ORIGIN_ID = "origin_id";
    private static final String DESTINATION_ID = "destination_id";
    private static final String CONTAINS_ID = "contains_id";

    @Test
    void validFareRuleEntityShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareRule.FareRuleBuilder mockBuilder = mock(FareRule.FareRuleBuilder.class, RETURNS_SELF);
        final FareRule mockFareRule = mock(FareRule.class);
        final ParsedEntity mockParsedFareRule = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);

        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFareRule).when(mockEntityBuildResult).getData();

        when(mockParsedFareRule.get(FARE_ID)).thenReturn(FARE_ID);
        when(mockParsedFareRule.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedFareRule.get(ORIGIN_ID)).thenReturn(ORIGIN_ID);
        when(mockParsedFareRule.get(DESTINATION_ID)).thenReturn(DESTINATION_ID);
        when(mockParsedFareRule.get(CONTAINS_ID)).thenReturn(CONTAINS_ID);

        when(mockGtfsDataRepo.addFareRule(mockFareRule)).thenReturn(mockFareRule);

        final ProcessParsedFareRule underTest =
                new ProcessParsedFareRule(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareRule);

        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ORIGIN_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(DESTINATION_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(CONTAINS_ID));

        verify(mockBuilder, times(1)).fareId(FARE_ID);
        verify(mockBuilder, times(1)).routeId(ROUTE_ID);
        verify(mockBuilder, times(1)).originId(ORIGIN_ID);
        verify(mockBuilder, times(1)).destinationId(DESTINATION_ID);
        verify(mockBuilder, times(1)).containsId(CONTAINS_ID);
        verify(mockBuilder, times(1)).build();
        verify(mockGtfsDataRepo, times(1)).addFareRule(ArgumentMatchers.eq(mockFareRule));

        verifyNoMoreInteractions(mockBuilder, mockFareRule, mockParsedFareRule, mockGtfsDataRepo);
    }

    @Test
    void invalidFareRuleEntityShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareRule.FareRuleBuilder mockBuilder = mock(FareRule.FareRuleBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFareRule = mock(ParsedEntity.class);
        final Notice mockNotice = mock(Notice.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        noticeCollection.add(mockNotice);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGenericObject.isSuccess()).thenReturn(false);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(noticeCollection).when(mockGenericObject).getData();

        final ProcessParsedFareRule underTest =
                new ProcessParsedFareRule(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedFareRule.get(FARE_ID)).thenReturn(FARE_ID);
        when(mockParsedFareRule.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedFareRule.get(ORIGIN_ID)).thenReturn(ORIGIN_ID);
        when(mockParsedFareRule.get(DESTINATION_ID)).thenReturn(DESTINATION_ID);
        when(mockParsedFareRule.get(CONTAINS_ID)).thenReturn(CONTAINS_ID);

        underTest.execute(mockParsedFareRule);

        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ORIGIN_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(DESTINATION_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(CONTAINS_ID));

        verify(mockBuilder, times(1)).fareId(FARE_ID);
        verify(mockBuilder, times(1)).routeId(ROUTE_ID);
        verify(mockBuilder, times(1)).originId(ORIGIN_ID);
        verify(mockBuilder, times(1)).destinationId(DESTINATION_ID);
        verify(mockBuilder, times(1)).containsId(CONTAINS_ID);
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();
        verify(mockResultRepo, times(1)).addNotice(ArgumentMatchers.eq(mockNotice));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        verifyNoMoreInteractions(mockParsedFareRule, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    void duplicateFareRuleShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareRule.FareRuleBuilder mockBuilder = mock(FareRule.FareRuleBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFareRule = mock(ParsedEntity.class);
        when(mockParsedFareRule.getEntityId()).thenReturn("entity id");
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        final FareRule mockFareRule = mock(FareRule.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFareRule).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addFareRule(mockFareRule)).thenReturn(null);

        final ProcessParsedFareRule underTest =
                new ProcessParsedFareRule(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedFareRule.get(FARE_ID)).thenReturn(FARE_ID);
        when(mockParsedFareRule.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedFareRule.get(ORIGIN_ID)).thenReturn(ORIGIN_ID);
        when(mockParsedFareRule.get(DESTINATION_ID)).thenReturn(DESTINATION_ID);
        when(mockParsedFareRule.get(CONTAINS_ID)).thenReturn(CONTAINS_ID);

        underTest.execute(mockParsedFareRule);

        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(ORIGIN_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(DESTINATION_ID));
        verify(mockParsedFareRule, times(1)).get(ArgumentMatchers.eq(CONTAINS_ID));

        verify(mockBuilder, times(1)).fareId(FARE_ID);
        verify(mockBuilder, times(1)).routeId(ROUTE_ID);
        verify(mockBuilder, times(1)).originId(ORIGIN_ID);
        verify(mockBuilder, times(1)).destinationId(DESTINATION_ID);
        verify(mockBuilder, times(1)).containsId(CONTAINS_ID);
        verify(mockBuilder, times(1)).build();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareRule, times(1)).getEntityId();
        verify(mockGtfsDataRepo, times(1)).addFareRule(ArgumentMatchers.eq(mockFareRule));

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("fare_rules.txt", noticeList.get(0).getFilename());
        assertEquals("fare_id; route_id; origin_id; destination_id; contains_id",
                noticeList.get(0).getFieldName());
        assertEquals("entity id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareRule, mockResultRepo, mockGtfsDataRepo, mockFareRule, mockBuilder,
                mockGenericObject);
    }
}