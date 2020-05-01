package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FeedInfoTest {
    private static final String STRING_TEST_VALUE = "string test value";

    @Test
    void createFeedInfoWithNullFeedPublisherNameShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.feedPublisherName(null)
                .feedPublisherUrl(STRING_TEST_VALUE)
                .feedLang(STRING_TEST_VALUE)
                .feedStartDate(LocalDateTime.now())
                .feedEndDate(LocalDateTime.now())
                .feedVersion(STRING_TEST_VALUE)
                .feedContactEmail(STRING_TEST_VALUE)
                .feedContactUrl(STRING_TEST_VALUE);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_publisher_name", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFeedInfoWithNullFeedPublisherUrlShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder(mockNoticeCollection);

        @SuppressWarnings("ConstantConditions") final EntityBuildResult<?> entityBuildResult =
                underTest.feedPublisherName(STRING_TEST_VALUE)
                        .feedPublisherUrl(null)
                        .feedLang(STRING_TEST_VALUE)
                        .feedStartDate(LocalDateTime.now())
                        .feedEndDate(LocalDateTime.now())
                        .feedVersion(STRING_TEST_VALUE)
                        .feedContactEmail(STRING_TEST_VALUE)
                        .feedContactUrl(STRING_TEST_VALUE)
                        .build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_contact_url", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFeedInfoWithNullFeedLangShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder(mockNoticeCollection);

        @SuppressWarnings("ConstantConditions") final EntityBuildResult<?> entityBuildResult =
                underTest.feedPublisherName(STRING_TEST_VALUE)
                        .feedPublisherUrl(STRING_TEST_VALUE)
                        .feedLang(null)
                        .feedStartDate(LocalDateTime.now())
                        .feedEndDate(LocalDateTime.now())
                        .feedVersion(STRING_TEST_VALUE)
                        .feedContactEmail(STRING_TEST_VALUE)
                        .feedContactUrl(STRING_TEST_VALUE)
                        .build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals("feed_lang", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFeedInfoWithValidValuesShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final FeedInfo.FeedInfoBuilder underTest = new FeedInfo.FeedInfoBuilder(mockNoticeCollection);

        final EntityBuildResult<?> entityBuildResult = underTest.feedPublisherName(STRING_TEST_VALUE)
                .feedPublisherUrl(STRING_TEST_VALUE)
                .feedLang(STRING_TEST_VALUE)
                .build();

        assertTrue(entityBuildResult.getData() instanceof FeedInfo);
        verify(mockNoticeCollection, times(1)).clear();
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}