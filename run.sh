#!/bin/bash
closing_curly_bracket="}"
array_string=$*
echo $array_string
IFS=" " read -a my_array <<< $array_string
echo ${#my_array[@]}
for el in "${my_array[@]}"
do
   echo "$el"
   el=${el//id/\"id\"}
   el=${el//url/\"url\"}
   el=${el//,/\",}
   el=${el//\":/\":\"}
   el=${el//$closing_curly_bracket/\"$closing_curly_bracket}

   echo $el
   ID=$(jq '.id' <<< "$el")
   URL=$(jq '.url' <<< "$el")
   echo $ID
   echo $URL
   # or do whatever with individual element of the array
done
