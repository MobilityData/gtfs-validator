package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessParsedTripTest {

    @Test
    public void processTripWithValidValuesShouldNotThrowExceptionAndShouldBeAddedToGtfsDataRepo() throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = mock(Trip.TripBuilder.class, RETURNS_SELF);
        when(mockBuilder.routeId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.serviceId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.tripId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.tripHeadsign(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.tripShortName(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.directionId(anyInt())).thenReturn(mockBuilder);
        when(mockBuilder.blockId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.shapeId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.wheelchairAccessible(anyInt())).thenReturn(mockBuilder);
        when(mockBuilder.bikesAllowed(anyInt())).thenReturn(mockBuilder);

        final Trip mockTrip = mock(Trip.class);
        when(mockBuilder.build()).thenReturn(mockTrip);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedTrip = mock(ParsedEntity.class);
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
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
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

    @SuppressWarnings("ConstantConditions")
    @Test
    public void processTripWithNullRouteIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

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

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("field route_id can not be null", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

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

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("route_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processTripWithNullServiceIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn(null);
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("field service_id can not be null", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockBuilder, times(1)).routeId("route_id");
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).serviceId(null);
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(1);

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processTripWithNullTripIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn(null);
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("field trip_id can not be null", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockBuilder, times(1)).routeId("route_id");
        verify(mockBuilder, times(1)).serviceId("service_id");
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).tripId(null);
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(1);

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("trip_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processTripWithInvalidDirectionIdShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(4);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(1);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("unexpected value found for field direction_id", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockBuilder, times(1)).routeId("route_id");
        verify(mockBuilder, times(1)).serviceId("service_id");
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(4);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(1);
        verify(mockBuilder, times(1)).bikesAllowed(1);

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("direction_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processTripWithInvalidWheelchairAccessibleShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

        when(mockParsedTrip.get("route_id")).thenReturn("route_id");
        when(mockParsedTrip.get("service_id")).thenReturn("service_id");
        when(mockParsedTrip.get("trip_id")).thenReturn("trip_id");
        when(mockParsedTrip.get("trip_headsign")).thenReturn("trip_headsign");
        when(mockParsedTrip.get("trip_short_name")).thenReturn("trip_short_name");
        when(mockParsedTrip.get("direction_id")).thenReturn(1);
        when(mockParsedTrip.get("block_id")).thenReturn("block_id");
        when(mockParsedTrip.get("shape_id")).thenReturn("shape_id");
        when(mockParsedTrip.get("wheelchair_accessible")).thenReturn(4);
        when(mockParsedTrip.get("bikes_allowed")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("unexpected value found for field wheelchair_accessible", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("bikes_allowed"));

        verify(mockBuilder, times(1)).routeId("route_id");
        verify(mockBuilder, times(1)).serviceId("service_id");
        verify(mockBuilder, times(1)).tripId("trip_id");
        verify(mockBuilder, times(1)).tripHeadsign("trip_headsign");
        verify(mockBuilder, times(1)).tripShortName("trip_short_name");
        verify(mockBuilder, times(1)).directionId(1);
        verify(mockBuilder, times(1)).blockId("block_id");
        verify(mockBuilder, times(1)).shapeId("shape_id");
        verify(mockBuilder, times(1)).wheelchairAccessible(4);
        verify(mockBuilder, times(1)).bikesAllowed(1);

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("wheelchair_accessible", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processTripWithInvalidBikesAllowedShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        final Trip.TripBuilder mockBuilder = spy(Trip.TripBuilder.class);

        final ProcessParsedTrip underTest = new ProcessParsedTrip(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        final ParsedEntity mockParsedTrip = mock(ParsedEntity.class);

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

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedTrip));

        assertEquals("unexpected value found for field bikes_allowed", exception.getMessage());

        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("route_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_headsign"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("trip_short_name"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("direction_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("block_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("shape_id"));
        verify(mockParsedTrip, times(1)).get(ArgumentMatchers.eq("wheelchair_accessible"));
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
        verify(mockBuilder, times(1)).bikesAllowed(4);

        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedTrip, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor = ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("bikes_allowed", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedTrip, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}