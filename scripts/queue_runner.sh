#!/bin/bash

source "$(dirname "$0")/common.sh"

oom_script="$(cd "$(dirname "$0")" && pwd)/write_oom_system_errors.sh"

# Runs a validator jar on the current feed. If the JVM throws an OutOfMemoryError, even one that
# ends the process before the validator writes its reports, the system errors report records it
# so that the comparator reports the dataset as out of memory. The JVM then exits: otherwise a
# thread still running keeps it alive and the job hangs.
run_validator() {
   local jar_dir="$1"
   local report_name="$2"
   shift 2
   local output_dir="$OUTPUT_BASE/output/$path_name"
   java -Xmx12G -Xms8G -Dgtfsvalidator.performance.liveHeap=true "-XX:OnOutOfMemoryError=bash $oom_script $output_dir/${report_name}_errors.json" -XX:+ExitOnOutOfMemoryError -jar $jar_dir/gtfs-validator*.jar "$@" --output_base "$output_dir" --validation_report_name "$report_name.json" --system_errors_report_name "${report_name}_errors.json" --skip_validator_update
}

master="$1"
raw_queue_string="${@:2}"
IFS=" " read -a queue <<< $raw_queue_string
for item in "${queue[@]}"
do
   item=$(format_json "$item")

   # -r matters: without it jq leaves the surrounding quotes on the value, and only
   # JCommander (pinned at 1.48) strips them again before the validator parses the URL.
   ID=$(jq -r '.id' <<< "$item")
   URL=$(jq -r '.url' <<< "$item")
   path_name=$ID

   if [ "$master" = "--include-master" ];
   then
      # Two validators run over the same feed, so fetch it once and point both at the
      # local copy. Besides halving the bytes pulled from the files server, both runs
      # then see identical input - a feed republished between the two runs would
      # otherwise show up as a difference in the reports.
      feed_dir="$OUTPUT_BASE/feeds/$path_name"
      mkdir -p "$feed_dir"
      cached_master_dir=""
      if curl -fsSL --retry 3 -A "MobilityData GTFS-Validator (acceptance tests)" "$URL" -o "$feed_dir/gtfs.zip";
      then
         snapshot_source=(--input "$feed_dir/gtfs.zip")
         master_source=(--input "$feed_dir/gtfs.zip")
         # Master's reports only depend on master's code and on the feed's content, so they are
         # stored by feed ID and feed file hash. MASTER_REPORTS_CACHE holds reports from earlier
         # runs on the same master commit (restored by the workflow); MASTER_REPORTS_OUT
         # receives this job's master reports, reused or new, so the workflow can save them.
         if [ -n "$MASTER_REPORTS_CACHE" ] && [ -n "$MASTER_REPORTS_OUT" ];
         then
            feed_hash=$(sha256sum "$feed_dir/gtfs.zip" | cut -d ' ' -f 1)
            cached_master_dir="$MASTER_REPORTS_CACHE/$path_name/$feed_hash"
            out_master_dir="$MASTER_REPORTS_OUT/$path_name/$feed_hash"
         fi;
      else
         # Let the validators do their own fetching so an unreachable feed is recorded as
         # an IOError in the reports, which is what the comparison expects, instead of
         # leaving no report at all.
         snapshot_source=(--url "$URL")
         master_source=(--url "$URL")
      fi;
      output_dir="$OUTPUT_BASE/output/$path_name"
      if [ -n "$cached_master_dir" ] && [ -f "$cached_master_dir/reference.json" ];
      then
         echo "Reusing master reports for $ID from an earlier run on the same feed file"
         mkdir -p "$output_dir"
         cp "$cached_master_dir"/reference*.json "$output_dir/"
         run_validator gtfs-validator-snapshot latest "${snapshot_source[@]}"
      else
         # Whichever validator runs first on a feed is slightly slower, which biased every
         # performance comparison against the snapshot when it always ran first. Alternating the
         # order per feed cancels that out across the datasets. The order comes from a checksum of
         # the feed ID so that a feed always runs in the same order across runs.
         if (( $(cksum <<< "$ID" | cut -d ' ' -f 1) % 2 == 0 ));
         then
            run_validator gtfs-validator-snapshot latest "${snapshot_source[@]}"
            run_validator gtfs-validator-master reference "${master_source[@]}"
         else
            run_validator gtfs-validator-master reference "${master_source[@]}"
            run_validator gtfs-validator-snapshot latest "${snapshot_source[@]}"
         fi;
      fi;
      # Only a complete master report is kept for reuse; a crashed or out-of-memory master run is
      # retried next time.
      if [ -n "$cached_master_dir" ] && [ -f "$output_dir/reference.json" ];
      then
         mkdir -p "$out_master_dir"
         cp "$output_dir"/reference*.json "$out_master_dir/"
      fi;
      rm -rf "$feed_dir"
   else
      run_validator gtfs-validator-snapshot latest --url "$URL"
   fi;
   wait
done
