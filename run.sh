declare -a my_array
my_array=( "$@" )
echo ${#my_array[@]}
