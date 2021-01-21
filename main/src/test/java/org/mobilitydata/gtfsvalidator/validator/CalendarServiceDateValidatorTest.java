package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class CalendarServiceDateValidatorTest {
    @Mock
    final GtfsCalendarTableContainer mockCalendarTable = mock(GtfsCalendarTableContainer.class);
    @InjectMocks
    final CalendarServiceDateValidator underTest = new CalendarServiceDateValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void startDateBeforeEndDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsCalendar> calendarCollection = new ArrayList<>();
        GtfsCalendar mockCalendar = mock(GtfsCalendar.class);
        when(mockCalendar.hasEndDate()).thenReturn(true);
        when(mockCalendar.hasStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        when(mockCalendar.startDate()).thenReturn(mockStartDate);
        when(mockCalendar.endDate()).thenReturn(mockEndDate);
        calendarCollection.add(mockCalendar);
        when(mockCalendarTable.getEntities()).thenReturn(calendarCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockCalendar, times(1)).hasEndDate();
        verify(mockCalendar, times(1)).endDate();
        verify(mockCalendar, times(1)).hasStartDate();
        verify(mockCalendar, times(1)).startDate();
        verify(mockStartDate, times(1)).isAfter(mockEndDate);
        verifyNoMoreInteractions(mockCalendar, mockEndDate, mockStartDate);
    }

    @Test
    public void startDateAfterEndDateShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsCalendar> calendarCollection = new ArrayList<>();
        GtfsCalendar mockCalendar = mock(GtfsCalendar.class);
        when(mockCalendar.hasEndDate()).thenReturn(true);
        when(mockCalendar.hasStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(true);
        when(mockCalendar.startDate()).thenReturn(mockStartDate);
        when(mockStartDate.toYYYYMMDD()).thenReturn("start date value");
        when(mockEndDate.toYYYYMMDD()).thenReturn("end date value");
        when(mockCalendar.endDate()).thenReturn(mockEndDate);
        when(mockCalendar.csvRowNumber()).thenReturn(2L);
        when(mockCalendar.serviceId()).thenReturn("service id value");
        calendarCollection.add(mockCalendar);
        when(mockCalendarTable.getEntities()).thenReturn(calendarCollection);
        when(mockCalendarTable.gtfsFilename()).thenReturn("calendar.txt");

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StartAndEndDateOutOfOrderNotice> captor =
                ArgumentCaptor.forClass(StartAndEndDateOutOfOrderNotice.class);

        verify(mockNoticeContainer, times(1)).addValidationNotice(captor.capture());
        StartAndEndDateOutOfOrderNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("start_and_end_date_out_of_order");
        assertThat(notice.getContext()).containsEntry("filename", "calendar.txt");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(notice.getContext()).containsEntry("entityId", "service id value");
        assertThat(notice.getContext()).containsEntry("startDate", "start date value");
        assertThat(notice.getContext()).containsEntry("endDate", "end date value");

        verify(mockCalendar, times(1)).hasEndDate();
        verify(mockCalendar, times(1)).hasStartDate();
        verify(mockCalendar, times(1)).serviceId();
        verify(mockCalendarTable, times(1)).gtfsFilename();
        verify(mockCalendar, times(1)).csvRowNumber();
        verify(mockCalendar, times(2)).endDate();
        verify(mockCalendar, times(2)).startDate();
        verify(mockStartDate, times(1)).isAfter(mockEndDate);
        verify(mockStartDate, times(1)).toYYYYMMDD();
        verify(mockEndDate, times(1)).toYYYYMMDD();
        verifyNoMoreInteractions(mockCalendar, mockEndDate, mockStartDate);
    }

    @Test
    public void noStartDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsCalendar> calendarCollection = new ArrayList<>();
        GtfsCalendar mockCalendar = mock(GtfsCalendar.class);
        when(mockCalendar.hasEndDate()).thenReturn(true);
        when(mockCalendar.hasStartDate()).thenReturn(false);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        when(mockCalendar.endDate()).thenReturn(mockEndDate);
        calendarCollection.add(mockCalendar);
        when(mockCalendarTable.getEntities()).thenReturn(calendarCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockCalendar, times(1)).hasStartDate();
        verifyNoMoreInteractions(mockCalendar, mockEndDate, mockStartDate);
    }

    @Test
    public void noEndDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsCalendar> calendarCollection = new ArrayList<>();
        GtfsCalendar mockCalendar = mock(GtfsCalendar.class);
        when(mockCalendar.hasEndDate()).thenReturn(false);
        when(mockCalendar.hasStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        calendarCollection.add(mockCalendar);
        when(mockCalendarTable.getEntities()).thenReturn(calendarCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockCalendar, times(1)).hasEndDate();
        verify(mockCalendar, times(1)).hasStartDate();
        verifyNoMoreInteractions(mockCalendar, mockEndDate, mockStartDate);
    }
}
