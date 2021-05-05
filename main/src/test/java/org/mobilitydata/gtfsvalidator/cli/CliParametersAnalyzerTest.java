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
import static org.junit.Assert.assertThrows;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {

  private static boolean validateArguments(String[] cliArguments) {
    Arguments args = new Arguments();
    new JCommander(args).parse(cliArguments);
    return CliParametersAnalyzer.isValid(args);
  }

  @Test
  public void noUrlNoInput_long_isNotValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--output_base", "output value",
                  "--threads", "4"
                }))
        .isFalse();
  }

  @Test
  public void noArguments_isNotValid() {
    assertThrows(ParameterException.class, () -> validateArguments(new String[] {}));
  }

  @Test
  public void urlAndInput_long_isNotValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--url", "url value",
                  "--input", "input value",
                  "--output_base", "output value",
                  "--threads", "4"
                }))
        .isFalse();
  }

  @Test
  public void storageDirectoryNoUrl_long_isNotValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--storage_directory", "storage directory value",
                  "--input", "input value",
                  "--output_base", "output value",
                  "--threads", "4"
                }))
        .isFalse();
  }

  @Test
  public void urlNoStorageDirectory_long_isValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--url", "url value",
                  "--output_base", "output value",
                  "--threads", "4"
                }))
        .isTrue();
  }

  @Test
  public void storageDirectoryNoInput_long_isValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--url", "url value",
                  "--storage_directory", "storage directory value",
                  "--output_base", "output value",
                  "--threads", "4"
                }))
        .isTrue();
  }

  @Test
  public void feedName_long_isNotValid() {
    assertThat(
            validateArguments(
                new String[] {
                  "--input", "input value",
                  "--output_base", "output value",
                  "--country_code", "ca",
                  "--threads", "4",
                  "--feed_name", "feed name"
                }))
        .isFalse();
  }
}
