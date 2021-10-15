my_array=$1
echo "$my_array"
echo "/n"
echo "/n"
IFS=" " read -a -r data_array <<< "$my_array"
echo "/n"
echo "/n"
echo ${#data_array[@]}
