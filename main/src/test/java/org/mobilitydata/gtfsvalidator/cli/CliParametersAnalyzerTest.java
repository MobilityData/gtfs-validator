/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.cli;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {
  private Handler mockHandler = null;
  private ArgumentCaptor<LogRecord> logRecordCaptor = null;

  @Before
  public void installLogRecordCaptor() {
    mockHandler = Mockito.mock(Handler.class);
    logRecordCaptor = ArgumentCaptor.forClass(LogRecord.class);
    Logger.getLogger(CliParametersAnalyzer.class.getName()).addHandler(mockHandler);
  }

  @Test
  public void provideUrlAndInputCliParametersShouldReturnFalse() {
    Arguments mockArguments = mock(Arguments.class);
    when(mockArguments.getUrl()).thenReturn("url to dataset");
    when(mockArguments.getInput()).thenReturn("path to dataset");

    CliParametersAnalyzer underTest = new CliParametersAnalyzer();
    assertThat(underTest.isValid(mockArguments)).isFalse();
    verify(mockHandler).publish(logRecordCaptor.capture());
    assertThat(logRecordCaptor.getValue().getMessage())
        .contains(
            "The two following CLI parameters cannot be "
                + "provided at the same time: '--input' and '--url'");

    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getUrl();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(2)).getInput();
    verifyNoMoreInteractions(mockArguments, mockHandler);
  }

  @Test
  public void bothUrlAndInputCliParametersNotProvidedShouldReturnFalse() {
    Arguments mockArguments = mock(Arguments.class);
    when(mockArguments.getUrl()).thenReturn(null);
    when(mockArguments.getInput()).thenReturn(null);

    CliParametersAnalyzer underTest = new CliParametersAnalyzer();
    assertThat(underTest.isValid(mockArguments)).isFalse();
    verify(mockHandler).publish(logRecordCaptor.capture());
    assertThat(logRecordCaptor.getValue().getMessage())
        .contains(
            "One of the two following CLI parameter must be" + " provided: '--input' and '--url'");
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getUrl();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getInput();
    verifyNoMoreInteractions(mockArguments, mockHandler);
  }

  @Test
  public void provideUrlWithoutSpecifyingStorageDirectoryCliParameterShouldReturnTrue() {
    Arguments mockArguments = mock(Arguments.class);
    when(mockArguments.getUrl()).thenReturn("url to dataset");
    when(mockArguments.getInput()).thenReturn(null);
    when(mockArguments.getStorageDirectory()).thenReturn(null);

    CliParametersAnalyzer underTest = new CliParametersAnalyzer();
    assertThat(underTest.isValid(mockArguments)).isTrue();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getUrl();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(2)).getInput();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getStorageDirectory();
    verifyNoMoreInteractions(mockArguments, mockHandler);
  }

  // FIXME(lionel-nj): Please fix this test.
  //  @Test
  //  public void provideStorageDirectoryCliParameterWithoutSpecifyingUrlShouldReturnFalse() {
  //    Arguments mockArguments = mock(Arguments.class);
  //    when(mockArguments.getUrl()).thenReturn(null);
  //    when(mockArguments.getInput()).thenReturn(null);
  //    when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");
  //
  //    CliParametersAnalyzer underTest = new CliParametersAnalyzer();
  //    assertThat(underTest.isValid(mockArguments)).isFalse();
  //    verify(mockHandler).publish(logRecordCaptor.capture());
  //    assertThat(logRecordCaptor.getValue().getMessage())
  //        .contains(
  //            "One of the two following CLI parameter must be" + " provided: '--input' and
  // '--url'");
  //    //noinspection ResultOfMethodCallIgnored because object is mocked
  //    verify(mockArguments, times(1)).getUrl();
  //    //noinspection ResultOfMethodCallIgnored because object is mocked
  //    verify(mockArguments, times(1)).getInput();
  //    verifyNoMoreInteractions(mockArguments, mockHandler);
  //  }

  @Test
  public void provideUrlStorageDirectoryAndNoInputCliParameterShouldReturnTrue() {
    Arguments mockArguments = mock(Arguments.class);
    when(mockArguments.getUrl()).thenReturn("url to dataset");
    when(mockArguments.getInput()).thenReturn(null);
    when(mockArguments.getStorageDirectory()).thenReturn("storage.zip");

    CliParametersAnalyzer underTest = new CliParametersAnalyzer();
    assertThat(underTest.isValid(mockArguments)).isTrue();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(2)).getUrl();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(2)).getInput();
    //noinspection ResultOfMethodCallIgnored because object is mocked
    verify(mockArguments, times(1)).getStorageDirectory();
    verifyNoMoreInteractions(mockArguments, mockHandler);
  }
}
