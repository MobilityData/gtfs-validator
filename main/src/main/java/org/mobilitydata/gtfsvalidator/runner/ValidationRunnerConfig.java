/*
 * Copyright 2020-2022 Google LLC, MobilityData IO
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
package org.mobilitydata.gtfsvalidator.runner;

import com.google.auto.value.AutoValue;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.input.CountryCode;

/** Defines executation parameters for {@link ValidationRunner}. */
@AutoValue
public abstract class ValidationRunnerConfig {
  // The GTFS input, as a URI to a local file or an external URL.
  public abstract URI gtfsSource();

  // The directory where all validation reports will be written.
  public abstract Path outputDirectory();

  // An optional storage directory to be used when downloading a GTFS feed
  // from an external URL.
  public abstract Optional<Path> storageDirectory();

  public abstract String validationReportFileName();

  public abstract String htmlReportFileName();

  public abstract String systemErrorsReportFileName();

  // Determines the number of parallel threads of execution used during
  // validation.
  public abstract int numThreads();

  // The country code for the country containing the transit service to be
  // validated.
  public abstract CountryCode countryCode();

  // If true, any output json will be pretty-printed.
  public abstract boolean prettyJson();

  public static Builder builder() {
    // Set reasonable defaults where appropriate.
    return new AutoValue_ValidationRunnerConfig.Builder()
        .setValidationReportFileName("report.json")
        .setHtmlReportFileName("report.html")
        .setSystemErrorsReportFileName("system_errors.json")
        .setNumThreads(1)
        .setPrettyJson(false)
        .setCountryCode(CountryCode.forStringOrUnknown(CountryCode.ZZ));
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setGtfsSource(URI gtfsSource);

    public abstract Builder setOutputDirectory(Path outputDirectory);

    public abstract Builder setStorageDirectory(Path storageDirectory);

    public abstract Builder setValidationReportFileName(String validationReportFileName);

    public abstract Builder setHtmlReportFileName(String htmlReportFileName);

    public abstract Builder setSystemErrorsReportFileName(String systemErrorsReportFileName);

    public abstract Builder setNumThreads(int numThreads);

    public abstract Builder setCountryCode(CountryCode countryCode);

    public abstract Builder setPrettyJson(boolean prettyJson);

    public abstract ValidationRunnerConfig build();
  }
}
