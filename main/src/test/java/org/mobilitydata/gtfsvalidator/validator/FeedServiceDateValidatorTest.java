package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;

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
    public void startDateBeforeEndDateShouldNotGenerateNotice() {}

    @Test
    public void startDateAfterEndDateShouldGenerateNotice() {}

    @Test
    public void noStartDateShouldNotGenerateNotice() {}

    @Test
    public void noEndDateShouldNotGenerateNotice() {}
}
