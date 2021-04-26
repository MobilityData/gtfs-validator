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

import com.beust.jcommander.JCommander;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {

  private static boolean isScenarioValid(String[] cliArguments) {
    Arguments args = new Arguments();
    CliParametersAnalyzer cliParametersAnalyzer = new CliParametersAnalyzer();
    new JCommander(args).parse(cliArguments);
    return cliParametersAnalyzer.isValid(args);
  }

  @Test
  public void noUrlNoInput_isNotValid() {
    // Nor --url or --input is provided
    String[] argv = {
      "--output_base", "output value",
      "--threads", "4"
    };
    assertThat(isScenarioValid(argv)).isFalse();

    // Nor -u or -i is provided
    argv =
        new String[] {
          "-o", "output value",
          "-t", "4"
        };
    assertThat(isScenarioValid(argv)).isFalse();
  }

  @Test
  public void urlAndInput_isNotValid() {
    // both --u and -i are provided
    String[] argv =
        new String[] {
          "-u", "url value",
          "-i", "input value",
          "-o", "output value",
          "-t", "4"
        };
    assertThat(isScenarioValid(argv)).isFalse();

    // both --url and --input are provided
    argv =
        new String[] {
          "--url", "url value",
          "--input", "input value",
          "--output_base", "output value",
          "--threads", "4"
        };
    assertThat(isScenarioValid(argv)).isFalse();
  }

  @Test
  public void storageDirectoryNoUrl_isNotValid() {
    // --storage_directory is provided and --url is not provided
    String[] argv =
        new String[] {
          "--storage_directory", "storage directory value",
          "--input", "input value",
          "--output_base", "output value",
          "--threads", "4"
        };
    assertThat(isScenarioValid(argv)).isFalse();

    // -s is provided and -u is not provided
    argv =
        new String[] {
          "-s", "storage directory value",
          "-i", "input value",
          "-o", "output value",
          "-t", "4"
        };
    assertThat(isScenarioValid(argv)).isFalse();
  }

  @Test
  public void urlNoStorageDirectory_isValid() {
    // --url is provided and --storage_directory is not provided
    String[] argv = {
      "--url", "url value",
      "--output_base", "output value",
      "--threads", "4"
    };
    assertThat(isScenarioValid(argv)).isTrue();

    // -u is provided and -s is not provided
    argv =
        new String[] {
          "-u", "url value",
          "-o", "output value",
          "-t", "4"
        };
    assertThat(isScenarioValid(argv)).isTrue();
  }

  @Test
  public void storageDirectoryNoInput_isValid() {
    // --storage_directory is provided and --input is not provided
    String[] argv =
        new String[] {
          "--url", "url value",
          "--storage_directory", "storage directory value",
          "--output_base", "output value",
          "--threads", "4"
        };
    assertThat(isScenarioValid(argv)).isTrue();

    // -s is provided and -i is not provided
    argv =
        new String[] {
          "-u", "url value",
          "-s", "storage directory value",
          "-o", "output value",
          "-t", "4"
        };
    assertThat(isScenarioValid(argv)).isTrue();
  }

  @Test
  public void feedName_isNotValid() {
    String[] argv = {
      "--input", "input value",
      "--output_base", "output value",
      "--country_code", "ca",
      "--threads", "4",
      "--feed_name", "feed name"
    };
    assertThat(isScenarioValid(argv)).isFalse();

    argv =
        new String[] {
          "-i", "input value",
          "-o", "output value",
          "-c", "au",
          "-t", "4",
          "-f", "feed name value",
        };
    assertThat(isScenarioValid(argv)).isFalse();
  }
}
