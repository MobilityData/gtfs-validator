#!/bin/bash

function format_json() {
    local raw_data="$1"
    local closing_curly_bracket="}"

    # Replace parts to form valid JSON objects
    raw_data=${raw_data//\{id/\{\"id\"}
    raw_data=${raw_data//,/\",}
    raw_data=${raw_data//\,url/\,\"url\"}
    raw_data=${raw_data//\":/\":\"}
    raw_data=${raw_data//$closing_curly_bracket/\"$closing_curly_bracket}

    # Correct the improperly escaped double quotes
    raw_data=${raw_data//\"\"/\"}

    echo "$raw_data"
}

function extract_last_number() {
    local id="$1"
    results=$(echo "$id" | grep -oE '[0-9]+$')
}
