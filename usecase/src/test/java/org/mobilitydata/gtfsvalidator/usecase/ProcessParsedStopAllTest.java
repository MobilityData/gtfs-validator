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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ParentStationInvalidLocationTypeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StationWithParentStationNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.*;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
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
    void validParsedStopOrPlatformShouldCreateStopOrPlatformEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final StopOrPlatform mockStopOrPlatform = mock(StopOrPlatform.class);
        final ParsedEntity mockParsedStopOrPlatform = mock(ParsedEntity.class);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockStopOrPlatform);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockFailBuildResult = mock(EntityBuildResult.class);

        when(mockFailBuildResult.getData()).thenReturn(Collections.emptyList());
        when(mockFailBuildResult.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockFailBuildResult);

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

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);

        when(mockGtfsDataRepo.addStop(mockStopOrPlatform)).thenReturn(mockStopOrPlatform);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mockStopOrPlatformBuilder,
                mockStationBuilder,
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStopOrPlatform);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

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
        verify(mockGtfsDataRepo, times(1)).getAgencyCount();

        verifyNoMoreInteractions(mockStopOrPlatformBuilder,
                mockResultRepo, mockGtfsDataRepo, mockParsedStopOrPlatform, mockGenericObject);
    }

    @Test
    void validParsedStationShouldCreateStationEntityAndBeAddedToGtfsDataRepository() {
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
    void validParsedEntranceShouldCreateEntranceEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final Entrance mockEntrance = mock(Entrance.class);
        final ParsedEntity mockParsedEntrance = mock(ParsedEntity.class);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockEntrance);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);


        @SuppressWarnings("rawtypes") final EntityBuildResult mockFailBuildResult = mock(EntityBuildResult.class);

        when(mockFailBuildResult.getData()).thenReturn(Collections.emptyList());
        when(mockFailBuildResult.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockFailBuildResult);

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

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);

        when(mockGtfsDataRepo.addStop(mockEntrance)).thenReturn(mockEntrance);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mockStationBuilder,
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
    void validParsedGenericNodeShouldCreateGenericNodeEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final GenericNode mockGenericNode = mock(GenericNode.class);
        final ParsedEntity mockParsedGenericNode = mock(ParsedEntity.class);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockGenericNode);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);


        @SuppressWarnings("rawtypes") final EntityBuildResult mockFailBuildResult = mock(EntityBuildResult.class);

        when(mockFailBuildResult.getData()).thenReturn(Collections.emptyList());
        when(mockFailBuildResult.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockFailBuildResult);

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

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);

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
    void validParsedBoardingAreaShouldCreateBoardingEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final BoardingArea mockBoardingArea = mock(BoardingArea.class);
        final ParsedEntity mockParsedBoardingArea = mock(ParsedEntity.class);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockBoardingArea);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);


        @SuppressWarnings("rawtypes") final EntityBuildResult mockFailBuildResult = mock(EntityBuildResult.class);

        when(mockFailBuildResult.getData()).thenReturn(Collections.emptyList());
        when(mockFailBuildResult.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockFailBuildResult);

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

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);

        when(mockGtfsDataRepo.addStop(mockBoardingArea)).thenReturn(mockBoardingArea);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mockGtfsDataRepo,
                mockStopOrPlatformBuilder,
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

        final DuplicatedEntityNotice notice = captor.getValue();

        assertEquals("stops.txt", notice.getFilename());
        assertEquals("stop_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals(CHILD_ID, notice.getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedStop, mockStop,
                mockGenericObject);
    }

    @Test
    void childrenAreResolved() {
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        final ParsedEntity mockChild0 = mock(ParsedEntity.class);
        final ParsedEntity mockChild1 = mock(ParsedEntity.class);
        final ParsedEntity mockChild2 = mock(ParsedEntity.class);
        final ParsedEntity mockParent0 = mock(ParsedEntity.class);
        final ParsedEntity mockParent1 = mock(ParsedEntity.class);

        when(mockChild0.getEntityId()).thenReturn(CHILD_ID + "0");
        when(mockChild1.getEntityId()).thenReturn(CHILD_ID + "1");
        when(mockChild2.getEntityId()).thenReturn(CHILD_ID + "2");
        when(mockParent0.getEntityId()).thenReturn(PARENT_ID + "0");
        when(mockParent1.getEntityId()).thenReturn(PARENT_ID + "1");

        when(mockChild0.get(PARENT_STATION)).thenReturn(PARENT_ID + "0");
        when(mockChild1.get(PARENT_STATION)).thenReturn(PARENT_ID + "0");
        when(mockChild2.get(PARENT_STATION)).thenReturn(PARENT_ID + "1");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(mockChild0.getEntityId(), mockChild0);
        fakePreprocessedStopMap.put(mockChild1.getEntityId(), mockChild1);
        fakePreprocessedStopMap.put(mockChild2.getEntityId(), mockChild2);
        fakePreprocessedStopMap.put(mockParent0.getEntityId(), mockParent0);
        fakePreprocessedStopMap.put(mockParent1.getEntityId(), mockParent1);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockStopOrPlatformBuilder, times(1)).childrenList(
                ArgumentMatchers.eq(List.of(mockChild0.getEntityId(), mockChild1.getEntityId()))
        );
        verify(mockStopOrPlatformBuilder, times(1)).childrenList(
                ArgumentMatchers.eq(List.of(mockChild2.getEntityId()))
        );
    }

    @Test
    void stopOrPlatformWheelchairBoardingInheritanceCheck() {
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockParent.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockChild.get(WHEELCHAIR_BOARDING)).thenReturn(0);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));

        verify(mockStopOrPlatformBuilder, times(2)).wheelchairBoarding(1);
    }

    @Test
    void stopOrPlatformWithParentShouldInheritParentTimezone() {
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockParent.get(STOP_TIMEZONE)).thenReturn("PARENT timezone");
        when(mockChild.get(STOP_TIMEZONE)).thenReturn("CHILD timezone");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockStopOrPlatformBuilder, times(2)).stopTimezone("PARENT timezone");
    }

    @Test
    void stopOrPlatformWithoutTimezoneNorParentShouldInheritAgencyTimezone() {
        // when no parent is defined and stop_timezone is not defined, timezone is inherited from agency

        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Agency mockAgency = mock(Agency.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockAgency.getAgencyTimezone()).thenReturn("AGENCY timezone");
        when(mockGtfsDataRepo.getAgencyCount()).thenReturn(1);
        when(mockGtfsDataRepo.getAgencyAll()).thenReturn(List.of(mockAgency));

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mockGtfsDataRepo,
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockStopOrPlatformBuilder, times(1)).stopTimezone("AGENCY timezone");
    }

    @Test
    void stopOrPlatformWithoutParentTimezoneInheritanceCheck() {
        // when no parent is defined and stop_timezone is defined, timezone is NOT inherited from agency

        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockChild.get(STOP_TIMEZONE)).thenReturn("CHILD timezone");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockStopOrPlatformBuilder, times(1)).stopTimezone("CHILD timezone");
    }

    @Test
    void entranceWheelchairBoardingInheritanceCheck() {
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockChild.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(mockParent.get(WHEELCHAIR_BOARDING)).thenReturn(1);
        when(mockChild.get(WHEELCHAIR_BOARDING)).thenReturn(0);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(WHEELCHAIR_BOARDING));

        verify(mockEntranceBuilder, times(1)).wheelchairBoarding(1);
    }

    @Test
    void entranceWithParentShouldInheritParentTimezone() {
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(mockChild.get(LOCATION_TYPE)).thenReturn(2);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(2);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockParent.get(STOP_TIMEZONE)).thenReturn("PARENT timezone");
        when(mockChild.get(STOP_TIMEZONE)).thenReturn("CHILD timezone");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockEntranceBuilder, times(2)).stopTimezone("PARENT timezone");
    }

    @Test
    void stationWithParentShouldAddNoticeToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedStation = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedStation.getEntityId()).thenReturn(CHILD_ID);
        when(mockParsedStation.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);
        when(mockParsedStation.get(PARENT_STATION)).thenReturn(PARENT_ID);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mockStationBuilder,
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockParsedStation);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<StationWithParentStationNotice> captor =
                ArgumentCaptor.forClass(StationWithParentStationNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        StationWithParentStationNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
    }

    @Test
    void stationWithoutTimezoneShouldInheritTimezoneFromAgency() {
        // if not provided, timezone is inherited from agency

        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Agency mockAgency = mock(Agency.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockGenericObject);

        when(mockChild.get(LOCATION_TYPE)).thenReturn(1);

        when(mockAgency.getAgencyTimezone()).thenReturn("AGENCY timezone");
        when(mockGtfsDataRepo.getAgencyCount()).thenReturn(1);
        when(mockGtfsDataRepo.getAgencyAll()).thenReturn(List.of(mockAgency));

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mockGtfsDataRepo,
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mockStationBuilder,
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockStationBuilder, times(1)).stopTimezone("AGENCY timezone");
    }

    @Test
    void genericNodeWithParentShouldInheritParentTimezone() {
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(mockChild.get(LOCATION_TYPE)).thenReturn(3);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(3);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockParent.get(STOP_TIMEZONE)).thenReturn("PARENT timezone");
        when(mockChild.get(STOP_TIMEZONE)).thenReturn("CHILD timezone");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockGenericNodeBuilder, times(2)).stopTimezone("PARENT timezone");
    }

    @Test
    void boardingAreaWithParentShouldInheritParentTimezone() {
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParent = mock(ParsedEntity.class);
        final ParsedEntity mockChild = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(mockChild.get(LOCATION_TYPE)).thenReturn(4);
        when(mockParent.get(LOCATION_TYPE)).thenReturn(4);

        when(mockParent.getEntityId()).thenReturn(PARENT_ID);
        when(mockChild.get(PARENT_STATION)).thenReturn(PARENT_ID);
        when(mockParent.get(STOP_TIMEZONE)).thenReturn("PARENT timezone");
        when(mockChild.get(STOP_TIMEZONE)).thenReturn("CHILD timezone");

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(
                mock(ValidationResultRepository.class),
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, mockChild);
        fakePreprocessedStopMap.put(PARENT_ID, mockParent);

        underTest.execute(fakePreprocessedStopMap);

        verify(mockParent, times(2)).get(ArgumentMatchers.eq(STOP_TIMEZONE));
        verify(mockChild, times(1)).get(ArgumentMatchers.eq(STOP_TIMEZONE));

        verify(mockBoardingAreaBuilder, times(2)).stopTimezone("PARENT timezone");
    }

    @Test
    void stopOrPlatformWithStopOrPlatformParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedStopOrPlatform = mock(ParsedEntity.class);
        final ParsedEntity parentStopOrPlatform = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedStopOrPlatform.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);
        when(childParsedStopOrPlatform.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentStopOrPlatform.getEntityId()).thenReturn(PARENT_ID);
        when(parentStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedStopOrPlatform);
        fakePreprocessedStopMap.put(PARENT_ID, parentStopOrPlatform);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void stopOrPlatformWithEntranceParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedStopOrPlatform = mock(ParsedEntity.class);
        final ParsedEntity parentEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedStopOrPlatform.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);
        when(childParsedStopOrPlatform.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentEntrance.getEntityId()).thenReturn(PARENT_ID);
        when(parentEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedStopOrPlatform);
        fakePreprocessedStopMap.put(PARENT_ID, parentEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void stopOrPlatformWithGenericNodeParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedStopOrPlatform = mock(ParsedEntity.class);
        final ParsedEntity parentGenericNode = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedStopOrPlatform.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);
        when(childParsedStopOrPlatform.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentGenericNode.getEntityId()).thenReturn(PARENT_ID);
        when(parentGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedStopOrPlatform);
        fakePreprocessedStopMap.put(PARENT_ID, parentGenericNode);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void stopOrPlatformWithBoardingAreaParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedStopOrPlatform = mock(ParsedEntity.class);
        final ParsedEntity parentEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedStopOrPlatform.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);
        when(childParsedStopOrPlatform.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentEntrance.getEntityId()).thenReturn(PARENT_ID);
        when(parentEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF),
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedStopOrPlatform);
        fakePreprocessedStopMap.put(PARENT_ID, parentEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void entranceWithStopOrPlatformParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedEntrance = mock(ParsedEntity.class);
        final ParsedEntity parentStopOrPlatform = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedEntrance.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(childParsedEntrance.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentStopOrPlatform.getEntityId()).thenReturn(PARENT_ID);
        when(parentStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedEntrance);
        fakePreprocessedStopMap.put(PARENT_ID, parentStopOrPlatform);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void entranceWithEntranceParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedEntrance = mock(ParsedEntity.class);
        final ParsedEntity parentParsedEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedEntrance.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(childParsedEntrance.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedEntrance.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedEntrance);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void entranceWithGenericNodeParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedEntrance = mock(ParsedEntity.class);
        final ParsedEntity parentGenericNode = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedEntrance.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(childParsedEntrance.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentGenericNode.getEntityId()).thenReturn(PARENT_ID);
        when(parentGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedEntrance);
        fakePreprocessedStopMap.put(PARENT_ID, parentGenericNode);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void entranceWithBoardingAreaParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedEntrance = mock(ParsedEntity.class);
        final ParsedEntity parentParsedBoardingArea = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedEntrance.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);
        when(childParsedEntrance.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedBoardingArea.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedEntrance);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedBoardingArea);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void genericNodeWithStopOrPlatformParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopOrPlatform.StopOrPlatformBuilder mockStopOrPlatformBuilder =
                mock(StopOrPlatform.StopOrPlatformBuilder.class, RETURNS_SELF);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedGenericNode = mock(ParsedEntity.class);
        final ParsedEntity parentParsedStopOrPlatform = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockStopOrPlatformBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedGenericNode.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);
        when(childParsedGenericNode.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedStopOrPlatform.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedStopOrPlatform.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STOP_OR_PLATFORM);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mockStopOrPlatformBuilder,
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class),
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedGenericNode);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedStopOrPlatform);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void genericNodeWithEntranceParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedGenericNode = mock(ParsedEntity.class);
        final ParsedEntity parentParsedEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedGenericNode.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);
        when(childParsedGenericNode.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedEntrance.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mockEntranceBuilder,
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedGenericNode);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void genericNodeWithGenericNodeParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedGenericNode = mock(ParsedEntity.class);
        final ParsedEntity parentGenericNode = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedGenericNode.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);
        when(childParsedGenericNode.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentGenericNode.getEntityId()).thenReturn(PARENT_ID);
        when(parentGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class),
                mockGenericNodeBuilder,
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF)
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedGenericNode);
        fakePreprocessedStopMap.put(PARENT_ID, parentGenericNode);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void genericNodeWithBoardingAreaParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedGenericNode = mock(ParsedEntity.class);
        final ParsedEntity parentParsedBoardingArea = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedGenericNode.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);
        when(childParsedGenericNode.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedBoardingArea.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class, RETURNS_SELF),
                mock(Entrance.EntranceBuilder.class),
                mockGenericNodeBuilder,
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedGenericNode);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedBoardingArea);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void boardingAreaWithStationParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final Station.StationBuilder mockStationBuilder =
                mock(Station.StationBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedBoardingArea = mock(ParsedEntity.class);
        final ParsedEntity parentParsedStation = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockStationBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedBoardingArea.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);
        when(childParsedBoardingArea.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedStation.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedStation.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_STATION);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mockStationBuilder,
                mock(Entrance.EntranceBuilder.class),
                mock(GenericNode.GenericNodeBuilder.class),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedBoardingArea);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedStation);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_STATION, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void boardingAreaWithEntranceParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final Entrance.EntranceBuilder mockEntranceBuilder =
                mock(Entrance.EntranceBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedBoardingArea = mock(ParsedEntity.class);
        final ParsedEntity parentParsedEntrance = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockEntranceBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedBoardingArea.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);
        when(childParsedBoardingArea.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedEntrance.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedEntrance.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_ENTRANCE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class),
                mockEntranceBuilder,
                mock(GenericNode.GenericNodeBuilder.class),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedBoardingArea);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedEntrance);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_ENTRANCE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void boardingAreaWithGenericNodeParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final GenericNode.GenericNodeBuilder mockGenericNodeBuilder =
                mock(GenericNode.GenericNodeBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedBoardingArea = mock(ParsedEntity.class);
        final ParsedEntity parentParsedGenericNode = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);
        //noinspection unchecked
        when(mockGenericNodeBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedBoardingArea.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);
        when(childParsedBoardingArea.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedGenericNode.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedGenericNode.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_GENERIC_NODE);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class),
                mock(Entrance.EntranceBuilder.class),
                mockGenericNodeBuilder,
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedBoardingArea);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedGenericNode);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_GENERIC_NODE, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }

    @Test
    void boardingAreaWithBoardingAreaParentShouldAddNoticeToResultRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final BoardingArea.BoardingAreaBuilder mockBoardingAreaBuilder =
                mock(BoardingArea.BoardingAreaBuilder.class, RETURNS_SELF);
        final ParsedEntity childParsedBoardingArea = mock(ParsedEntity.class);
        final ParsedEntity parentParsedBoardingArea = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(Collections.emptyList());
        when(mockGenericObject.isSuccess()).thenReturn(false);

        //noinspection unchecked
        when(mockBoardingAreaBuilder.build()).thenReturn(mockGenericObject);

        when(childParsedBoardingArea.getEntityId()).thenReturn(CHILD_ID);
        when(childParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);
        when(childParsedBoardingArea.get(PARENT_STATION)).thenReturn(PARENT_ID);

        when(parentParsedBoardingArea.getEntityId()).thenReturn(PARENT_ID);
        when(parentParsedBoardingArea.get(LOCATION_TYPE)).thenReturn(LOCATION_TYPE_BOARDING_AREA);

        final ProcessParsedStopAll underTest = new ProcessParsedStopAll(mockResultRepo,
                mock(GtfsDataRepository.class),
                mock(StopOrPlatform.StopOrPlatformBuilder.class),
                mock(Station.StationBuilder.class),
                mock(Entrance.EntranceBuilder.class),
                mock(GenericNode.GenericNodeBuilder.class),
                mockBoardingAreaBuilder
        );

        Map<String, ParsedEntity> fakePreprocessedStopMap = new HashMap<>();
        fakePreprocessedStopMap.put(CHILD_ID, childParsedBoardingArea);
        fakePreprocessedStopMap.put(PARENT_ID, parentParsedBoardingArea);

        underTest.execute(fakePreprocessedStopMap);

        final ArgumentCaptor<ParentStationInvalidLocationTypeNotice> captor =
                ArgumentCaptor.forClass(ParentStationInvalidLocationTypeNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        ParentStationInvalidLocationTypeNotice notice = captor.getValue();
        assertEquals(CHILD_ID, notice.getEntityId());
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_CHILD_LOCATION_TYPE));
        assertEquals(PARENT_ID, notice.getNoticeSpecific(KEY_PARENT_ID));
        assertEquals(LOCATION_TYPE_STOP_OR_PLATFORM, notice.getNoticeSpecific(KEY_EXPECTED_PARENT_LOCATION_TYPE));
        assertEquals(LOCATION_TYPE_BOARDING_AREA, notice.getNoticeSpecific(KEY_ACTUAL_PARENT_LOCATION_TYPE));
    }
}