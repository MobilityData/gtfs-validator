#!/bin/bash

source "$(dirname "$0")/common.sh"

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
      if curl -fsSL --retry 3 -A "MobilityData GTFS-Validator (acceptance tests)" "$URL" -o "$feed_dir/gtfs.zip";
      then
         snapshot_source=(--input "$feed_dir/gtfs.zip")
         master_source=(--input "$feed_dir/gtfs.zip")
      else
         # Let the validators do their own fetching so an unreachable feed is recorded as
         # an IOError in the reports, which is what the comparison expects, instead of
         # leaving no report at all.
         snapshot_source=(--url "$URL")
         master_source=(--url "$URL")
      fi;
      java -Xmx12G -Xms8G -jar gtfs-validator-snapshot/gtfs-validator*.jar "${snapshot_source[@]}" --output_base $OUTPUT_BASE/output/$path_name --validation_report_name latest.json --system_errors_report_name latest_errors.json --skip_validator_update
      java -Xmx12G -Xms8G -jar gtfs-validator-master/gtfs-validator*.jar "${master_source[@]}" --output_base $OUTPUT_BASE/output/$path_name --validation_report_name reference.json --system_errors_report_name reference_errors.json --skip_validator_update
      rm -rf "$feed_dir"
   else
      java -Xmx12G -Xms8G -jar gtfs-validator-snapshot/gtfs-validator*.jar --url "$URL" --output_base $OUTPUT_BASE/output/$path_name --validation_report_name latest.json --system_errors_report_name latest_errors.json --skip_validator_update
   fi;
   wait
done
