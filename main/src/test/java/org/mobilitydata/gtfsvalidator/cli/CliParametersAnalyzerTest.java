package org.mobilitydata.gtfsvalidator.cli;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {

    @Test
    public void provideUrlAndInputCliParametersShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn("path to dataset");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        underTest.isValid(mockArguments);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        // TODO: verify logger.error has been called with the correct set of parameters
    }

    @Test
    public void bothUrlAndInputCliParametersNotProvidedShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn(null);
        when(mockArguments.getInput()).thenReturn(null);

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        underTest.isValid(mockArguments);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        // TODO: verify logger.error has been called with the correct set of parameters
    }

    @Test
    public void provideUrlWithoutSpecifyingStorageDirectoryCliParameterShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn(null);

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        underTest.isValid(mockArguments);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        // TODO: verify logger.error has been called with the correct set of parameters
    }

    @Test
    public void provideStorageDirectoryCliParameterWithoutSpecifyingUrlShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn(null);
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        underTest.isValid(mockArguments);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        // TODO: verify logger.error has been called with the correct set of parameters
    }

    @Test
    public void provideUrlStorageDirectoryAndNoInputCliParameterShouldReturnTrue() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        underTest.isValid(mockArguments);
        assertThat(underTest.isValid(mockArguments)).isTrue();
        // TODO: verify logger.error has been called with the correct set of parameters
    }
}
