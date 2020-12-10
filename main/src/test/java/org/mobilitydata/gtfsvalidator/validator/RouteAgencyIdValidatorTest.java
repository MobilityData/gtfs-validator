package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class RouteAgencyIdValidatorTest {
    @Mock
    final GtfsAgencyTableContainer mockAgencyTable = mock(GtfsAgencyTableContainer.class);
    @Mock
    final GtfsRouteTableContainer mockRouteTable = mock(GtfsRouteTableContainer.class);

    @InjectMocks
    final RouteAgencyIdValidator underTest = new RouteAgencyIdValidator();

    @Test
    public void onlyOneAgencyInDatasetShouldNotGenerateNotice() {}

    @Test
    public void undefinedRouteAgencyIdShouldGenerateNotice() {}

    @Test
    public void definedRouteAgencyIdShouldNotGenerateNotice() {}
}
