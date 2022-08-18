#!/bin/bash
closing_curly_bracket="}"
master="$1"
raw_queue_string="${@:2}"
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
   java -Xmx10G -Xms8G -jar gtfs-validator-snapshot/gtfs-validator*.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name --validation_report_name latest.json --system_errors_report_name latest_errors.json
   if [ "$master" = "true" ];
   then
      java -Xmx10G -Xms8G -jar gtfs-validator-master/gtfs-validator*.jar --url $URL --output_base $OUTPUT_BASE/output/$path_name --validation_report_name reference.json --system_errors_report_name reference_errors.json
   fi;
   wait
done
