package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithArrivalBeforePreviousDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithOnlyArrivalOrDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class StopTimeArrivalAndDepartureTimeValidatorTest {
    @Mock
    final GtfsStopTimeTableContainer mockStopTimeTable = mock(GtfsStopTimeTableContainer.class);
    @InjectMocks
    final StopTimeArrivalAndDepartureTimeValidator underTest = new StopTimeArrivalAndDepartureTimeValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void departureTimeAndArrivalTimeNotProvidedShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();
        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(false);
        when(mockStopTime.hasDepartureTime()).thenReturn(false);
        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockStopTimeTable, times(1)).byTripIdMap();
    }

    @Test
    public void departureTimeAfterArrivalTimeShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();
        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(true);
        GtfsTime mockArrivalTime = mock(GtfsTime.class);
        when(mockStopTime.arrivalTime()).thenReturn(mockArrivalTime);
        when(mockStopTime.hasDepartureTime()).thenReturn(true);
        GtfsTime mockDepartureTime = mock(GtfsTime.class);
        when(mockStopTime.departureTime()).thenReturn(mockDepartureTime);
        when(mockDepartureTime.isBefore(mockArrivalTime)).thenReturn(false);

        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(mockStopTime, times(1)).hasDepartureTime();
        verify(mockStopTime, times(1)).departureTime();
        verify(mockStopTime, times(1)).hasArrivalTime();
        verify(mockStopTime, times(1)).arrivalTime();
        verify(mockDepartureTime, times(1)).isBefore(mockArrivalTime);

        verifyNoMoreInteractions(mockStopTimeTable, mockArrivalTime, mockDepartureTime, mockStopTime);

    }

    @Test
    public void departureTimeBeforeArrivalTimeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();
        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(true);
        when(mockStopTime.csvRowNumber()).thenReturn(1L);
        when(mockStopTime.tripId()).thenReturn("trip id value");
        when(mockStopTime.stopSequence()).thenReturn(2);
        GtfsTime mockArrivalTime = mock(GtfsTime.class);
        when(mockStopTime.arrivalTime()).thenReturn(mockArrivalTime);
        when(mockArrivalTime.toHHMMSS()).thenReturn("arrival time value");
        when(mockStopTime.hasDepartureTime()).thenReturn(true);
        GtfsTime mockDepartureTime = mock(GtfsTime.class);
        when(mockStopTime.departureTime()).thenReturn(mockDepartureTime);
        when(mockDepartureTime.isBefore(mockArrivalTime)).thenReturn(true);
        when(mockDepartureTime.toHHMMSS()).thenReturn("departure time value");

        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StopTimeWithDepartureBeforeArrivalTimeNotice> captor =
                ArgumentCaptor.forClass(StopTimeWithDepartureBeforeArrivalTimeNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        StopTimeWithDepartureBeforeArrivalTimeNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("stop_time_with_departure_before_arrival_time");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("tripId", "trip id value");
        assertThat(notice.getContext()).containsEntry("stopSequence", 2);
        assertThat(notice.getContext()).containsEntry("departureTime", "departure time value");
        assertThat(notice.getContext()).containsEntry("arrivalTime", "arrival time value");

        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(mockStopTime, times(1)).hasDepartureTime();
        verify(mockStopTime, times(2)).departureTime();
        verify(mockStopTime, times(1)).hasArrivalTime();
        verify(mockStopTime, times(2)).arrivalTime();
        verify(mockStopTime, times(1)).csvRowNumber();
        verify(mockStopTime, times(1)).tripId();
        verify(mockStopTime, times(1)).stopSequence();
        verify(mockDepartureTime, times(1)).isBefore(mockArrivalTime);
        verify(mockArrivalTime, times(1)).toHHMMSS();
        verify(mockDepartureTime, times(1)).toHHMMSS();

        verifyNoMoreInteractions(mockStopTimeTable, mockArrivalTime, mockDepartureTime, mockStopTime);
    }

    @Test
    public void stopTimeWithArrivalBeforePreviousDepartureTimeShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();

        GtfsStopTime previousStopTime = mock(GtfsStopTime.class);
        when(previousStopTime.hasArrivalTime()).thenReturn(true);
        GtfsTime previousArrivalTime = mock(GtfsTime.class);
        when(previousStopTime.arrivalTime()).thenReturn(previousArrivalTime);
        when(previousStopTime.hasDepartureTime()).thenReturn(true);
        when(previousStopTime.csvRowNumber()).thenReturn(0L);
        GtfsTime previousDepartureTime = mock(GtfsTime.class);
        when(previousStopTime.arrivalTime()).thenReturn(previousArrivalTime);
        when(previousStopTime.departureTime()).thenReturn(previousDepartureTime);
        when(previousDepartureTime.isBefore(previousArrivalTime)).thenReturn(false);
        when(previousDepartureTime.toHHMMSS()).thenReturn("previous departure time value");
        when(previousArrivalTime.toHHMMSS()).thenReturn("previous arrival time value");

        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(true);
        GtfsTime arrivalTime = mock(GtfsTime.class);
        when(mockStopTime.arrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.hasDepartureTime()).thenReturn(true);
        when(mockStopTime.tripId()).thenReturn("trip id value");
        when(mockStopTime.csvRowNumber()).thenReturn(1L);
        GtfsTime departureTime = mock(GtfsTime.class);
        when(arrivalTime.toHHMMSS()).thenReturn("arrival time value");
        when(departureTime.toHHMMSS()).thenReturn("departure time value");
        when(mockStopTime.arrivalTime()).thenReturn(arrivalTime);
        when(mockStopTime.departureTime()).thenReturn(departureTime);
        when(departureTime.isBefore(arrivalTime)).thenReturn(false);

        when(arrivalTime.isBefore(previousDepartureTime)).thenReturn(true);

        stopTimeCollection.put("trip id value", previousStopTime);
        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StopTimeWithArrivalBeforePreviousDepartureTimeNotice> captor =
                ArgumentCaptor.forClass(StopTimeWithArrivalBeforePreviousDepartureTimeNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        StopTimeWithArrivalBeforePreviousDepartureTimeNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("stop_time_with_arrival_before_previous_departure_time");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("prevCsvRowNumber", 0L);
        assertThat(notice.getContext()).containsEntry("tripId", "trip id value");
        assertThat(notice.getContext()).containsEntry("arrivalTime", "arrival time value");
        assertThat(notice.getContext()).containsEntry("departureTime", "previous departure time value");

        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(previousStopTime, times(1)).hasDepartureTime();
        verify(previousStopTime, times(3)).departureTime();
        verify(previousStopTime, times(1)).hasArrivalTime();
        verify(previousStopTime, times(1)).arrivalTime();
        verify(mockStopTime, times(1)).hasDepartureTime();
        verify(mockStopTime, times(1)).departureTime();
        verify(mockStopTime, times(1)).hasArrivalTime();
        verify(mockStopTime, times(3)).arrivalTime();
        verify(previousDepartureTime, times(1)).isBefore(previousArrivalTime);
        verify(departureTime, times(1)).isBefore(arrivalTime);
        verify(arrivalTime, times(1)).isBefore(previousDepartureTime);
        verify(arrivalTime, times(1)).toHHMMSS();
        verify(previousDepartureTime, times(1)).toHHMMSS();
        verify(previousStopTime, times(1)).csvRowNumber();
        verify(mockStopTime, times(1)).csvRowNumber();
        verify(mockStopTime, times(1)).tripId();

        verifyNoMoreInteractions(mockStopTimeTable, previousArrivalTime, previousDepartureTime,
                previousStopTime, arrivalTime, departureTime, mockStopTime);
    }

    @Test
    public void stopTimeWithArrivalAfterPreviousDepartureTimeShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();

        GtfsStopTime mockStopTime0 = mock(GtfsStopTime.class);
        when(mockStopTime0.hasArrivalTime()).thenReturn(true);
        GtfsTime mockArrivalTime0 = mock(GtfsTime.class);
        when(mockStopTime0.arrivalTime()).thenReturn(mockArrivalTime0);
        when(mockStopTime0.hasDepartureTime()).thenReturn(true);
        GtfsTime mockDepartureTime0 = mock(GtfsTime.class);
        when(mockStopTime0.departureTime()).thenReturn(mockDepartureTime0);
        when(mockDepartureTime0.isBefore(mockArrivalTime0)).thenReturn(false);

        GtfsStopTime mockStopTime1 = mock(GtfsStopTime.class);
        when(mockStopTime1.hasArrivalTime()).thenReturn(true);
        GtfsTime mockArrivalTime1 = mock(GtfsTime.class);
        when(mockStopTime1.arrivalTime()).thenReturn(mockArrivalTime1);
        when(mockStopTime1.hasDepartureTime()).thenReturn(true);
        GtfsTime mockDepartureTime1 = mock(GtfsTime.class);
        when(mockStopTime1.departureTime()).thenReturn(mockDepartureTime1);
        when(mockDepartureTime1.isBefore(mockArrivalTime1)).thenReturn(false);

        when(mockArrivalTime1.isBefore(mockDepartureTime0)).thenReturn(false);

        stopTimeCollection.put("trip id value", mockStopTime0);
        stopTimeCollection.put("trip id value", mockStopTime1);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(mockStopTime0, times(1)).hasDepartureTime();
        verify(mockStopTime0, times(2)).departureTime();
        verify(mockStopTime0, times(1)).hasArrivalTime();
        verify(mockStopTime0, times(1)).arrivalTime();
        verify(mockStopTime1, times(1)).hasDepartureTime();
        verify(mockStopTime1, times(1)).departureTime();
        verify(mockStopTime1, times(1)).hasArrivalTime();
        verify(mockStopTime1, times(2)).arrivalTime();
        verify(mockDepartureTime0, times(1)).isBefore(mockArrivalTime0);
        verify(mockDepartureTime1, times(1)).isBefore(mockArrivalTime1);
        verify(mockArrivalTime1, times(1)).isBefore(mockDepartureTime0);

        verifyNoMoreInteractions(mockStopTimeTable, mockArrivalTime0, mockDepartureTime0, mockStopTime0,
                mockArrivalTime1, mockDepartureTime1, mockStopTime1);
    }

    @Test
    public void missingArrivalTimeShouldGenerateNoticeIfDepartureTimeIsProvided() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();
        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(true);
        when(mockStopTime.csvRowNumber()).thenReturn(1L);
        when(mockStopTime.tripId()).thenReturn("trip id value");
        when(mockStopTime.stopSequence()).thenReturn(2);
        when(mockStopTime.hasDepartureTime()).thenReturn(false);

        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StopTimeWithOnlyArrivalOrDepartureTimeNotice> captor =
                ArgumentCaptor.forClass(StopTimeWithOnlyArrivalOrDepartureTimeNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        StopTimeWithOnlyArrivalOrDepartureTimeNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("stop_time_with_only_arrival_or_departure_time");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("tripId", "trip id value");
        assertThat(notice.getContext()).containsEntry("stopSequence", 2);
        assertThat(notice.getContext())
                .containsEntry("specifiedField", GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME);

        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(mockStopTime, times(1)).hasDepartureTime();
        verify(mockStopTime, times(1)).hasArrivalTime();
        verify(mockStopTime, times(1)).csvRowNumber();
        verify(mockStopTime, times(1)).tripId();
        verify(mockStopTime, times(1)).stopSequence();

        verifyNoMoreInteractions(mockStopTimeTable, mockStopTime);
    }

    @Test
    public void missingDepartureTimeShouldGenerateNoticeIfArrivalTimeIsProvided() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        ListMultimap<String, GtfsStopTime> stopTimeCollection = ArrayListMultimap.create();
        GtfsStopTime mockStopTime = mock(GtfsStopTime.class);
        when(mockStopTime.hasArrivalTime()).thenReturn(false);
        when(mockStopTime.csvRowNumber()).thenReturn(1L);
        when(mockStopTime.tripId()).thenReturn("trip id value");
        when(mockStopTime.stopSequence()).thenReturn(2);
        when(mockStopTime.hasDepartureTime()).thenReturn(true);

        stopTimeCollection.put("trip id value", mockStopTime);
        when(mockStopTimeTable.byTripIdMap()).thenReturn(stopTimeCollection);

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StopTimeWithOnlyArrivalOrDepartureTimeNotice> captor =
                ArgumentCaptor.forClass(StopTimeWithOnlyArrivalOrDepartureTimeNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        StopTimeWithOnlyArrivalOrDepartureTimeNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("stop_time_with_only_arrival_or_departure_time");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 1L);
        assertThat(notice.getContext()).containsEntry("tripId", "trip id value");
        assertThat(notice.getContext()).containsEntry("stopSequence", 2);
        assertThat(notice.getContext())
                .containsEntry("specifiedField", GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME);

        verify(mockStopTimeTable, times(1)).byTripIdMap();
        verify(mockStopTime, times(1)).hasDepartureTime();
        verify(mockStopTime, times(1)).hasArrivalTime();
        verify(mockStopTime, times(1)).csvRowNumber();
        verify(mockStopTime, times(1)).tripId();
        verify(mockStopTime, times(1)).stopSequence();

        verifyNoMoreInteractions(mockStopTimeTable, mockStopTime);
    }
}
