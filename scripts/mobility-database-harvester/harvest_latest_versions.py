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
import dateutil.parser as dp
import json
import requests
import os
from os import path

from google.cloud import storage

###############################################################################
# This script harvests the latest versions of the sources listed in a catalog.
# Made for Python 3.9. Requires the modules listed in requirements.txt.
###############################################################################

# API constants
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


def get_credentials():
    credentials = {
        TYPE_KEY: os.getenv(TYPE).replace("\\n", "\n"),
        PROJECT_ID_KEY: os.getenv(PROJECT_ID).replace("\\n", "\n"),
        PRIVATE_KEY_ID_KEY: os.getenv(PRIVATE_KEY_ID).replace("\\n", "\n"),
        PRIVATE_KEY_KEY: os.getenv(PRIVATE_KEY).replace("\\n", "\n"),
        CLIENT_EMAIL_KEY: os.getenv(CLIENT_EMAIL).replace("\\n", "\n"),
        CLIENT_ID_KEY: os.getenv(CLIENT_ID).replace("\\n", "\n"),
        AUTH_URI_KEY: os.getenv(AUTH_URI).replace("\\n", "\n"),
        TOKEN_URI_KEY: os.getenv(TOKEN_URI).replace("\\n", "\n"),
        AUTH_PROVIDER_X509_CERT_URL_KEY:
            os.getenv(AUTH_PROVIDER_X509_CERT_URL).replace("\\n", "\n"),
        CLIENT_X509_CERT_URL_KEY:
            os.getenv(CLIENT_X509_CERT_URL).replace("\\n", "\n")
    }
    return str(credentials).replace("'", '"')


def parse_archives_ids_file(data_path, filename):
    file_path = path.join(data_path, filename)
    if path.exists(file_path):
        with open(file_path, "r") as f:
            archives_ids_file = json.load(f)
        harvesting_date = archives_ids_file[HARVESTING_DATE]
        archives_ids = archives_ids_file[ARCHIVES_IDS]
    else:
        harvesting_date = EPOCH_DATE
        archives_ids = None
    return harvesting_date, archives_ids


def save_content_to_file(content, data_path, filename):
    file_path = path.join(data_path, filename)
    with open(file_path, "w") as f:
        json.dump(content, f)


def save_archives_ids_file(harvesting_date, archives_ids, data_path, filename):
    archives_ids_json =\
        {HARVESTING_DATE: harvesting_date, ARCHIVES_IDS: archives_ids}
    save_content_to_file(archives_ids_json, data_path, filename)


def get_entity_data(entity_id):
    query = {ACTION: WBGETENTITIES, IDS: entity_id, FORMAT: JSON}
    response = requests.get(API_URL, params=query)
    response_json = response.json()
    return response_json[ENTITIES][entity_id]


def has_been_modified_since(catalog_data, last_harvesting_date):
    modification_date = catalog_data[MODIFIED]
    last_harvesting_date = dp.parse(last_harvesting_date)
    modification_date = dp.parse(modification_date)
    return modification_date > last_harvesting_date


def harvest_archives_ids(catalog_data):
    harvesting_date = catalog_data[MODIFIED]
    archives_ids = []

    sources = catalog_data[CLAIMS][SOURCE_PROPERTY]
    for source in sources:
        source_id = source[MAINSNAK][DATAVALUE][VALUE][ID]
        source_data = get_entity_data(source_id)
        source_archives_id = source_data[CLAIMS][ARCHIVES_ID_PROPERTY][0][MAINSNAK][
            DATAVALUE
        ][VALUE]
        archives_ids.append(source_archives_id)

    return harvesting_date, archives_ids


def harvest_latest_versions(archives_ids):
    client = storage.Client.from_service_account_info(
        info=json.loads(get_credentials())
    )
    latest_versions = {}

    for archives_id in archives_ids:
        bucket_id = client.lookup_bucket(
            LATEST_BUCKET_PATH.format(source_archives_id=archives_id)
        )
        if bucket_id is not None:
            bucket = client.get_bucket(
                LATEST_BUCKET_PATH.format(source_archives_id=archives_id)
            )
            blobs = client.list_blobs(bucket.name)
            for blob in blobs:
                archives_url = LATEST_URL.format(
                    source_archives_id=archives_id, blob_name=blob.name
                )
                latest_versions[archives_id] = archives_url

    return latest_versions


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Harvesting script for the latest versions. Python 3.9."
    )
    parser.add_argument(
        "-a",
        "--archives-ids-file",
        action="store",
        help="Name of the archives ids file. If the file does not exist, the script will create it.",
    )
    parser.add_argument(
        "-l",
        "--latest-versions-file",
        action="store",
        help="Name of the latest versions file. If the file exists, it will be overwritten.",
    )
    parser.add_argument(
        "-d",
        "--data-path",
        action="store",
        default="./data/",
        help="Data path.",
    )
    args = parser.parse_args()

    archives_ids_file = args.archives_ids_file
    latest_versions_file = args.latest_versions_file
    data_path = args.data_path

    if not path.isdir(data_path) and path.exists(data_path):
        raise Exception("Data path must be a directory if existing.")
    elif not path.isdir(data_path):
        os.mkdir(data_path)

    harvesting_date, archives_ids = parse_archives_ids_file(
        data_path, archives_ids_file
    )

    catalog_id = GTFS_CATALOG_ID
    catalog_data = get_entity_data(catalog_id)

    if has_been_modified_since(catalog_data, harvesting_date):
        harvesting_date, archives_ids = harvest_archives_ids(catalog_data)
        save_archives_ids_file(
            harvesting_date, archives_ids, data_path, archives_ids_file
        )

    latest_versions = harvest_latest_versions(archives_ids)
    save_content_to_file(latest_versions, data_path, latest_versions_file)
