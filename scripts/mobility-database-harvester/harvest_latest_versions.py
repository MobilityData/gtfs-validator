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
import json
import pandas as pd
import os
from os import path

#####################################################################################
# This script harvests the latest dataset versions on the Mobility Database Catalogs.
# Made for Python 3.9. Requires the modules listed in requirements.txt.
#####################################################################################

# Mobility Database Catalogs constants
CATALOGS_CSV = "https://bit.ly/catalogs-csv"
LATEST = "urls.latest"
DATA_TYPE = "data_type"
GTFS = "gtfs"

# Google Cloud constants
URL_PREFIX = "https://storage.googleapis.com/storage/v1/b/mdb-latest/o/"
URL_SUFFIX = ".zip?alt=media"

# Script constants
GITHUB_FORMATTED_LATEST_VERSIONS_JSON = "latest_versions.json"

# Github constants
MAX_JOB_NUMBER = 256

# json keys
ROOT = "include"
URL_KEY = "url"
DATA = "data"
ID = "id"


def save_content_to_file(content, data_path, filename):
    file_path = path.join(data_path, filename)
    with open(file_path, "w") as f:
        json.dump(content, f)


def harvest_latest_versions():
    catalogs = pd.read_csv(CATALOGS_CSV)
    latest_versions = {}

    latest_urls = catalogs[LATEST].loc[catalogs[DATA_TYPE] == GTFS]
    for index, value in latest_urls.items():
        latest_url = value
        source_file_name = latest_url.replace(URL_PREFIX, "").replace(URL_SUFFIX, "")
        latest_versions[source_file_name] = latest_url

    return latest_versions


def apply_github_matrix_formatting(json_data):
    latest_versions_data = []

    jobs = np.array_split(list(json_data.keys()), min(MAX_JOB_NUMBER, len(list(json_data.keys()))))
    jobs = [list(job_ids) for job_ids in jobs]
    for job_ids in jobs:
        latest_version_data_string = ""
        while len(job_ids) > 0:
            job_id = job_ids.pop()
            dataset_information = {ID: job_id, URL_KEY: json_data[job_id]}
            latest_version_data_string = latest_version_data_string + json.dumps(
                dataset_information, separators=(",", ":")
            )
        job_data = {DATA: latest_version_data_string.replace("}{", "} {")}
        latest_versions_data.append(job_data)
    return {ROOT: latest_versions_data}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script to harvest the latest dataset versions on the Mobility Database Catalogs. Python 3.9."
    )
    parser.add_argument(
        "-d",
        "--data-path",
        action="store",
        default="./",
        help="Data path.",
    )
    args = parser.parse_args()

    latest_versions_file = args.latest_versions_file
    data_path = args.data_path

    if not path.isdir(data_path) and path.exists(data_path):
        raise Exception("Data path must be a directory if existing.")
    elif not path.isdir(data_path):
        os.mkdir(data_path)

    latest_versions = harvest_latest_versions()
    github_formatted_latest_versions = apply_github_matrix_formatting(
        latest_versions
    )
    save_content_to_file(
        github_formatted_latest_versions,
        data_path,
        GITHUB_FORMATTED_LATEST_VERSIONS_JSON,
    )
