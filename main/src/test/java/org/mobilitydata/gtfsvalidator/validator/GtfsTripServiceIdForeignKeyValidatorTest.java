package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GtfsTripServiceIdForeignKeyValidatorTest {
    @Mock
    final GtfsTripTableContainer mockTripTable = mock(GtfsTripTableContainer.class);
    @Mock
    final GtfsCalendarTableContainer mockCalendarTable = mock(GtfsCalendarTableContainer.class);
    @Mock
    final GtfsCalendarDateTableContainer mockCalendarDateTable = mock(GtfsCalendarDateTableContainer.class);

    @InjectMocks
    final GtfsTripServiceIdForeignKeyValidator underTest = new GtfsTripServiceIdForeignKeyValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void tripServiceIdInCalendarTableShouldNotGenerateNotice() {}

    @Test
    public void tripServiceIdNotInCalendarTableShouldGenerateNotice() {}

    @Test
    public void tripServiceIdInCalendarDateTableShouldNotGenerateNotice() {}

    @Test
    public void tripServiceIdNotInCalendarDateTableShouldGenerateNotice() {}
}
