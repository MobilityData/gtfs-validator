# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import argparse

import numpy as np
import requests
import os
from os import path
import json
from tqdm import tqdm

###############################################################################
# This script harvests the latest dataset versions on the Mobility Database.
# Made for Python 3.9. Requires the modules listed in requirements.txt.
###############################################################################

# API constants
STABLE_URL_PROPERTY = "P13"
API_URL = "http://mobilitydatabase.org/w/api.php?"
ACTION = "action"
ARCHIVES_ID_PROPERTY = "P33"
CLAIMS = "claims"
ENTITIES = "entities"
IDS = "ids"
MODIFIED = "modified"
SOURCE_PROPERTY = "P5"
WBGETENTITIES = "wbgetentities"
FORMAT = "format"
JSON = "json"
MAINSNAK = "mainsnak"
DATAVALUE = "datavalue"
VALUE = "value"
ID = "id"

# Catalog constants
GTFS_CATALOG_ID = "Q6"

# Script constants
GITHUB_FORMATTED_LATEST_VERSIONS_JSON = "latest_versions.json"

# Github constants
MAX_JOB_NUMBER = 256

# json keys
ROOT = "include"
URL_KEY = "url"
DATA = "data"


def get_entity_data(entity_id):
    query = {ACTION: WBGETENTITIES, IDS: entity_id, FORMAT: JSON}
    response = requests.get(API_URL, params=query)
    response_json = response.json()
    return response_json[ENTITIES][entity_id]


def list_entities(data, entity_property):
    entities_list = []
    if entity_property in data[CLAIMS]:
        entities_json = data[CLAIMS][entity_property]
        entities_list = [
            entity[MAINSNAK][DATAVALUE][VALUE][ID] for entity in entities_json
        ]
    return entities_list


def list_sources(catalog_data):
    return list_entities(catalog_data, SOURCE_PROPERTY)


def get_archives_id(source_data):
    return source_data[CLAIMS][ARCHIVES_ID_PROPERTY][0][MAINSNAK][DATAVALUE][VALUE]


def save_content_to_file(content, data_path, filename):
    file_path = path.join(data_path, filename)
    with open(file_path, "w") as f:
        json.dump(content, f)


def load_content(data_path, filename):
    file_path = path.join(data_path, filename)
    with open(file_path, "r") as f:
        content = json.load(f)
    return content


def apply_github_matrix_formatting(json_data):
    latest_versions_data = []

    jobs = np.array_split(list(json_data.keys()), MAX_JOB_NUMBER)
    jobs = [list(job_archives_ids) for job_archives_ids in jobs]
    for job_archives_ids in jobs:
        latest_version_data_string = ""
        while len(job_archives_ids) > 0:
            archive_id = job_archives_ids.pop()
            dataset_information = {ID: archive_id, URL_KEY: json_data[archive_id]}
            latest_version_data_string = latest_version_data_string + json.dumps(
                dataset_information, separators=(",", ":")
            )
        job_data = {DATA: latest_version_data_string.replace("}{", "} {")}
        latest_versions_data.append(job_data)
    return {ROOT: latest_versions_data}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script to harvest the latest dataset versions on the Mobility Database. Python 3.9."
    )
    parser.add_argument(
        "-l",
        "--latest-versions-file",
        action="store",
        help="Name of the latest urls file. If the file exists, it will be overwritten.",
    )
    parser.add_argument(
        "-d",
        "--data-path",
        action="store",
        default="./data/",
        help="Data path.",
    )
    args = parser.parse_args()

    latest_versions_file = args.latest_versions_file
    data_path = args.data_path

    if not path.isdir(data_path) and path.exists(data_path):
        raise Exception("Data path must be a directory if existing.")
    elif not path.isdir(data_path):
        os.mkdir(data_path)

    catalog_id = GTFS_CATALOG_ID
    catalog_data = get_entity_data(catalog_id)

    sources = list_sources(catalog_data)

    latest_versions = {}
    for source in tqdm(sources):
        source_data = get_entity_data(source)
        source_archives_id = get_archives_id(source_data)
        latest_url = source_data[CLAIMS][STABLE_URL_PROPERTY][0][MAINSNAK][DATAVALUE][VALUE]
        latest_versions[source_archives_id] = latest_url

    save_content_to_file(
        latest_versions, data_path, latest_versions_file
    )
    github_formatted_latest_versions = apply_github_matrix_formatting(
        load_content(data_path, "gtfs_latest_versions.json")
    )
    save_content_to_file(
        github_formatted_latest_versions,
        data_path,
        latest_versions_file,
    )
