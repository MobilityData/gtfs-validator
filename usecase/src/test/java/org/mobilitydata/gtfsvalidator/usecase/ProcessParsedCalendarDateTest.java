package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProcessParsedCalendarDateTest {

    @Test
    void validCalendarDateShouldNotGenerateNoticeAndAddNewEntityToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockCalendarDate).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addCalendarDate(mockCalendarDate)).thenReturn(mockCalendarDate);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();

        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        underTest.execute(mockParsedCalendarDate);

        final InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo, mockParsedCalendarDate);

        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("date"));
        verify(mockParsedCalendarDate, times(1))
                .get(ArgumentMatchers.eq("exception_type"));

        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();
        verify(mockGenericObject, times(1)).isSuccess();

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1))
                .addCalendarDate(ArgumentMatchers.eq(mockCalendarDate));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedCalendarDate,
                mockGenericObject);
    }

    @Test
    void invalidCalendarDateShouldGenerateNoticeAndEntityShouldNotBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);

        final List<Notice> mockNoticeCollection = spy(new ArrayList<>());
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(false);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockGenericObject).getData();

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        final LocalDateTime date = LocalDateTime.now();

        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        underTest.execute(mockParsedCalendarDate);

        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("date"));
        verify(mockParsedCalendarDate, times(1))
                .get(ArgumentMatchers.eq("exception_type"));

        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).build();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();
        verify(mockGenericObject, times(1)).isSuccess();

        verify(mockResultRepo, times(1)).addNotice(mockNotice);

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedCalendarDate,
                mockGenericObject);
    }

    @Test
    void duplicateCalendarDateShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final CalendarDate.CalendarDateBuilder mockBuilder = mock(CalendarDate.CalendarDateBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedCalendarDate = mock(ParsedEntity.class);
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockCalendarDate).when(mockGenericObject).getData();

        when(mockCalendarDate.getServiceId()).thenReturn("service_id");
        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addCalendarDate(mockCalendarDate)).thenReturn(null);

        final ProcessParsedCalendarDate underTest = new ProcessParsedCalendarDate(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);
        final LocalDateTime date = LocalDateTime.now();

        when(mockParsedCalendarDate.get("service_id")).thenReturn("service_id");
        when(mockParsedCalendarDate.get("date")).thenReturn(date);
        when(mockParsedCalendarDate.get("exception_type")).thenReturn(1);

        underTest.execute(mockParsedCalendarDate);

        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("service_id"));
        verify(mockParsedCalendarDate, times(1)).get(ArgumentMatchers.eq("date"));
        verify(mockParsedCalendarDate, times(1))
                .get(ArgumentMatchers.eq("exception_type"));

        verify(mockGtfsDataRepo, times(1))
                .addCalendarDate(ArgumentMatchers.eq(mockCalendarDate));

        verify(mockBuilder, times(1)).serviceId(ArgumentMatchers.eq("service_id"));
        verify(mockBuilder, times(1)).date(ArgumentMatchers.eq(date));
        verify(mockBuilder, times(1)).exceptionType(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).build();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendarDate, times(1)).getEntityId();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("service_id, date", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedCalendarDate,
                mockCalendarDate, mockGenericObject);
    }
}