#!/bin/bash

source "$(dirname "$0")/common.sh"

master="$1"
raw_queue_string="${@:2}"
IFS=" " read -a queue <<< $raw_queue_string
for item in "${queue[@]}"
do
   item=$(format_json "$item")

   ID=$(jq '.id' <<< "$item")
   URL=$(jq '.url' <<< "$item")
   path_name=${ID//\"/}
   java -Xmx12G -Xms8G -jar gtfs-validator-snapshot/gtfs-validator*.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name --validation_report_name latest.json --system_errors_report_name latest_errors.json --skip_validator_update
   if [ "$master" = "--include-master" ];
   then
      java -Xmx12G -Xms8G -jar gtfs-validator-master/gtfs-validator*.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name --validation_report_name reference.json --system_errors_report_name reference_errors.json --skip_validator_update
   fi;
   wait
done
