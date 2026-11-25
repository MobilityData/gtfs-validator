#!/bin/bash
#
# Writes a CSV comparing the notices of the reference and latest validation reports of every feed
# of an acceptance test run. It has one row per feed and notice code, with the same columns and
# counts as the comparison MobilityData runs in BigQuery between two validator releases:
# total_previous and total_current are the code's notice counts in the reference and latest
# reports, and new_notices and dropped_notices the increase and decrease between them. Codes
# whose count did not change are included with zeros.
#
# A feed that could not be compared gets a single row with empty counts and a status saying why
# and for which report, checked in the same order as the acceptance test comparator:
#   out_of_memory_<report>   the validation ran out of memory
#   missing_<report>         the report was not written
#   unreadable_<report>      the report is not valid JSON
#   system_errors_<report>   the report has no notices and the system errors report has some, for
#                            example when the feed could not be read; the code column lists them
# where <report> is reference, latest or reference_latest. The other rows have the status ok.
# A feed whose reports both have no notices has no row, as in the BigQuery comparison.
#
# Usage: export_notice_diff_csv.sh <report directory> <latest versions file> <output CSV>
#   report directory      the folder holding one subfolder per feed ID, each with reference.json
#                         and latest.json, as written by queue_runner.sh
#   latest versions file  gtfs_latest_versions.json written by harvest_latest_versions.py, which
#                         maps each feed ID to its URL and gives the list of feeds
#   output CSV            the CSV file to write

set -euo pipefail

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <report directory> <latest versions file> <output CSV>" >&2
  exit 1
fi

report_directory="$1"
latest_versions="$2"
output_csv="$3"

rows=$(mktemp)
trap 'rm -f "$rows"' EXIT

# The comparison of one feed, from the raw text of its four reports (empty when missing). It
# returns the failure row, or one row per notice code. The checks follow the comparator's order.
# A code's count is summed over its entries, as the comparator does. The severity is the reference
# one, falling back to the latest one, as in the BigQuery comparison.
read -r -d '' compare_feed <<'JQ' || true
  def parse: if . == "" then null else (try fromjson catch "unreadable") end;
  def failure($status; $reports; $codes):
    {feed_id: $feed, code: (if $codes == [] then null else ($codes | unique | join(";")) end),
     source_url: $url, status: "\($status)_\($reports | join("_"))"};
  def out_of_memory: type == "object" and any(.notices[]?; .code == "thread_execution_error"
    and any(.sampleNotices[]?; (.exception? // "") | tostring | contains("OutOfMemoryError")));
  def system_errors($errors): type == "object" and ((.notices // []) | length) == 0
    and ($errors | type == "object" and ((.notices // []) | length) > 0);
  def counts: (.notices // []) | group_by(.code)
    | map({key: .[0].code, value: {severity: .[0].severity, total: (map(.totalNotices) | add)}})
    | from_entries;
  {reference: {report: ($reference | parse), errors: ($reference_errors | parse),
               exists: ($reference_exists == "true")},
   latest: {report: ($latest | parse), errors: ($latest_errors | parse),
            exists: ($latest_exists == "true")}} as $r
  | ["reference", "latest"] as $names
  | ([$names[] as $n | select($r[$n].errors | out_of_memory) | $n]) as $oom
  | ([$names[] as $n | select($r[$n].exists | not) | $n]) as $missing
  | ([$names[] as $n | select($r[$n].report == "unreadable") | $n]) as $unreadable
  | ([$names[] as $n | select($r[$n].report | system_errors($r[$n].errors)) | $n]) as $system
  | if $oom != [] then failure("out_of_memory"; $oom; [])
    elif $missing != [] then failure("missing"; $missing; [])
    elif $unreadable != [] then failure("unreadable"; $unreadable; [])
    elif $system != [] then failure("system_errors"; $system; [$system[] as $n | $r[$n].errors.notices[]?.code])
    else ($r.reference.report | counts) as $previous | ($r.latest.report | counts) as $current
      | ($previous + $current | keys[]) as $code
      | ($previous[$code].total // 0) as $p | ($current[$code].total // 0) as $c
      | {feed_id: $feed, code: $code,
         severity: ($previous[$code].severity // $current[$code].severity),
         total_previous: $p, total_current: $c,
         new_notices: ([$c - $p, 0] | max), dropped_notices: ([$p - $c, 0] | max),
         source_url: $url, status: "ok"}
    end
JQ

jq -r 'to_entries[] | "\(.key)\t\(.value)"' "$latest_versions" | while IFS=$'\t' read -r feed_id url; do
  # Each report is passed as raw text, so that an unreadable one is reported instead of failing.
  arguments=(--arg feed "$feed_id" --arg url "$url")
  for report in reference reference_errors latest latest_errors; do
    path="$report_directory/$feed_id/$report.json"
    if [ -f "$path" ]; then
      arguments+=(--rawfile "$report" "$path" --arg "${report}_exists" true)
    else
      arguments+=(--arg "$report" "" --arg "${report}_exists" false)
    fi
  done
  jq -cn "${arguments[@]}" "$compare_feed" >> "$rows"
done

# Sorted by feed ID, in numeric order (mdb-2 before mdb-10), then by notice code.
jq -rs '
  ["feed_id", "code", "severity", "total_previous", "total_current", "new_notices",
   "dropped_notices", "source_url", "status"] as $columns
  | $columns,
    (sort_by([.feed_id | scan("[0-9]+$") | tonumber][0], .feed_id, .code)[]
     | [.[$columns[]]])
  | @csv' "$rows" > "$output_csv"
