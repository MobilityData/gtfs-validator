package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class FeedServiceDateValidatorTest {
    @Mock
    final GtfsFeedInfoTableContainer mockFeedInfoTable = mock(GtfsFeedInfoTableContainer.class);
    @InjectMocks
    final FeedServiceDateValidator underTest = new FeedServiceDateValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void startDateBeforeEndDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.hasFeedEndDate()).thenReturn(true);
        when(mockFeedInfo.hasFeedStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        when(mockFeedInfo.feedStartDate()).thenReturn(mockStartDate);
        when(mockFeedInfo.feedEndDate()).thenReturn(mockEndDate);
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockFeedInfo, times(1)).hasFeedEndDate();
        verify(mockFeedInfo, times(1)).feedEndDate();
        verify(mockFeedInfo, times(1)).hasFeedStartDate();
        verify(mockFeedInfo, times(1)).feedStartDate();
        verify(mockStartDate, times(1)).isAfter(mockEndDate);
        verifyNoMoreInteractions(mockFeedInfo, mockEndDate, mockStartDate);
    }

    @Test
    public void startDateAfterEndDateShouldGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.hasFeedEndDate()).thenReturn(true);
        when(mockFeedInfo.hasFeedStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(true);
        when(mockFeedInfo.feedStartDate()).thenReturn(mockStartDate);
        when(mockStartDate.toYYYYMMDD()).thenReturn("start date value");
        when(mockEndDate.toYYYYMMDD()).thenReturn("end date value");
        when(mockFeedInfo.feedEndDate()).thenReturn(mockEndDate);
        when(mockFeedInfo.csvRowNumber()).thenReturn(2L);
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);
        when(mockFeedInfoTable.gtfsFilename()).thenReturn("feed_info.txt");

        underTest.validate(mockNoticeContainer);

        final ArgumentCaptor<StartAndEndDateOutOfOrderNotice> captor =
                ArgumentCaptor.forClass(StartAndEndDateOutOfOrderNotice.class);

        verify(mockNoticeContainer, times(1)).addNotice(captor.capture());
        StartAndEndDateOutOfOrderNotice notice = captor.getValue();

        assertThat(notice.getCode()).matches("start_and_end_date_out_of_order");
        assertThat(notice.getContext()).containsEntry("filename", "feed_info.txt");
        assertThat(notice.getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(notice.getContext()).containsEntry("startDate", "start date value");
        assertThat(notice.getContext()).containsEntry("endDate", "end date value");

        verify(mockFeedInfo, times(1)).hasFeedEndDate();
        verify(mockFeedInfo, times(1)).hasFeedStartDate();
        verify(mockFeedInfoTable, times(1)).gtfsFilename();
        verify(mockFeedInfo, times(1)).csvRowNumber();
        verify(mockFeedInfo, times(2)).feedEndDate();
        verify(mockFeedInfo, times(2)).feedStartDate();
        verify(mockStartDate, times(1)).isAfter(mockEndDate);
        verify(mockStartDate, times(1)).toYYYYMMDD();
        verify(mockEndDate, times(1)).toYYYYMMDD();
        verifyNoMoreInteractions(mockFeedInfo, mockEndDate, mockStartDate);
    }

    @Test
    public void noStartDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.hasFeedEndDate()).thenReturn(true);
        when(mockFeedInfo.hasFeedStartDate()).thenReturn(false);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        when(mockFeedInfo.feedEndDate()).thenReturn(mockEndDate);
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockFeedInfo, times(1)).hasFeedStartDate();
        verifyNoMoreInteractions(mockFeedInfo, mockEndDate, mockStartDate);
    }

    @Test
    public void noEndDateShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        List<GtfsFeedInfo> feedInfoCollection = new ArrayList<>();
        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.hasFeedEndDate()).thenReturn(false);
        when(mockFeedInfo.hasFeedStartDate()).thenReturn(true);

        GtfsDate mockStartDate = mock(GtfsDate.class);
        GtfsDate mockEndDate = mock(GtfsDate.class);

        when(mockStartDate.isAfter(mockEndDate)).thenReturn(false);
        feedInfoCollection.add(mockFeedInfo);
        when(mockFeedInfoTable.getEntities()).thenReturn(feedInfoCollection);

        underTest.validate(mockNoticeContainer);

        verifyNoInteractions(mockNoticeContainer);
        verify(mockFeedInfo, times(1)).hasFeedEndDate();
        verify(mockFeedInfo, times(1)).hasFeedStartDate();
        verifyNoMoreInteractions(mockFeedInfo, mockEndDate, mockStartDate);
    }
}
