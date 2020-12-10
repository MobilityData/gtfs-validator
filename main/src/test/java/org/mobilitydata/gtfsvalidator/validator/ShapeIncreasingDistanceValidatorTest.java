package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ShapeIncreasingDistanceValidatorTest {
    @Mock
    final GtfsShapeTableContainer mockShapeTable = mock(GtfsShapeTableContainer.class);
    @InjectMocks
    final ShapeIncreasingDistanceValidator underTest = new ShapeIncreasingDistanceValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void increasingDistanceAlongShapeShouldNotGenerateNotice() {}

    @Test
    public void decreasingDistanceAlongShapeShouldGenerateNotice() {}
}
