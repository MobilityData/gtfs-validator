#!/bin/bash
closing_curly_bracket="}"
open_curly_bracket="{"
array_string=$*
IFS=" " read -a my_array <<< $array_string
echo "${my_array[@]}"
for el in "${my_array[@]}"
do
   el=${el//$open_curly_bracketid/\"id\"}
   el=${el//url/\"url\"}
   el=${el//,/\",}
   el=${el//\":/\":\"}
   el=${el//$closing_curly_bracket/\"$closing_curly_bracket}

   ID=$(jq '.id' <<< "$el")
   URL=$(jq '.url' <<< "$el")
   echo $el
   echo "\n"
   path_name=${ID//\"/}
   response=$(curl --write-out '%{http_code}' --silent --output /dev/null $URL)
   if [ $response == 404 ]; then
     continue
   fi
   echo $path_name
   echo $URL
   java -Xmx8G -Xms8G -jar gtfs-validator*.jar --url $URL --output_base output/$path_name
done
