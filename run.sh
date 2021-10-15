#!/bin/bash
array_string=$*
echo $array_string
IFS=" " read -a -r my_array <<< $array_string
echo ${#my_array[@]}

for el in "${my_array[@]}"
do
   echo "$el"
#   ID=jq '.id' <<< "$el"
#   URL=jq '.url' <<< "$el"
   # or do whatever with individual element of the array
done
