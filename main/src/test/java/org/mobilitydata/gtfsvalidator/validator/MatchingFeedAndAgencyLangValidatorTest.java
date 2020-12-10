package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class MatchingFeedAndAgencyLangValidatorTest {
    @Mock
    final GtfsFeedInfoTableContainer mockFeedInfoTable = mock(GtfsFeedInfoTableContainer.class);
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @InjectMocks
    final MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void feedInfoFileNotProvidedShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(true);

        underTest.validate(mockNoticeContainer);

        verifyZeroInteractions(mockNoticeContainer);
    }

    @Test
    public void matchingFeedInfoFeedLangShouldNotGenerateNotice() {
        NoticeContainer mockNoticeContainer = mock(NoticeContainer.class);
        when(mockFeedInfoTable.isEmptyFile()).thenReturn(false);

        GtfsFeedInfo mockFeedInfo = mock(GtfsFeedInfo.class);
        when(mockFeedInfo.feedLang()).thenReturn(Locale.forLanguageTag("fr_FR"));

        GtfsAgency mockAgency0 = mock(GtfsAgency.class);
        when(mockAgency0.agencyLang()).thenReturn(Locale.forLanguageTag("fr_FR"));
        GtfsAgency mockAgency1 = mock(GtfsAgency.class);
        when(mockAgency1.agencyLang()).thenReturn(Locale.forLanguageTag("fr_FR"));

        underTest.validate(mockNoticeContainer);

        verifyZeroInteractions(mockNoticeContainer);
        verifyNoMoreInteractions(mockNoticeContainer, mockAgency0, mockAgency1, mockFeedInfo, mockAgencyTable,
                mockFeedInfoTable);
    }

    @Test
    public void nonMatchingFeedInfoFeedLangShouldGenerateNotice() {}

    @Test
    public void nonMatchingFeedInfoFeedLangAndUniqueAgencyLangShouldGenerateNotice() {}

    @Test
    public void feedLangNotMulAndMoreThanOneAgencyLangShouldGenerateNotice() {}

    @Test
    public void feedLangNotMulAndOnlyThanOneMatchingAgencyLangShouldNotGenerateNotice() {}

    @Test
    public void feedLangNotMulAndOnlyOneMismatchingAgencyLangShouldGenerateNotice() {}

    @Test
    public void mulFeedLangAndNoMoreThanOneAgencyLangShouldGenerateNotice() {}

    @Test
    public void mulFeedLandAnMoreThanOneAgencyShouldNotGenerateNotice() {}
}
