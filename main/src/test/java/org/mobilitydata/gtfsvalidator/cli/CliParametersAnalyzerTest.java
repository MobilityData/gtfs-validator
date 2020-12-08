package org.mobilitydata.gtfsvalidator.cli;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {

    @Test
    public void provideUrlAndInputCliParametersShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn("path to dataset");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        verify(mockLogger, times(1)).error("The two following CLI parameters cannot be " +
                "provided at the same time: '--input' and '--url'");
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getUrl();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(2)).getInput();
        verifyNoMoreInteractions(mockArguments, mockLogger);
    }

    @Test
    public void bothUrlAndInputCliParametersNotProvidedShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn(null);
        when(mockArguments.getInput()).thenReturn(null);

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        verify(mockLogger, times(1)).error("One of the two following CLI parameter must be" +
                " provided: '--input' and '--url'");
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getUrl();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getInput();
        verifyNoMoreInteractions(mockArguments, mockLogger);
    }

    @Test
    public void provideUrlWithoutSpecifyingStorageDirectoryCliParameterShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn(null);

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        verify(mockLogger, times(1)).error("CLI parameter '--storage_directory' must be " +
                "provided if '--url' is provided");
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(2)).getUrl();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(2)).getInput();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getStorageDirectory();
        verifyNoMoreInteractions(mockArguments, mockLogger);
    }

    @Test
    public void provideStorageDirectoryCliParameterWithoutSpecifyingUrlShouldReturnFalse() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn(null);
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        assertThat(underTest.isValid(mockArguments)).isFalse();
        verify(mockLogger, times(1)).error("One of the two following CLI parameter must be" +
                " provided: '--input' and '--url'");
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getUrl();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(1)).getInput();
        verifyNoMoreInteractions(mockArguments, mockLogger);
    }

    @Test
    public void provideUrlStorageDirectoryAndNoInputCliParameterShouldReturnTrue() {
        Logger mockLogger = mock(Logger.class);
        Arguments mockArguments = mock(Arguments.class);
        when(mockArguments.getUrl()).thenReturn("url to dataset");
        when(mockArguments.getInput()).thenReturn(null);
        when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");

        CliParametersAnalyzer underTest = new CliParametersAnalyzer(mockLogger);
        assertThat(underTest.isValid(mockArguments)).isTrue();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(3)).getUrl();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(2)).getInput();
        //noinspection ResultOfMethodCallIgnored because object is mocked
        verify(mockArguments, times(2)).getStorageDirectory();
        verifyNoMoreInteractions(mockArguments, mockLogger);
    }
}
