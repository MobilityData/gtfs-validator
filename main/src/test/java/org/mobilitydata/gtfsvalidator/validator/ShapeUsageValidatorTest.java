package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Before;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;

public class ShapeUsageValidatorTest {
    @Mock
    final GtfsShapeTableContainer mockShapeTable = mock(GtfsShapeTableContainer.class);
    @InjectMocks
    final ShapeUsageValidator underTest = new ShapeUsageValidator();

    @Before
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void allShapeUsedShouldNotGenerateNotice() {}

    @Test
    public void unusedShapeShouldGenerateNotice() {}
}
