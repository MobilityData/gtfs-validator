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
   java -Xmx8G -Xms8G -jar gtfs-validator*SNAPSHOT.jar --url $URL --output_base output/$path_name
done
