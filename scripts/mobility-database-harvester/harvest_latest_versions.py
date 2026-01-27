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

#  This CSV is commited in git for reproducibility of the acceptance tests.
# It  can be generated in the mobility-feed-api project.
# See scripts/mobility-database-harvester/README.md for more information.
FEEDS_CSV = "scripts/mobility-database-harvester/acceptance_test_feed_list.csv"
LATEST_URL = "urls.latest"

# Github constants
# As per https://docs.github.com/en/actions/administering-github-actions/usage-limits-billing-and-administration#usage-limits
MAX_JOB_NUMBER = 60

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
    """Harvests the latest URLs from the provided csv.
    The only columns of interest are 'stable_id' and 'urls.latest'.
    The files may contain other columns, but they are ignored.
    :return: The dictionary of the latest URLs with the format {Name: Url}.
    """
    catalogs_gtfs = pd.read_csv(FEEDS_CSV)
    latest_versions = {}

    for _, row in catalogs_gtfs.iterrows():
        stable_id = row['stable_id']
        latest_url = row[LATEST_URL]
        latest_versions[stable_id] = latest_url

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
        description="Script to harvest the latest versions of feeds from a csv files and save them to a JSON file."
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
