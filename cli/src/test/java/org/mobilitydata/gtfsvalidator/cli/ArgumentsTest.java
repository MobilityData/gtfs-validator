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
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.runner.ValidationRunnerConfig;

public class ArgumentsTest {

  @Test
  public void shortNameShouldInitializeArguments() throws URISyntaxException {
    String[] commandLineArgumentAsStringArray = {
      "-i", "/tmp/gtfs.zip",
      "-o", "/tmp/output",
      "-c", "au",
      "-t", "4"
    };

    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    ValidationRunnerConfig config = underTest.toConfig();
    assertThat(config.gtfsSource()).isEqualTo(toFileUri("/tmp/gtfs.zip"));
    assertThat((Object) config.outputDirectory()).isEqualTo(Path.of("/tmp/output"));
    assertThat(config.countryCode()).isEqualTo(CountryCode.forStringOrUnknown("au"));
    assertThat(config.numThreads()).isEqualTo(4);
    assertThat(config.validationReportFileName()).matches("report.json");
    assertThat(config.htmlReportFileName()).matches("report.html");
    assertThat(config.systemErrorsReportFileName()).matches("system_errors.json");
  }

  @Test
  public void shortNameShouldInitializeArguments_url() throws URISyntaxException {
    // same test using -u, -s, -v and -e command line options
    String[] commandLineArgumentAsStringArray =
        new String[] {
          "-o", "/tmp/output",
          "-c", "au",
          "-t", "4",
          "-u", "http://host/gtfs.zip",
          "-s", "/tmp/storage",
          "-v", "validation_report.json",
          "-r", "validation_report.html",
          "-e", "errors.json",
        };

    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    ValidationRunnerConfig config = underTest.toConfig();
    assertThat(config.gtfsSource()).isEqualTo(new URI("http://host/gtfs.zip"));
    assertThat((Object) config.outputDirectory()).isEqualTo(Path.of("/tmp/output"));
    assertThat(config.countryCode()).isEqualTo(CountryCode.forStringOrUnknown("au"));
    assertThat(config.numThreads()).isEqualTo(4);
    assertThat(config.storageDirectory()).hasValue(Path.of("/tmp/storage"));
    assertThat(config.validationReportFileName()).matches("validation_report.json");
    assertThat(config.htmlReportFileName()).matches("validation_report.html");
    assertThat(config.systemErrorsReportFileName()).matches("errors.json");
  }

  @Test
  public void longNameShouldInitializeArguments() throws URISyntaxException {
    String[] commandLineArgumentAsStringArray = {
      "--input", "/tmp/gtfs.zip",
      "--output_base", "/tmp/output",
      "--country_code", "ca",
      "--threads", "4",
      "--date", "2020-01-02"
    };

    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    ValidationRunnerConfig config = underTest.toConfig();
    assertThat(config.gtfsSource()).isEqualTo(toFileUri("/tmp/gtfs.zip"));
    assertThat((Object) config.outputDirectory()).isEqualTo(Path.of("/tmp/output"));
    assertThat(config.countryCode()).isEqualTo(CountryCode.forStringOrUnknown("ca"));
    assertThat(config.numThreads()).isEqualTo(4);
    assertThat(config.validationReportFileName()).matches("report.json");
    assertThat(config.htmlReportFileName()).matches("report.html");
    assertThat(config.systemErrorsReportFileName()).matches("system_errors.json");
    assertThat(config.dateForValidation().toString()).matches("2020-01-02");
  }

  @Test
  public void longNameShouldInitializeArguments_url() throws URISyntaxException {
    // same test using -u, -s, -v and -e command line options
    String[] commandLineArgumentAsStringArray =
        new String[] {
          "--output_base",
          "/tmp/output",
          "--country_code",
          "ca",
          "--threads",
          "4",
          "--url",
          "http://host/gtfs.zip",
          "--storage_directory",
          "/tmp/storage",
          "--validation_report_name",
          "validation_report.json",
          "--html_report_name",
          "validation_report.html",
          "--system_errors_report_name",
          "errors.json",
        };

    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    ValidationRunnerConfig config = underTest.toConfig();
    assertThat(config.gtfsSource()).isEqualTo(new URI("http://host/gtfs.zip"));
    assertThat((Object) config.outputDirectory()).isEqualTo(Path.of("/tmp/output"));
    assertThat(config.countryCode()).isEqualTo(CountryCode.forStringOrUnknown("ca"));
    assertThat(config.numThreads()).isEqualTo(4);
    assertThat(config.storageDirectory()).hasValue(Path.of("/tmp/storage"));
    assertThat(config.validationReportFileName()).matches("validation_report.json");
    assertThat(config.htmlReportFileName()).matches("validation_report.html");
    assertThat(config.systemErrorsReportFileName()).matches("errors.json");
  }

  @Test
  public void numThreadsShouldHaveDefaultValueIfNotProvided() throws URISyntaxException {
    String[] commandLineArgumentAsStringArray = {
      "--input", "/tmp/gtfs.zip",
      "--output_base", "/tmp/output",
      "--country_code", "ca",
    };

    Arguments underTest = new Arguments();
    new JCommander(underTest).parse(commandLineArgumentAsStringArray);
    ValidationRunnerConfig config = underTest.toConfig();
    assertThat(config.gtfsSource()).isEqualTo(toFileUri("/tmp/gtfs.zip"));
    assertThat((Object) config.outputDirectory()).isEqualTo(Path.of("/tmp/output"));
    assertThat(config.countryCode()).isEqualTo(CountryCode.forStringOrUnknown("ca"));
    assertThat(config.numThreads()).isEqualTo(1);
  }

  private static URI toFileUri(String path) {
    // Ideally we would just hardcode the URI value for each test, but URI path
    // generation is OS-specific, so it breaks on different test environments.
    return new File(path).toURI();
  }

  private static boolean validateArguments(String[] cliArguments) {
    Arguments args = new Arguments();
    new JCommander(args).parse(cliArguments);
    return args.validate();
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
  public void exportNoticesSchema_schemaOnly() {
    String[] cliArguments = {"--export_notices_schema", "--output_base", "output value"};
    Arguments args = new Arguments();
    new JCommander(args).parse(cliArguments);

    assertThat(args.validate()).isTrue();
    assertThat(args.getExportNoticeSchema()).isTrue();
    assertThat(args.abortAfterNoticeSchemaExport()).isTrue();
  }

  @Test
  public void exportNoticesSchema_schemaAndValidation() {
    String[] cliArguments = {
      "--export_notices_schema", "--input", "input value", "--output_base", "output value"
    };
    Arguments args = new Arguments();
    new JCommander(args).parse(cliArguments);

    assertThat(args.validate()).isTrue();
    assertThat(args.getExportNoticeSchema()).isTrue();
    assertThat(args.abortAfterNoticeSchemaExport()).isFalse();
  }
}
