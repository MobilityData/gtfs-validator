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
import numpy as np
import os
from os import path

#####################################################################################
# This script harvests the latest dataset versions on the Mobility Database Catalogs.
# Made for Python 3.9. Requires the modules listed in requirements.txt.
#####################################################################################

# Mobility Database Catalogs constants
CATALOGS_CSV = "https://storage.googleapis.com/storage/v1/b/mdb-csv/o/sources.csv?alt=media"
LATEST_URL = "urls.latest"
DATA_TYPE = "data_type"
GTFS = "gtfs"

# Sources to exclude because they are too big for the workflow.
SOURCES_TO_EXCLUDE = ["de-unknown-rursee-schifffahrt-kg-gtfs-784"]

# Google Cloud constants
URL_PREFIX = "https://storage.googleapis.com/storage/v1/b/mdb-latest/o/"
URL_SUFFIX = ".zip?alt=media"

# Github constants
MAX_JOB_NUMBER = 256

# json keys
ROOT = "include"
URL_KEY = "url"
DATA = "data"
ID = "id"


def save_content_to_file(content, data_path, filename):
    """Saves content to JSON file.
    :param content: The content to save.
    :param data_path: The path to the folder where to save the content.
    :param filename: The file name for the JSON file.
    """
    file_path = path.join(data_path, filename)
    with open(file_path, "w") as f:
        json.dump(content, f)


def harvest_latest_versions():
    """Harvests the latest URLs from the Mobility Database catalogs.
    :return: The dictionary of the latest URLs with the format {Name: Url}.
    """
    catalogs = pd.read_csv(CATALOGS_CSV)
    latest_versions = {}

    latest_urls = catalogs[LATEST_URL].loc[catalogs[DATA_TYPE] == GTFS]
    for index, value in latest_urls.items():
        latest_url = value
        source_file_name = latest_url.replace(URL_PREFIX, "").replace(URL_SUFFIX, "")
        latest_versions[source_file_name] = latest_url

    # Some sources/datasets are too big for the workflow so we are excluding them.
    for source in SOURCES_TO_EXCLUDE:
        del latest_versions[source]

    return latest_versions


def apply_github_matrix_formatting(latest_urls):
    """Transforms the dictionary of latest URLs to a GitHub matrix.
    :param latest_urls: The dictionary of the latest URLs with the format {Name: Url}.
    :return: The GitHub matrix of latest URLs for the workflow jobs.
    """
    latest_versions_data = []

    jobs = np.array_split(list(latest_urls.keys()), min(MAX_JOB_NUMBER, len(list(latest_urls.keys()))))
    jobs = [list(job_ids) for job_ids in jobs]
    for job_ids in jobs:
        latest_version_data_string = ""
        while len(job_ids) > 0:
            job_id = job_ids.pop()
            dataset_information = {ID: job_id, URL_KEY: latest_urls[job_id]}
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
        "-l",
        "--latest-versions-file",
        action="store",
        help="Name of the latest urls file. If the file exists, it will be overwritten.",
    )
    parser.add_argument(
        "-d",
        "--data-path",
        action="store",
        default=".",
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
    # We save the latest versions as a JSON file because it is used later in the "compare-outputs" job of the workflow.
    save_content_to_file(
        latest_versions,
        data_path,
        latest_versions_file,
    )
    github_formatted_latest_versions = apply_github_matrix_formatting(
        latest_versions
    )
    print(github_formatted_latest_versions)
