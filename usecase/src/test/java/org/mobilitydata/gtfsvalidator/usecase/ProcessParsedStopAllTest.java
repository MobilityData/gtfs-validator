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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedStopAllTest {

    private static final String CHILD_ID = "child_id";
    private static final String PARENT_ID = "parent_id";
    private static final String LOCATION_TYPE = "location_type";
    private static final int LOCATION_TYPE_STOP_OR_PLATFORM = 0;
    private static final int LOCATION_TYPE_STATION = 1;
    private static final int LOCATION_TYPE_ENTRANCE = 2;
    private static final int LOCATION_TYPE_GENERIC_NODE = 3;
    private static final int LOCATION_TYPE_BOARDING_AREA = 4;
    private static final String STOP_NAME = "stop_name";
    private static final String STOP_LAT = "stop_lat";
    private static final String STOP_LON = "stop_lon";
    private static final String STOP_CODE = "stop_code";
    private static final String STOP_DESC = "stop_desc";
    private static final String ZONE_ID = "zone_id";
    private static final String STOP_URL = "stop_url";
    private static final String PARENT_STATION = "parent_station";
    private static final String STOP_TIMEZONE = "stop_timezone";
    private static final String WHEELCHAIR_BOARDING = "wheelchair_boarding";
    private static final String LEVEL_ID = "level_id";
    private static final String PLATFORM_CODE = "platform_code";

    @Test
    void validatedParsedStopOrPlatformShouldCreateStopOrPlatformEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final StopOrPlatform mockStopOrPlatform = mock(StopOrPlatform.class);
        final ParsedEntity mockParsedStopOrPlatform = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockStopOrPlatform);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedStopOrPlatform.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);
        when(mockParsedStopOrPlatform.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedStopOrPlatform.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedStopOrPlatform.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedStopOrPlatform.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedStopOrPlatform.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedStopOrPlatform.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedStopOrPlatform.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedStopOrPlatform.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedStopOrPlatform.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedStopOrPlatform.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedStopOrPlatform.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        when(mockGtfsDataRepo.addStop(mockStopOrPlatform)).thenReturn(mockStopOrPlatform);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStopOrPlatform);

        underTest.execute(fakePreprocessedStopMap);

        final InOrder inOrder = inOrder(mockStopOrPlatformBuilder, mockGtfsDataRepo, mockParsedStopOrPlatform);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStopOrPlatform, times(2)).getEntityId();
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedStopOrPlatform, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedStopOrPlatform, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockStopOrPlatformBuilder, times(1)).clear();
        verify(mockStopOrPlatformBuilder,
                times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockStopOrPlatformBuilder,
                times(1)).wheelchairBoarding(ArgumentMatchers.eq(1));
        verify(mockStopOrPlatformBuilder,
                times(1)).platformCode(ArgumentMatchers.eq(PLATFORM_CODE));
        verify(mockStopOrPlatformBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockStopOrPlatformBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockStopOrPlatformBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockStopOrPlatformBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockStopOrPlatformBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockStopOrPlatformBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockStopOrPlatformBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockStopOrPlatformBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockStopOrPlatformBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockStopOrPlatformBuilder, times(1)).childrenList(ArgumentMatchers.isNull());

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockStopOrPlatformBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo,
                times(1)).addStop(ArgumentMatchers.eq(mockStopOrPlatform));

        verifyNoMoreInteractions(mockStopOrPlatformBuilder,
                mockResultRepo, mockGtfsDataRepo, mockParsedStopOrPlatform, mockGenericObject);
    }

    @Test
    void validatedParsedStationShouldCreateStationEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final Station mockStation = mock(Station.class);
        final ParsedEntity mockParsedStation = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockStation);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedStation.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedStation.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);
        when(mockParsedStation.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedStation.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedStation.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedStation.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedStation.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedStation.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedStation.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedStation.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedStation.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedStation.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedStation.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        when(mockGtfsDataRepo.addStop(mockStation)).thenReturn(mockStation);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mockStationBuilder,
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStation);

        underTest.execute(fakePreprocessedStopMap);

        final InOrder inOrder = inOrder(mockStationBuilder, mockGtfsDataRepo, mockParsedStation);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStation, times(2)).getEntityId();
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedStation, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedStation, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockStationBuilder, times(1)).clear();
        verify(mockStationBuilder,
                times(1)).wheelchairBoarding(ArgumentMatchers.eq(1));
        verify(mockStationBuilder,
                times(1)).levelId(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockStationBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockStationBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockStationBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockStationBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockStationBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockStationBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockStationBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockStationBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockStationBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockStationBuilder, times(1)).childrenList(ArgumentMatchers.isNull());

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockStationBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo,
                times(1)).addStop(ArgumentMatchers.eq(mockStation));

        verifyNoMoreInteractions(mockStationBuilder,
                mockResultRepo, mockGtfsDataRepo, mockStation, mockGenericObject);
    }

    @Test
    void validatedParsedEntranceShouldCreateEntranceEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final Entrance mockEntrance = mock(Entrance.class);
        final ParsedEntity mockParsedEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockEntrance);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedEntrance.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(mockParsedEntrance.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedEntrance.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedEntrance.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedEntrance.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedEntrance.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedEntrance.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedEntrance.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedEntrance.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedEntrance.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedEntrance.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedEntrance.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        when(mockGtfsDataRepo.addStop(mockEntrance)).thenReturn(mockEntrance);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final InOrder inOrder = inOrder(mockEntranceBuilder, mockGtfsDataRepo, mockParsedEntrance);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedEntrance, times(2)).getEntityId();
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedEntrance, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedEntrance, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockEntranceBuilder, times(1)).clear();
        verify(mockEntranceBuilder, times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockEntranceBuilder,
                times(1)).wheelchairBoarding(ArgumentMatchers.eq(1));
        verify(mockEntranceBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockEntranceBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockEntranceBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockEntranceBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockEntranceBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockEntranceBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockEntranceBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockEntranceBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockEntranceBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockEntranceBuilder, times(1)).childrenList(ArgumentMatchers.isNull());

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockEntranceBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo,
                times(1)).addStop(ArgumentMatchers.eq(mockEntrance));

        verifyNoMoreInteractions(mockEntranceBuilder,
                mockResultRepo, mockGtfsDataRepo, mockEntrance, mockGenericObject);
    }

    @Test
    void validatedParsedGenericNodeShouldCreateGenericNodeEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final GenericNode mockGenericNode = mock(GenericNode.class);
        final ParsedEntity mockParsedGenericNode = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockGenericNode);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedGenericNode.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);
        when(mockParsedGenericNode.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedGenericNode.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedGenericNode.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedGenericNode.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedGenericNode.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedGenericNode.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedGenericNode.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedGenericNode.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedGenericNode.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedGenericNode.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedGenericNode.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        when(mockGtfsDataRepo.addStop(mockGenericNode)).thenReturn(mockGenericNode);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedGenericNode);

        underTest.execute(fakePreprocessedStopMap);

        final InOrder inOrder = inOrder(mockGenericNodeBuilder, mockGtfsDataRepo, mockParsedGenericNode);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedGenericNode, times(2)).getEntityId();
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedGenericNode, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedGenericNode, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockGenericNodeBuilder, times(1)).clear();
        verify(mockGenericNodeBuilder, times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockGenericNodeBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockGenericNodeBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockGenericNodeBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockGenericNodeBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockGenericNodeBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockGenericNodeBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockGenericNodeBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockGenericNodeBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockGenericNodeBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockGenericNodeBuilder, times(1)).childrenList(ArgumentMatchers.isNull());

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockGenericNodeBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo,
                times(1)).addStop(ArgumentMatchers.eq(mockGenericNode));

        verifyNoMoreInteractions(mockGenericNodeBuilder,
                mockResultRepo, mockGtfsDataRepo, mockGenericNode, mockGenericObject);
    }

    @Test
    void validatedParsedBoardingAreaShouldCreateBoardingEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final BoardingArea mockBoardingArea = mock(BoardingArea.class);
        final ParsedEntity mockParsedBoardingArea = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockBoardingArea);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedBoardingArea.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);
        when(mockParsedBoardingArea.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedBoardingArea.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedBoardingArea.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedBoardingArea.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedBoardingArea.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedBoardingArea.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedBoardingArea.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedBoardingArea.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedBoardingArea.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedBoardingArea.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedBoardingArea.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        when(mockGtfsDataRepo.addStop(mockBoardingArea)).thenReturn(mockBoardingArea);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedBoardingArea);

        underTest.execute(fakePreprocessedStopMap);

        final InOrder inOrder = inOrder(mockBoardingAreaBuilder, mockGtfsDataRepo, mockParsedBoardingArea);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedBoardingArea, times(2)).getEntityId();
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedBoardingArea, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedBoardingArea, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockBoardingAreaBuilder, times(1)).clear();
        verify(mockBoardingAreaBuilder, times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockBoardingAreaBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockBoardingAreaBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockBoardingAreaBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockBoardingAreaBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockBoardingAreaBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockBoardingAreaBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockBoardingAreaBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockBoardingAreaBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockBoardingAreaBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockBoardingAreaBuilder, times(1)).childrenList(ArgumentMatchers.isNull());

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockBoardingAreaBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo,
                times(1)).addStop(ArgumentMatchers.eq(mockBoardingArea));

        verifyNoMoreInteractions(mockBoardingAreaBuilder,
                mockResultRepo, mockGtfsDataRepo, mockBoardingArea, mockGenericObject);
    }

    @Test
    void invalidStopShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        // stopOrPlatform is default code path
        final StopOrPlatform.StopOrPlatformBuilder mockBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedStop = mock(ParsedEntity.class);

        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.isSuccess()).thenReturn(false);
        when(mockGenericObject.getData()).thenReturn(mockNoticeCollection);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedStop.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedStop.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedStop.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedStop.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedStop.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedStop.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedStop.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedStop.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedStop.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedStop.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockParsedStop.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedStop.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mockBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStop);

        underTest.execute(fakePreprocessedStopMap);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStop, times(2)).getEntityId();
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedStop, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder,
                times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockBuilder,
                times(1)).wheelchairBoarding(ArgumentMatchers.eq(1));
        verify(mockBuilder,
                times(1)).platformCode(ArgumentMatchers.eq(PLATFORM_CODE));
        verify(mockBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockBuilder, times(1)).childrenList(ArgumentMatchers.isNull());
        verify(mockBuilder, times(1)).build();


        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(MissingRequiredValueNotice.class));
        verifyNoMoreInteractions(mockBuilder,
                mockResultRepo, mockGtfsDataRepo, mockParsedStop, mockGenericObject);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void duplicateStopShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        // stopOrPlatform is default code path
        final StopOrPlatform.StopOrPlatformBuilder mockBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedStop = mock(ParsedEntity.class);
        final LocationBase mockStop = mock(LocationBase.class);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        when(mockGenericObject.getData()).thenReturn(mockStop);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addStop(mockStop)).thenReturn(null);

        when(mockParsedStop.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedStop.get(STOP_NAME)).thenReturn(STOP_NAME);
        when(mockParsedStop.get(STOP_LAT)).thenReturn(45.5017f);
        when(mockParsedStop.get(STOP_LON)).thenReturn(73.5673f);
        when(mockParsedStop.get(STOP_CODE)).thenReturn(STOP_CODE);
        when(mockParsedStop.get(STOP_DESC)).thenReturn(STOP_DESC);
        when(mockParsedStop.get(ZONE_ID)).thenReturn(ZONE_ID);
        when(mockParsedStop.get(STOP_URL)).thenReturn(STOP_URL);
        when(mockParsedStop.get(STOP_TIMEZONE)).thenReturn(STOP_TIMEZONE);
        when(mockParsedStop.get(WHEELCHAIR_BOARDING)).thenReturn(0);
        when(mockParsedStop.get(LEVEL_ID)).thenReturn(LEVEL_ID);
        when(mockParsedStop.get(PLATFORM_CODE)).thenReturn(PLATFORM_CODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mockBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStop);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParsedStop, times(2)).getEntityId();
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(LOCATION_TYPE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_NAME));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_LAT));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_LON));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_CODE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_DESC));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(ZONE_ID));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_URL));
        verify(mockParsedStop, times(2)).get(ArgumentMatchers.eq(PARENT_STATION));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(LEVEL_ID));
        verify(mockParsedStop, times(1)).get(ArgumentMatchers.eq(PLATFORM_CODE));

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedStop, times(2)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addStop(ArgumentMatchers.isA(LocationBase.class));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder,
                times(1)).parentStation(ArgumentMatchers.isNull());
        verify(mockBuilder,
                times(1)).wheelchairBoarding(ArgumentMatchers.eq(0));
        verify(mockBuilder,
                times(1)).platformCode(ArgumentMatchers.eq(PLATFORM_CODE));
        verify(mockBuilder, times(1)).stopId(ArgumentMatchers.eq(CHILD_ID));
        verify(mockBuilder, times(1)).stopName(ArgumentMatchers.eq(STOP_NAME));
        verify(mockBuilder, times(1)).stopLat(ArgumentMatchers.eq(45.5017f));
        verify(mockBuilder, times(1)).stopLon(ArgumentMatchers.eq(73.5673f));
        verify(mockBuilder, times(1)).stopCode(ArgumentMatchers.eq(STOP_CODE));
        verify(mockBuilder, times(1)).stopDesc(ArgumentMatchers.eq(STOP_DESC));
        verify(mockBuilder, times(1)).zoneId(ArgumentMatchers.eq(ZONE_ID));
        verify(mockBuilder, times(1)).stopUrl(ArgumentMatchers.eq(STOP_URL));
        verify(mockBuilder,
                times(1)).stopTimezone(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockBuilder, times(1)).childrenList(ArgumentMatchers.isNull());
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("stops.txt", noticeList.get(0).getFilename());
        assertEquals("stop_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(CHILD_ID, noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedStop, mockStop,
                mockGenericObject);
    }

    //TODO: tests for parent <--> child relationship
    //TODO: tests for invalid parent location type
    //TODO: tests for wheelchair boarding taken from parent when it should (wheelchairBoarding = 0)
}