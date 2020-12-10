package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;

public class AgencyConsistencyValidatorTest {
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @InjectMocks
    final AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void multipleAgenciesPresentButNoAgencyIdSetShouldGenerateNotice() {}

    @Test
    public void agenciesWithDifferentTimezoneShouldGenerateNotice() {}

    @Test
    public void agenciesWithSameTimezoneShouldNotGenerateNotice() {}

    @Test
    public void agenciesWithDifferentLanguagesShouldNotGenerateNotice() {}

    @Test
    public void agenciesWithSameLanguagesShouldNotGenerateNotice() {}

    @Test
    public void onlyOneAgencyWithNoAgencyIdShouldNotGenerateNotice() {}
}
