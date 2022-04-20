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
from datetime import datetime as dt
import os
from os import path
import json
from tqdm import tqdm

###############################################################################
# This script harvests the latest dataset versions on the Mobility Database.
# Made for Python 3.9. Requires the modules listed in requirements.txt.
###############################################################################

# API constants
API_URL = "http://old.mobilitydatabase.org/w/api.php?"
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
TYPE = "TYPE"
PROJECT_ID = "PROJECT_ID"
PRIVATE_KEY_ID = "PRIVATE_KEY_ID"
PRIVATE_KEY = "PRIVATE_KEY"
CLIENT_EMAIL = "CLIENT_EMAIL"
CLIENT_ID = "CLIENT_ID"
AUTH_URI = "AUTH_URI"
TOKEN_URI = "TOKEN_URI"
AUTH_PROVIDER_X509_CERT_URL = "AUTH_PROVIDER_X509_CERT_URL"
CLIENT_X509_CERT_URL = "CLIENT_X509_CERT_URL"
DATASET_PROPERTY = "P15"
DOWNLOAD_DATE = "P32"
URL_PROPERTY = "P13"

# Credentials keys
TYPE_KEY = "type"
PROJECT_ID_KEY = "project_id"
PRIVATE_KEY_ID_KEY = "private_key_id"
PRIVATE_KEY_KEY = "private_key"
CLIENT_EMAIL_KEY = "client_email"
CLIENT_ID_KEY = "client_id"
AUTH_URI_KEY = "auth_uri"
TOKEN_URI_KEY = "token_uri"
AUTH_PROVIDER_X509_CERT_URL_KEY = "auth_provider_x509_cert_url"
CLIENT_X509_CERT_URL_KEY = "client_x509_cert_url"

# Catalog constants
GTFS_CATALOG_ID = "Q6"

# Archives ids file constants
ARCHIVES_IDS = "archives_ids"
HARVESTING_DATE = "harvesting_date"
EPOCH_DATE = "1970-01-01T00:00:00Z"

# Google Cloud constants
LATEST_BUCKET_PATH = "{source_archives_id}_latest"
LATEST_URL = "https://storage.googleapis.com/storage/v1/b/{source_archives_id}_latest/o/{blob_name}?alt=media"
ARCHIVES_URL_PREFIX = "https://storage.googleapis.com/storage"

# Script constants
UNKNOWN_DATE = "unknown-date"
DATE_FORMAT = "%Y-%m-%d"
OLDER_VERSIONS = "older_versions"
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


def list_datasets(source_data):
    return list_entities(source_data, DATASET_PROPERTY)


def get_date(dataset_data):
    date_string = dataset_data[CLAIMS][DOWNLOAD_DATE][0][MAINSNAK][DATAVALUE][VALUE]
    return date_string


def get_archives_url(dataset_data):
    dataset_url = None
    if dataset_data is not None:
        for potential_url in dataset_data[CLAIMS][URL_PROPERTY]:
            url_value = potential_url[MAINSNAK][DATAVALUE][VALUE]
            if ARCHIVES_URL_PREFIX in url_value:
                dataset_url = url_value
    return dataset_url


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

    jobs = np.array_split(list(json_data.keys()), min(MAX_JOB_NUMBER, len(list(json_data.keys()))))
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
        "-o",
        "--older-versions-file",
        action="store",
        help="Name of the older versions file. If the file exists, it will be overwritten.",
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

    older_versions_file = args.older_versions_file
    latest_versions_file = args.latest_versions_file
    data_path = args.data_path

    if not path.isdir(data_path) and path.exists(data_path):
        raise Exception("Data path must be a directory if existing.")
    elif not path.isdir(data_path):
        os.mkdir(data_path)

    if path.exists(path.join(data_path, older_versions_file)):
        older_versions_json = load_content(data_path, older_versions_file)
        older_versions = set(older_versions_json[OLDER_VERSIONS])
    else:
        older_versions = set()

    catalog_id = GTFS_CATALOG_ID
    catalog_data = get_entity_data(catalog_id)

    sources = list_sources(catalog_data)

    latest_versions = {}
    for source in tqdm(sources):
        source_data = get_entity_data(source)
        source_archives_id = get_archives_id(source_data)
        datasets = list_datasets(source_data)

        # Using Epoch for comparison
        latest_date = dt(1970, 1, 1)
        latest_dataset = None
        latest_dataset_data = None

        # Subtract the older versions before requesting the database
        # This allows to reduce considerably the number of requests made to the database
        datasets = set(datasets).difference(older_versions)
        if len(datasets) > 0:
            for dataset in datasets:
                dataset_data = get_entity_data(dataset)
                download_date = get_date(dataset_data)
                if download_date != UNKNOWN_DATE:
                    download_date = dt.strptime(download_date, DATE_FORMAT)
                    if download_date > latest_date:
                        older_versions.add(latest_dataset)
                        latest_dataset = dataset
                        latest_dataset_data = dataset_data
                        latest_date = download_date
                    else:
                        older_versions.add(dataset)
                else:
                    older_versions.add(dataset)
            latest_url = get_archives_url(latest_dataset_data)
            if latest_url is not None:
                latest_versions[source_archives_id] = latest_url

    # Make sure there is no None in the older version set
    older_versions.discard(None)

    save_content_to_file(
        {OLDER_VERSIONS: list(older_versions)}, data_path, older_versions_file
    )
    save_content_to_file(
        latest_versions, data_path, latest_versions_file
    )
    github_formatted_latest_versions = apply_github_matrix_formatting(
        load_content(data_path, latest_versions_file)
    )
    save_content_to_file(
        github_formatted_latest_versions,
        data_path,
        GITHUB_FORMATTED_LATEST_VERSIONS_JSON,
    )
