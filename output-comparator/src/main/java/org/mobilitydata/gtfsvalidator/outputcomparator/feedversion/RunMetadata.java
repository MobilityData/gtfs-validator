/*
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

package org.mobilitydata.gtfsvalidator.outputcomparator.feedversion;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Facts about one validation run, supplied by a trusted loader alongside a {@code
 * ValidationReport}. The report model discards most of its JSON summary and cannot establish these
 * facts by itself.
 *
 * <p>{@code sourceId} identifies the publisher/feed independently of a particular ZIP URL. The
 * loader must include every result-affecting option in {@code validationOptions}, and must verify
 * the four digests, the report summary, the validator exit status, and system errors before
 * asserting evidence. This value object does no file I/O and cannot independently prove a caller's
 * attestation.
 */
public record RunMetadata(
    String sourceId,
    String observedAtUtc,
    String feedSha256,
    String validatorVersion,
    String validatorSha256,
    String reportSha256,
    String systemErrorsSha256,
    String validationDate,
    String countryCode,
    Map<String, String> validationOptions,
    boolean executionSucceeded,
    boolean systemErrorsEmpty,
    Evidence evidence) {

  public RunMetadata {
    if (validationOptions != null) {
      TreeMap<String, String> sorted = new TreeMap<>(Comparator.nullsFirst(String::compareTo));
      sorted.putAll(validationOptions);
      validationOptions = Collections.unmodifiableMap(sorted);
    }
  }

  /**
   * The provenance and verification performed by the loader, not a claim inferred from the report.
   */
  public record Evidence(
      String provenanceId,
      boolean feedSha256Verified,
      boolean validatorSha256Verified,
      boolean reportSha256Verified,
      boolean systemErrorsSha256Verified,
      boolean reportSummaryVerified,
      boolean systemErrorsInspected) {

    public boolean complete() {
      return provenanceId != null
          && !provenanceId.isBlank()
          && feedSha256Verified
          && validatorSha256Verified
          && reportSha256Verified
          && systemErrorsSha256Verified
          && reportSummaryVerified
          && systemErrorsInspected;
    }
  }
}
