echo "\n"
echo "\n"
array_string=$*
echo "\n"
echo "\n"
echo $array_string
echo "\n"
echo "\n"
IFS=" " read -a my_array <<< $array_string
echo "\n"
echo "\n"
echo ${#my_array[@]}
