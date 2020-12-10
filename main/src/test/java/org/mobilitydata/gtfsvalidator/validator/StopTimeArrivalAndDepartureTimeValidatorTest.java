package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class StopTimeArrivalAndDepartureTimeValidatorTest {
    @Mock
    final GtfsStopTimeTableContainer mockStopTimeTable = mock(GtfsStopTimeTableContainer.class);
    @InjectMocks
    final StopTimeArrivalAndDepartureTimeValidator underTest = new StopTimeArrivalAndDepartureTimeValidator();

    @Test
    public void departureTimeBeforeArrivalTimeShouldGenerateNotice() {}

    @Test
    public void departureTimeAfterArrivalTimeShouldGenerateNotice() {}

    @Test
    public void stopTimeWithArrivalBeforePreviousDepartureTimeShouldGenerateNotice() {}

    @Test
    public void stopTimeWithArrivalAfterPreviousDepartureTimeShouldNotyGenerateNotice() {}

    @Test
    public void missingDepartureTimeShouldGenerateNoticeIfArrivalTimeIsProvided() {}

    @Test
    public void missingArrivalTimeShouldGenerateNoticeIfDepartureTimeIsProvided() {}

    @Test
    public void departureTimeAndArrivalTimeNotProvidedShouldNotGenerateNotice() {}

    @Test
    public void departureTimeAndArrivalTimeProvidedShouldNotGenerateNotice() {}

}
