#!/bin/bash
closing_curly_bracket="}"
raw_queue_string=$*
IFS=" " read -a queue <<< $raw_queue_string
for el in "${queue[@]}"
do
   el=${el//\{id/\{\"id\"}
   el=${el//,/\",}
   el=${el//\,url/\,\"url\"}
   el=${el//\":/\":\"}
   el=${el//$closing_curly_bracket/\"$closing_curly_bracket}

   ID=$(jq '.id' <<< "$el")
   URL=$(jq '.url' <<< "$el")
   path_name=${ID//\"/}
   java -Xmx8G -Xms8G -jar gtfs-validator*.jar --url $URL --output_base output/$path_name
done
