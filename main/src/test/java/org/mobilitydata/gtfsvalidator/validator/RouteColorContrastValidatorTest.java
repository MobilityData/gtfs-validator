package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;

public class RouteColorContrastValidatorTest {
    @Test
    public void contrastingRouteColorAndRouteTextColorShouldNotGenerateNotice() {}

    @Test
    public void nonContrastingRouteColorAndRouteTextColorShouldGenerateNotice() {}
}
