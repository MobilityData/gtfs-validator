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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;

/** A stable, machine-readable count diff; it does not identify individual GTFS records. */
public record FeedVersionDiff(
    int schemaVersion,
    Status status,
    List<String> reasons,
    RunSummary before,
    RunSummary after,
    List<NoticeChange> changes,
    List<NoticeChange> diagnosticChanges) {

  private static final Gson GSON = new GsonBuilder().serializeNulls().create();

  public FeedVersionDiff {
    reasons = List.copyOf(reasons);
    if (reasons.stream().anyMatch(reason -> !reason.matches("[A-Za-z][A-Za-z0-9_.]{0,127}"))) {
      throw new IllegalArgumentException("reasons must contain field identifiers only");
    }
    changes = List.copyOf(changes);
    diagnosticChanges = List.copyOf(diagnosticChanges);
  }

  /** Serializes the diff and its safe run summaries. */
  public String toJson() {
    return GSON.toJson(this);
  }

  /**
   * Digests safe to include in a result. Raw source IDs, provenance IDs, and validation options may
   * contain private URLs, paths, or HTTP headers and are not retained by this record.
   */
  public record RunSummary(
      String feedSha256, String validatorSha256, String reportSha256, String systemErrorsSha256) {
    public RunSummary {
      feedSha256 = safeSha(feedSha256);
      validatorSha256 = safeSha(validatorSha256);
      reportSha256 = safeSha(reportSha256);
      systemErrorsSha256 = safeSha(systemErrorsSha256);
    }

    public static RunSummary from(RunMetadata metadata) {
      return metadata == null
          ? null
          : new RunSummary(
              metadata.feedSha256(),
              metadata.validatorSha256(),
              metadata.reportSha256(),
              metadata.systemErrorsSha256());
    }

    private static String safeSha(String value) {
      return value != null && value.matches("[0-9a-f]{64}") ? value : null;
    }
  }

  public enum Status {
    @SerializedName("comparable")
    COMPARABLE,
    @SerializedName("same_feed")
    SAME_FEED,
    @SerializedName("different_source")
    DIFFERENT_SOURCE,
    @SerializedName("incompatible_method")
    INCOMPATIBLE_METHOD,
    @SerializedName("unverified_input")
    UNVERIFIED_INPUT,
    @SerializedName("invalid_input")
    INVALID_INPUT
  }

  public enum Kind {
    @SerializedName("added")
    ADDED,
    @SerializedName("removed")
    REMOVED,
    @SerializedName("increased")
    INCREASED,
    @SerializedName("decreased")
    DECREASED
  }

  /** Sample flags are diagnostic only; counts always come from {@code totalNotices}. */
  public record NoticeChange(
      String code,
      SeverityLevel severity,
      long before,
      long after,
      long delta,
      Kind kind,
      boolean beforeSamplesTruncated,
      boolean afterSamplesTruncated) {
    public NoticeChange {
      if (code == null || !code.matches("[a-z][a-z0-9_]{0,127}")) {
        throw new IllegalArgumentException("notice code must be lowercase snake case");
      }
    }
  }
}
