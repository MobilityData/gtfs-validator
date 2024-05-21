#!/bin/bash

source "$(dirname "$0")/common.sh"

raw_queue_string="${@}"
IFS=" " read -a queue <<< $raw_queue_string

concatenated_ids=""
for item in "${queue[@]}"
do
   item=$(format_json "$item")

   ID=$(jq -r '.id' <<< "$item")
   number=$(extract_last_number "$ID")

   if [ -z "$concatenated_ids" ]; then
      concatenated_ids="$number"
   else
      concatenated_ids="${concatenated_ids}_$number"
   fi
done

echo "$concatenated_ids"
