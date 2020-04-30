package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.BikesAllowedStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.WheelchairAccessibleStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TripTest {

    // Field routeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullRouteIdShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId(null)
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("route_id", noticeList.get(0).getFieldName());
        assertEquals("trip id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullServiceIdShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId(null)
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("trip id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field tripId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullTripIdShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId(null)
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("trip_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createTripWithInvalidDirectionIdShouldGenerateUnexpectedEnumValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(3)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("direction_id", noticeList.get(0).getFieldName());
        assertEquals("trip id", noticeList.get(0).getEntityId());
        assertEquals("3", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createTripWithValidDirectionIdShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final Trip toCheck = underTest.build();

        verify(mockNoticeCollection, times(1)).clear();

        assertEquals("route id", toCheck.getRouteId());
        assertEquals("service id", toCheck.getServiceId());
        assertEquals("trip id", toCheck.getTripId());
        assertEquals("test", toCheck.getTripHeadsign());
        assertEquals("test", toCheck.getTripShortName());
        assertEquals(DirectionId.INBOUND, toCheck.getDirectionId());
        assertEquals("test", toCheck.getBlockId());
        assertEquals("test", toCheck.getShapeId());
        assertEquals(WheelchairAccessibleStatus.WHEELCHAIR_ACCESSIBLE, toCheck.getWheelchairAccessibleStatus());
        assertEquals(BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE, toCheck.getBikesAllowedStatus());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createTripWithInvalidWheelchairAccessibleShouldGenerateUnexpectedEnumValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(4)
                .bikesAllowed(0);

        underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("wheelchair_accessible", noticeList.get(0).getFieldName());
        assertEquals("trip id", noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createTripWithInvalidBikesAllowedShouldThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Trip.TripBuilder underTest = new Trip.TripBuilder(mockNoticeCollection);

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(4);

        underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("bikes_allowed", noticeList.get(0).getFieldName());
        assertEquals("trip id", noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockNoticeCollection);
    }
}