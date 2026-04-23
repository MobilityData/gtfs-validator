#!/bin/bash

source "$(dirname "$0")/common.sh"

raw_queue_string="$1"
IFS=' ' read -r -a queue <<< "$raw_queue_string"

concatenated_ids=""
for item in "${queue[@]}"
do
   item=$(format_json "$item")

   ID=$(jq -r '.id' <<< "$item")
   number=$(echo "$ID" | grep -oE '[0-9]+$')

   if [ -z "$concatenated_ids" ]; then
      concatenated_ids="$number"
   else
      concatenated_ids="${concatenated_ids}_$number"
   fi
done

echo "$concatenated_ids"
