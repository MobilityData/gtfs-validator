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

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CliParametersAnalyzerTest {
  @Test
  public void isValid_false() {
    Arguments args = new Arguments();
    // --url and --input not provided
    CliParametersAnalyzer cliParametersAnalyzer = new CliParametersAnalyzer();
    String[] argv = {
      "--output_base", "output value",
      "--threads", "4"
    };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    // --u and -i not provided
    argv =
        new String[] {
          "-o", "output value",
          "-t", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    // --u and -i are provided
    argv =
        new String[] {
          "-u", "url value",
          "-i", "input value",
          "-o", "output value",
          "-t", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    // --url and --input are provided
    argv =
        new String[] {
          "--url", "url value",
          "--input", "input value",
          "--output_base", "output value",
          "--threads", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    // --storage_directory is provided and --url is not provided
    argv =
        new String[] {
          "--storage_directory", "storage directory value",
          "--input", "input value",
          "--output_base", "output value",
          "--threads", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    // -s is provided and -u is not provided
    argv =
        new String[] {
          "-s", "storage directory value",
          "-i", "input value",
          "-o", "output value",
          "-t", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();
  }

  @Test
  public void isValid_true() {
    Arguments args = new Arguments();
    // --url is provided and --storage_directory is not provided
    CliParametersAnalyzer cliParametersAnalyzer = new CliParametersAnalyzer();
    String[] argv = {
      "--url", "url value",
      "--output_base", "output value",
      "--threads", "4"
    };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isTrue();

    // --u is provided and --s is not provided
    argv =
        new String[] {
          "-u", "url value",
          "-o", "output value",
          "-t", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isTrue();

    // -s is provided and -i is not provided
    argv =
        new String[] {
          "-u", "url value",
          "-s", "storage directory value",
          "-o", "output value",
          "-t", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isTrue();

    // --storage_directory is provided and --input is not provided
    argv =
        new String[] {
          "--url", "url value",
          "--storage_directory", "storage directory value",
          "--output_base", "output value",
          "--threads", "4"
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isTrue();
  }

  @Test
  public void feedName_isNotValid() {
    Arguments args = new Arguments();
    CliParametersAnalyzer cliParametersAnalyzer = new CliParametersAnalyzer();
    String[] argv = {
      "--input", "input value",
      "--output_base", "output value",
      "--country_code", "ca",
      "--threads", "4",
      "--feed_name", "feed name"
    };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();

    argv =
        new String[] {
          "-i", "input value",
          "-o", "output value",
          "-c", "au",
          "-t", "4",
          "-f", "feed name value",
        };
    new JCommander(args).parse(argv);
    assertThat(cliParametersAnalyzer.isValid(args)).isFalse();
  }
}
