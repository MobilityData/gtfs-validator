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

public class ArgumentsTest {

  @Test
  public void shortNameShouldInitializeArguments() {
    String[] commandLineArgumentAsStringArray = {
      "-i", "input value",
      "-o", "output value",
      "-c", "au",
      "-t", "4"
    };
    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    assertThat(underTest.getInput()).matches("input value");
    assertThat(underTest.getOutputBase()).matches("output value");
    assertThat(underTest.getCountryCode()).matches("au");
    assertThat(underTest.getNumThreads()).isEqualTo(4);
    assertThat(underTest.getValidationReportName()).matches("report.json");
    assertThat(underTest.getSystemErrorsReportName()).matches("system_errors.json");

    // same test using -u, -s, -v and -e command line options
    commandLineArgumentAsStringArray =
        new String[] {
          "-o", "output value",
          "-c", "au",
          "-t", "4",
          "-u", "url value",
          "-s", "storage value",
          "-v", "validation_report.json",
          "-e", "errors.json",
        };

    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    assertThat(underTest.getOutputBase()).matches("output value");
    assertThat(underTest.getCountryCode()).matches("au");
    assertThat(underTest.getNumThreads()).isEqualTo(4);
    assertThat(underTest.getUrl()).matches("url value");
    assertThat(underTest.getStorageDirectory()).matches("storage value");
    assertThat(underTest.getValidationReportName()).matches("validation_report.json");
    assertThat(underTest.getSystemErrorsReportName()).matches("errors.json");
  }

  @Test
  public void longNameShouldInitializeArguments() {
    String[] commandLineArgumentAsStringArray = {
      "--input", "input value",
      "--output_base", "output value",
      "--country_code", "ca",
      "--threads", "4"
    };
    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    assertThat(underTest.getInput()).matches("input value");
    assertThat(underTest.getOutputBase()).matches("output value");
    assertThat(underTest.getCountryCode()).matches("ca");
    assertThat(underTest.getNumThreads()).isEqualTo(4);
    assertThat(underTest.getValidationReportName()).matches("report.json");
    assertThat(underTest.getSystemErrorsReportName()).matches("system_errors.json");

    // same test using -u, -s, -v and -e command line options
    commandLineArgumentAsStringArray =
        new String[] {
          "--output_base",
          "output value",
          "--country_code",
          "ca",
          "--threads",
          "4",
          "--url",
          "url value",
          "--storage_directory",
          "storage value",
          "--validation_report_name",
          "validation_report.json",
          "--system_errors_report_name",
          "errors.json",
        };

    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    assertThat(underTest.getOutputBase()).matches("output value");
    assertThat(underTest.getCountryCode()).matches("ca");
    assertThat(underTest.getNumThreads()).isEqualTo(4);
    assertThat(underTest.getUrl()).matches("url value");
    assertThat(underTest.getStorageDirectory()).matches("storage value");
    assertThat(underTest.getValidationReportName()).matches("validation_report.json");
    assertThat(underTest.getSystemErrorsReportName()).matches("errors.json");
  }

  @Test
  public void numThreadsShouldHaveDefaultValueIfNotProvided() {
    String[] commandLineArgumentAsStringArray = {
      "--input", "input value",
      "--output_base", "output value",
      "--country_code", "ca",
    };
    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    assertThat(underTest.getInput()).matches("input value");
    assertThat(underTest.getOutputBase()).matches("output value");
    assertThat(underTest.getCountryCode()).matches("ca");
    assertThat(underTest.getNumThreads()).isEqualTo(1);
  }
}
