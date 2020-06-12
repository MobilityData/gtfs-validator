/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
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
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedFeedInfoTest {
    private static final String FEED_PUBLISHER_NAME = "feed_publisher_name";
    private static final String FEED_PUBLISHER_URL = "feed_publisher_url";
    private static final String FEED_LANG = "feed_lang";
    private static final String FEED_START_DATE = "feed_start_date";
    private static final String FEED_END_DATE = "feed_end_date";
    private static final String FEED_VERSION = "feed_version";
    private static final String FEED_CONTACT_EMAIL = "feed_contact_email";
    private static final String FEED_CONTACT_URL = "feed_contact_url";
    private static final LocalDateTime START_DATE = LocalDateTime.now();
    private static final LocalDateTime END_DATE = LocalDateTime.now();

    @Test
    void validatedParsedFeedInfoShouldCreateEntityAndToBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FeedInfo.FeedInfoBuilder mockBuilder = mock(FeedInfo.FeedInfoBuilder.class, RETURNS_SELF);
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);
        final ParsedEntity mockParsedFeedInfo = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFeedInfo).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();

        when(mockParsedFeedInfo.get(FEED_PUBLISHER_NAME)).thenReturn(FEED_PUBLISHER_NAME);
        when(mockParsedFeedInfo.get(FEED_PUBLISHER_URL)).thenReturn(FEED_PUBLISHER_URL);
        when(mockParsedFeedInfo.get(FEED_LANG)).thenReturn(FEED_LANG);
        when(mockParsedFeedInfo.get(FEED_START_DATE)).thenReturn(START_DATE);
        when(mockParsedFeedInfo.get(FEED_END_DATE)).thenReturn(END_DATE);
        when(mockParsedFeedInfo.get(FEED_VERSION)).thenReturn(FEED_VERSION);
        when(mockParsedFeedInfo.get(FEED_CONTACT_EMAIL)).thenReturn(FEED_CONTACT_EMAIL);
        when(mockParsedFeedInfo.get(FEED_CONTACT_URL)).thenReturn(FEED_CONTACT_URL);

        when(mockGtfsDataRepo.addFeedInfo(mockFeedInfo)).thenReturn(mockFeedInfo);

        final ProcessParsedFeedInfo underTest = new ProcessParsedFeedInfo(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        underTest.execute(mockParsedFeedInfo);

        final InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo, mockParsedFeedInfo);

        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_LANG));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_START_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_END_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_URL));

        verify(mockBuilder, times(1))
                .feedPublisherName(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockBuilder, times(1)).feedPublisherUrl(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockBuilder, times(1)).feedLang(ArgumentMatchers.eq(FEED_LANG));
        verify(mockBuilder, times(1)).feedStartDate(ArgumentMatchers.eq(START_DATE));
        verify(mockBuilder, times(1)).feedEndDate(ArgumentMatchers.eq(END_DATE));
        verify(mockBuilder, times(1)).feedVersion(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockBuilder, times(1)).feedContactEmail(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockBuilder, times(1)).feedContactUrl(ArgumentMatchers.eq(FEED_CONTACT_URL));

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1))
                .addFeedInfo(ArgumentMatchers.eq(mockFeedInfo));

        verifyNoMoreInteractions(mockBuilder, mockResultRepo, mockGtfsDataRepo, mockParsedFeedInfo, mockGenericObject);
    }

    @Test
    void invalidParsedFeedInfoShouldAddNoticeToRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FeedInfo.FeedInfoBuilder mockBuilder = mock(FeedInfo.FeedInfoBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFeedInfo = mock(ParsedEntity.class);

        final List<Notice> mockNoticeCollection = spy(new ArrayList<>());
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.isSuccess()).thenReturn(false);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockGenericObject).getData();

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedFeedInfo underTest = new ProcessParsedFeedInfo(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        when(mockParsedFeedInfo.get(FEED_PUBLISHER_NAME)).thenReturn(FEED_PUBLISHER_NAME);
        when(mockParsedFeedInfo.get(FEED_PUBLISHER_URL)).thenReturn(FEED_PUBLISHER_URL);
        when(mockParsedFeedInfo.get(FEED_LANG)).thenReturn(FEED_LANG);
        when(mockParsedFeedInfo.get(FEED_START_DATE)).thenReturn(START_DATE);
        when(mockParsedFeedInfo.get(FEED_END_DATE)).thenReturn(END_DATE);
        when(mockParsedFeedInfo.get(FEED_VERSION)).thenReturn(FEED_VERSION);
        when(mockParsedFeedInfo.get(FEED_CONTACT_EMAIL)).thenReturn(FEED_CONTACT_EMAIL);
        when(mockParsedFeedInfo.get(FEED_CONTACT_URL)).thenReturn(FEED_CONTACT_URL);

        underTest.execute(mockParsedFeedInfo);

        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_LANG));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_START_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_END_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_URL));

        verify(mockBuilder, times(1))
                .feedPublisherName(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockBuilder, times(1)).feedPublisherUrl(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockBuilder, times(1)).feedLang(ArgumentMatchers.eq(FEED_LANG));
        verify(mockBuilder, times(1)).feedStartDate(ArgumentMatchers.eq(START_DATE));
        verify(mockBuilder, times(1)).feedEndDate(ArgumentMatchers.eq(END_DATE));
        verify(mockBuilder, times(1)).feedVersion(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockBuilder, times(1)).feedContactEmail(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockBuilder, times(1)).feedContactUrl(ArgumentMatchers.eq(FEED_CONTACT_URL));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedFeedInfo, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    void duplicateFeedInfoShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FeedInfo.FeedInfoBuilder mockBuilder = mock(FeedInfo.FeedInfoBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedFeedInfo = mock(ParsedEntity.class);
        final FeedInfo mockFeedInfo = mock(FeedInfo.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFeedInfo).when(mockGenericObject).getData();

        when(mockFeedInfo.getFeedPublisherName()).thenReturn(FEED_PUBLISHER_NAME);
        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addFeedInfo(mockFeedInfo)).thenReturn(null);

        final ProcessParsedFeedInfo underTest = new ProcessParsedFeedInfo(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        when(mockParsedFeedInfo.get(FEED_PUBLISHER_NAME)).thenReturn(FEED_PUBLISHER_NAME);
        when(mockParsedFeedInfo.get(FEED_PUBLISHER_URL)).thenReturn(FEED_PUBLISHER_URL);
        when(mockParsedFeedInfo.get(FEED_LANG)).thenReturn(FEED_LANG);
        when(mockParsedFeedInfo.get(FEED_START_DATE)).thenReturn(START_DATE);
        when(mockParsedFeedInfo.get(FEED_END_DATE)).thenReturn(END_DATE);
        when(mockParsedFeedInfo.get(FEED_VERSION)).thenReturn(FEED_VERSION);
        when(mockParsedFeedInfo.get(FEED_CONTACT_EMAIL)).thenReturn(FEED_CONTACT_EMAIL);
        when(mockParsedFeedInfo.get(FEED_CONTACT_URL)).thenReturn(FEED_CONTACT_URL);

        underTest.execute(mockParsedFeedInfo);

        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_LANG));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_START_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_END_DATE));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockParsedFeedInfo, times(1)).get(ArgumentMatchers.eq(FEED_CONTACT_URL));

        verify(mockGtfsDataRepo, times(1)).addFeedInfo(ArgumentMatchers.eq(mockFeedInfo));

        verify(mockBuilder, times(1))
                .feedPublisherName(ArgumentMatchers.eq(FEED_PUBLISHER_NAME));
        verify(mockBuilder, times(1)).feedPublisherUrl(ArgumentMatchers.eq(FEED_PUBLISHER_URL));
        verify(mockBuilder, times(1)).feedLang(ArgumentMatchers.eq(FEED_LANG));
        verify(mockBuilder, times(1)).feedStartDate(ArgumentMatchers.eq(START_DATE));
        verify(mockBuilder, times(1)).feedEndDate(ArgumentMatchers.eq(END_DATE));
        verify(mockBuilder, times(1)).feedVersion(ArgumentMatchers.eq(FEED_VERSION));
        verify(mockBuilder, times(1)).feedContactEmail(ArgumentMatchers.eq(FEED_CONTACT_EMAIL));
        verify(mockBuilder, times(1)).feedContactUrl(ArgumentMatchers.eq(FEED_CONTACT_URL));
        verify(mockBuilder, times(1)).build();

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFeedInfo, times(1)).getEntityId();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("feed_info.txt", noticeList.get(0).getFilename());
        assertEquals(FEED_PUBLISHER_NAME, noticeList.get(0).getNoticeSpecific(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedFeedInfo, mockFeedInfo,
                mockGenericObject);
    }
}
