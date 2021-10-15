#!/bin/bash

echo 1
array_string=$*
echo 2
echo "$array_string"
echo 3
IFS=" " read -a my_array <<< "$array_string"
echo 4
echo ${#my_array[@]}
echo "end"
