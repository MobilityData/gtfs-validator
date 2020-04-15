package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.EntityMustBeUniqueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.UnexpectedValueNotice;
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

class ProcessParsedCalendarDateTest {

    @Test
    void processValidCalendarDateShouldNotThrowExceptionAndAddNewEntityToGtfsDataRepository()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockCalendarDate);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);
        final LocalDateTime date = LocalDateTime.now();

        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        underTest.execute(mockParsedCalendarDate);

        verify(mockParsedCalendarDate, times(3)).get(ArgumentMatchers.anyString());

        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));

        InOrder inOrder = inOrder(mockBuilder, mockResultRepo, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1))
                .addCalendarDate(ArgumentMatchers.eq(mockCalendarDate));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedCalendarDate);
    }

    @Test
    void nullServiceIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = spy(CalendarDate.CalendarDateBuilder.class);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn(null);
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendarDate));

        Assertions.assertEquals("field service_id in calendar_dates.txt can not be null",
                exception.getMessage());

        verify(mockParsedCalendarDate, times(3)).get(anyString());
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));

        verify(mockBuilder, times(1)).build();
        verify(mockParsedCalendarDate, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendarDate, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void nullDateShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = spy(CalendarDate.CalendarDateBuilder.class);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(null);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendarDate));

        Assertions.assertEquals("field date in calendar_dates.txt can not be null",
                exception.getMessage());

        verify(mockParsedCalendarDate, times(3)).get(anyString());
        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));

        verify(mockBuilder, times(1)).build();
        verify(mockParsedCalendarDate, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("date", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendarDate, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void nullExceptionTypeIdShouldThrowExceptionAndMissingRequiredValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = spy(CalendarDate.CalendarDateBuilder.class);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(null);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendarDate));

        Assertions.assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());

        verify(mockParsedCalendarDate, times(3)).get(anyString());
        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(null));

        verify(mockBuilder, times(1)).build();
        verify(mockParsedCalendarDate, times(1)).getEntityId();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("exception_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedCalendarDate, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void invalidExceptionTypeIdShouldThrowExceptionAndUnexpectedValueNoticeShouldBeAddedToResultRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = spy(CalendarDate.CalendarDateBuilder.class);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(4);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedCalendarDate));

        Assertions.assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());

        verify(mockParsedCalendarDate, times(3)).get(anyString());
        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(4));

        verify(mockBuilder, times(1)).build();
        verify(mockParsedCalendarDate, times(1)).getEntityId();

        final ArgumentCaptor<UnexpectedValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedValueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<UnexpectedValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("exception_type", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockParsedCalendarDate, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void duplicateCalendarDateShouldThrowExceptionAndEntityMustBeUniqueNoticeShouldBeAddedToResultRepo()
            throws SQLIntegrityConstraintViolationException {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        when(mockBuilder.build()).thenReturn(mockCalendarDate);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(2);

        when(mockGtfsDataRepo.addCalendarDate(mockCalendarDate)).
                thenThrow(new SQLIntegrityConstraintViolationException("calendar_dates based on service_id" +
                        " and date must be unique in dataset"));

        final Exception exception = Assertions.assertThrows(SQLIntegrityConstraintViolationException.class,
                () -> underTest.execute(mockParsedCalendarDate));
        Assertions.assertEquals("calendar_dates based on service_id and date must be unique in dataset",
                exception.getMessage());

        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("date"));
        verify(mockParsedCalendarDate, times(1))
                .get(ArgumentMatchers.eq("exception_type"));

        verify(mockGtfsDataRepo, times(1)).
                addCalendarDate(ArgumentMatchers.eq(mockCalendarDate));

        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).build();

        verify(mockParsedCalendarDate, times(1)).getEntityId();

        final ArgumentCaptor<EntityMustBeUniqueNotice> captor = ArgumentCaptor.forClass(EntityMustBeUniqueNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<EntityMustBeUniqueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());
    }
}