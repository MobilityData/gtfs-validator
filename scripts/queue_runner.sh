#!/bin/bash
closing_curly_bracket="}"
raw_queue_string=$*
IFS=" " read -a queue <<< $raw_queue_string
for item in "${queue[@]}"
do
   item=${item//\{id/\{\"id\"}
   item=${item//,/\",}
   item=${item//\,url/\,\"url\"}
   item=${item//\":/\":\"}
   item=${item//$closing_curly_bracket/\"$closing_curly_bracket}

   ID=$(jq '.id' <<< "$item")
   URL=$(jq '.url' <<< "$item")
   path_name=${ID//\"/}
   java -Xmx8G -Xms8G -jar gtfs-validator*SNAPSHOT_cli.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name --validation_report_name v3_latest.json --system_errors_report_name v3_latest_errors.json
   java -Xmx8G -Xms8G -jar gtfs-validator*v2_cli.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name -f us-test
   wait
done
