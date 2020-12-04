package org.mobilitydata.gtfsvalidator.cli;

import org.junit.Test;

public class ValidateCliParametersTest {

    @Test
    public void provideUrlAndInputCliParametersShouldThrowException() {
    }

    @Test
    public void bothUrlAndInputCliParametersNotProvidedShouldThrowException() {}

    @Test
    public void provideUrlWithoutSpecifyingStorageDirectoryCliParameterShouldNotThrowException() {}

    @Test
    public void provideStorageDirectoryCliParameterWithoutSpecifyingUrlShouldThrowException() {}
}
