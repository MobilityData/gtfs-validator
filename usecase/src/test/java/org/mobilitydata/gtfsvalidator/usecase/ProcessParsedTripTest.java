package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedTripTest {

    @Test
    public void validatedTripShouldCreateTripAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        final Trip mockTrip = mock(Trip.class);
        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        //result of method .getData() is not used here, therefore removing this warning to avoid lint
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTrip).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        when(mockGtfsDataRepo.addTrip(mockTrip)).thenReturn(mockTrip);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedTrip);

        final InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockBuilder);

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1))
                .get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockBuilder, times(1)).routeId("route_id");
        verify(mockBuilder, times(1)).serviceId("service_id");
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(1);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockGtfsDataRepo, times(1)).addTrip(ArgumentMatchers.eq(mockTrip));

        verifyNoMoreInteractions(mockBuilder, mockTrip, mockParsedTrip, mockGtfsDataRepo);
    }

    @Test
    public void invalidTripShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

        final ArrayList<Notice> mockNoticeCollection = spy(new ArrayList<>());
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(false);
        // result of method .getData() is not used here, therefore removing this warning to avoid lint
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockGenericObject).getData();

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedTrip.get("route_id")).thenReturn(null);
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        underTest.execute(mockParsedTrip);

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1))
                .get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        // parameter of method .routeId() is annotated as non null, removing this warning for the purpose of the test
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).routeId(null);
        verify(mockBuilder, times(1)).serviceId("service_id");
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(1);
        verify(mockBuilder, times(1)).build();

        verify(mockResultRepo, times(1)).addNotice(isA(MissingRequiredValueNotice.class));
        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void duplicateTripShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);
        final Trip mockTrip = mock(Trip.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        //result of method .getData() is not used here, therefore removing this warning to avoid lint
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockTrip).when(mockGenericObject).getData();

        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(4);

        underTest.execute(mockParsedTrip);

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1))
                .get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockGtfsDataRepo, times(1)).addTrip(ArgumentMatchers.isA(Trip.class));

        verify(mockBuilder, times(1)).routeId("route_id");
        verify(mockBuilder, times(1)).serviceId("service_id");
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(4);
        verify(mockBuilder, times(1)).build();

        //result of method .getEntityId() is not used here, therefore removing this warning to avoid lint
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("trip_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedTrip, mockTrip);
    }
}