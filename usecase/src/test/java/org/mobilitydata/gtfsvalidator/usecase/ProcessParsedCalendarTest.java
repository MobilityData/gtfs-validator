package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessParsedCalendarTest {

    @Test
    public void processValidParsedCalendarShouldAddToGtfsDataRepoWithoutNoticeOrException()
            throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar mockCalendar = mock(Calendar.class);

        Calendar.CalendarBuilder mockBuilder = mock(Calendar.CalendarBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockCalendar);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedCalendar);

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));

        InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockGtfsDataRepo, times(1)).addCalendar(ArgumentMatchers.eq(mockCalendar));

        verifyNoMoreInteractions(mockBuilder, mockCalendar, mockParsedCalendar, mockGtfsDataRepo);
    }

    @Test
    public void processCalendarWithNullServiceIdShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn(null);
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("field service_id can not be null", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).serviceId(null);
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidMondayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(4);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field monday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("monday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidTuesdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(4);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field tuesday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("tuesday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidWednesdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(4);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field wednesday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("wednesday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidThursdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(4);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field thursday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("thursday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidFridayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(4);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field friday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("friday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidSaturdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(4);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field saturday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("saturday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithInvalidSundayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(4);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field sunday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(4));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor = ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("sunday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(1, noticeList.get(0).getRangeMax());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullMondayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(null);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field monday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("monday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullTuesdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(null);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field tuesday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("tuesday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullWednesdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(null);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field wednesday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("wednesday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullThursdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(null);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field thursday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("thursday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullFridayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(null);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field friday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("friday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullSaturdayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(null);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field saturday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("saturday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullSundayShouldThrowExceptionAndIntegerFieldValueOutOfRangeNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(null);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("invalid value found for field sunday", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("sunday", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullStartDateShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(null);
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("field start_date can not be null", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).startDate(null);
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("start_date", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void processCalendarWithNullEndDateShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar.CalendarBuilder mockBuilder = spy(Calendar.CalendarBuilder.class);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("field end_date can not be null", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).endDate(null);
        verify(mockBuilder, times(1)).build();

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("end_date", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void duplicateCalendarShouldThrowExceptionAndEntityMustBeUniqueNoticeShouldBeAddedToResultRepo()
            throws SQLIntegrityConstraintViolationException {
        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Calendar mockCalendar = mock(Calendar.class);
        Calendar.CalendarBuilder mockBuilder = mock(Calendar.CalendarBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockCalendar);

        ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        when(mockParsedCalendar.get("service_id")).thenReturn("test id");
        when(mockParsedCalendar.get("monday")).thenReturn(0);
        when(mockParsedCalendar.get("tuesday")).thenReturn(0);
        when(mockParsedCalendar.get("wednesday")).thenReturn(0);
        when(mockParsedCalendar.get("thursday")).thenReturn(0);
        when(mockParsedCalendar.get("friday")).thenReturn(0);
        when(mockParsedCalendar.get("saturday")).thenReturn(0);
        when(mockParsedCalendar.get("sunday")).thenReturn(0);
        when(mockParsedCalendar.get("start_date")).thenReturn(LocalDateTime.now());
        when(mockParsedCalendar.get("end_date")).thenReturn(LocalDateTime.now());

        when(mockGtfsDataRepo.addCalendar(mockCalendar)).thenThrow(
                new SQLIntegrityConstraintViolationException("service_id must be unique in calendar.txt"));

        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedCalendar));

        assertEquals("service_id must be unique in calendar.txt", exception.getMessage());

        verify(mockParsedCalendar, times(10)).get(anyString());
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId("test id");
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDateTime.class));
        verify(mockBuilder, times(1)).build();

        verify(mockGtfsDataRepo, times(1)).addCalendar(mockCalendar);

        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}