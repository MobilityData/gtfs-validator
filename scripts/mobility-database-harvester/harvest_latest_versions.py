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
import requests
from concurrent.futures import ThreadPoolExecutor
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
STABLE_ID = "stable_id"

# The full Mobility Database catalog, for manual runs over more feeds than the committed list.
CATALOG_CSV = "https://files.mobilitydatabase.org/feeds_v2.csv"
CATALOG_ID = "id"
CATALOG_DATA_TYPE = "data_type"
CATALOG_STATUS = "status"
GTFS = "gtfs"
DEPRECATED = "deprecated"
# Feed lists: the committed list, or the catalog's GTFS feeds that have a latest dataset, either
# only the active ones or all but the deprecated ones.
ACCEPTANCE_LIST = "acceptance"
ACTIVE = "active"
NON_DEPRECATED = "non-deprecated"
FEED_LISTS = [ACCEPTANCE_LIST, ACTIVE, NON_DEPRECATED]

# Github constants
# As per https://docs.github.com/en/actions/administering-github-actions/usage-limits-billing-and-administration#usage-limits
MAX_JOB_NUMBER = 60

# json keys
ROOT = "include"
URL_KEY = "url"
DATA = "data"
ID = "id"
RUNNER = "runner"

# Job grouping constants
MB = 1_000_000
# Feeds above this size (zipped) are validated on the large runner. Below it, on the small runner.
DEFAULT_LARGE_FEED_THRESHOLD_MB = 50
# Fixed cost of a feed regardless of its size (download, JVM start-up, report writing), expressed
# as an equivalent size so that a job of many tiny feeds is not mistaken for a light one.
DEFAULT_PER_FEED_OVERHEAD_MB = 10
DEFAULT_SMALL_RUNNER = "ubuntu-24.04"
DEFAULT_LARGE_RUNNER = "ubuntu-latest-4-cores"
SIZE_REQUEST_TIMEOUT_SECONDS = 30
SIZE_REQUEST_WORKERS = 32
USER_AGENT = "MobilityData GTFS-Validator (acceptance tests)"

# Sampling constants

SAMPLES = set(
    [
        "mdb-6",     # Buenos Aires, AR
        "mdb-8",     # São Paulo, BR
        "mdb-13",    # San Diego, US - Fares V1, Fares V2, Pathways
        "mdb-28",    # LA Go Bus, Los Angeles, US - Fares V1
        "mdb-53",    # BART, San Francisco, US - Fares V1
        "mdb-77",    # Sacramento, US - Fares V1, Fares V2
        "mdb-314",   # Compton, US - Fares V2
        "mdb-389",   # CTA, Chicago, US
        "mdb-510",   # NYC Bus, MTA, New York City, US
        "mdb-516",   # NYC Subway, MTA, New York City, US
        "mdb-782",   # Berlin, DE - Pathways
        "mdb-791",   # Madrid, ES
        "mdb-865",   # Helsinki, FI
        "mdb-892",   # Barcelona (aggregated feed), ES
        "mdb-990",   # Budapest, HU - Pathways
        "mdb-1073",  # Taichung, TW
        "mdb-1090",  # Germany Urban Transport (aggregated feed), DE
        "mdb-1132",  # Wellington, NZ
        "mdb-1294",  # Rome, IT
        "mdb-1329",  # Abu Dhabi, AE
        "mdb-1788",  # Wasco, US - Flex V2
        "mdb-1791",  # Rio de Janeiro, BR
        "mdb-1807",  # Bamako, ML
        "mdb-1815",  # Nairobi, KE
    ]
)


def save_content_to_file(content, data_path, filename):
    """Saves content to JSON file.
    :param content: The content to save.
    :param data_path: The path to the folder where to save the content.
    :param filename: The file name for the JSON file.
    """
    file_path = path.join(data_path, filename)
    with open(file_path, "w") as f:
        json.dump(content, f)


def read_catalog(feed_list):
    """Reads the GTFS feeds that have a latest dataset from the Mobility Database catalog.
    :param feed_list: ACTIVE or NON_DEPRECATED.
    :return: A data frame with the columns 'stable_id' and 'urls.latest'.
    """
    catalog = pd.read_csv(CATALOG_CSV, low_memory=False)
    catalog = catalog[
        (catalog[CATALOG_DATA_TYPE] == GTFS) & catalog[LATEST_URL].notna()
    ]
    if feed_list == ACTIVE:
        catalog = catalog[catalog[CATALOG_STATUS] == ACTIVE]
    else:
        catalog = catalog[catalog[CATALOG_STATUS] != DEPRECATED]
    return catalog.rename(columns={CATALOG_ID: STABLE_ID})[[STABLE_ID, LATEST_URL]]


def harvest_latest_versions(to_sample, feed_list=ACCEPTANCE_LIST):
    """Harvests the latest URLs from the provided csv.
    The only columns of interest are 'stable_id' and 'urls.latest'.
    The files may contain other columns, but they are ignored.
    :param to_sample: Boolean flag. Sample the sources in the CSV if True.
    :param feed_list: One of FEED_LISTS: the committed list, or feeds of the catalog.
    :return: The dictionary of the latest URLs with the format {Name: Url}.
    """
    if feed_list == ACCEPTANCE_LIST:
        catalogs_gtfs = pd.read_csv(FEEDS_CSV)
    else:
        catalogs_gtfs = read_catalog(feed_list)
    latest_versions = {}

    if to_sample:
        catalogs_gtfs = catalogs_gtfs[catalogs_gtfs[STABLE_ID].isin(SAMPLES)]

    for _, row in catalogs_gtfs.iterrows():
        stable_id = row[STABLE_ID]
        latest_url = row[LATEST_URL]
        latest_versions[stable_id] = latest_url

    return latest_versions


def fetch_feed_size(url):
    """Returns the size in bytes of the feed at the given URL, read from a HEAD request.
    :param url: The feed URL.
    :return: The size in bytes, or None if it could not be determined.
    """
    try:
        response = requests.head(
            url,
            allow_redirects=True,
            timeout=SIZE_REQUEST_TIMEOUT_SECONDS,
            headers={"User-Agent": USER_AGENT},
        )
        response.raise_for_status()
        return int(response.headers["Content-Length"])
    except (requests.RequestException, KeyError, ValueError):
        return None


def fetch_feed_sizes(latest_urls):
    """Fetches the size of every feed in parallel.
    :param latest_urls: The dictionary of the latest URLs with the format {Name: Url}.
    :return: The dictionary of sizes in bytes with the format {Name: size or None}.
    """
    ids = list(latest_urls.keys())
    with ThreadPoolExecutor(max_workers=SIZE_REQUEST_WORKERS) as executor:
        sizes = executor.map(fetch_feed_size, [latest_urls[feed_id] for feed_id in ids])
    return dict(zip(ids, sizes))


def split_into_balanced_jobs(weights, job_count):
    """Splits feeds into jobs of similar total weight, heaviest feed first, each one going to the
    job with the least weight so far (longest-processing-time-first scheduling).
    :param weights: The dictionary of feed weights with the format {Name: weight}.
    :param job_count: The number of jobs to create.
    :return: The list of jobs, each one a list of feed IDs, heaviest job first.
    """
    jobs = [[] for _ in range(job_count)]
    loads = [0] * job_count
    for feed_id in sorted(weights, key=lambda feed: (-weights[feed], feed)):
        lightest_job = loads.index(min(loads))
        jobs[lightest_job].append(feed_id)
        loads[lightest_job] += weights[feed_id]
    return [job for _, job in sorted(zip(loads, jobs), key=lambda pair: -pair[0]) if job]


def group_feeds_by_size(
    latest_urls,
    sizes,
    large_feed_threshold_mb,
    per_feed_overhead_mb,
    small_runner,
    large_runner,
):
    """Groups feeds into at most MAX_JOB_NUMBER jobs of similar validation work and picks a runner
    for each job. Feeds above the threshold, or whose size is unknown, go to the large runner.
    Jobs are shared between the two runners in proportion to their total weight.
    :return: The list of (runner, list of feed IDs) tuples, large-runner jobs first.
    """
    overhead = per_feed_overhead_mb * MB
    threshold = large_feed_threshold_mb * MB
    weights = {}
    large_ids = []
    small_ids = []
    for feed_id in latest_urls:
        size = sizes.get(feed_id)
        if size is None or size > threshold:
            large_ids.append(feed_id)
            # An unknown size is treated as the threshold so the feed is neither ignored nor
            # assumed to be the heaviest.
            weights[feed_id] = (size if size is not None else threshold) + overhead
        else:
            small_ids.append(feed_id)
            weights[feed_id] = size + overhead

    total_jobs = min(MAX_JOB_NUMBER, len(latest_urls))
    large_weight = sum(weights[feed_id] for feed_id in large_ids)
    small_weight = sum(weights[feed_id] for feed_id in small_ids)

    if not small_ids:
        large_jobs = total_jobs
    elif not large_ids:
        large_jobs = 0
    else:
        large_jobs = round(total_jobs * large_weight / (large_weight + small_weight))
        large_jobs = max(1, min(large_jobs, len(large_ids), total_jobs - 1))
    small_jobs = min(total_jobs - large_jobs, len(small_ids))

    grouped = []
    if large_jobs:
        large_weights = {feed_id: weights[feed_id] for feed_id in large_ids}
        for job in split_into_balanced_jobs(large_weights, large_jobs):
            grouped.append((large_runner, job))
    if small_jobs:
        small_weights = {feed_id: weights[feed_id] for feed_id in small_ids}
        for job in split_into_balanced_jobs(small_weights, small_jobs):
            grouped.append((small_runner, job))
    return grouped


def apply_github_matrix_formatting(latest_urls, grouped_jobs):
    """Transforms the grouped feeds to a GitHub matrix.
    :param latest_urls: The dictionary of the latest URLs with the format {Name: Url}.
    :param grouped_jobs: The list of (runner, list of feed IDs) tuples.
    :return: The GitHub matrix of latest URLs and runners for the workflow jobs.
    """
    latest_versions_data = []
    for runner, job_ids in grouped_jobs:
        datasets = [
            json.dumps({ID: job_id, URL_KEY: latest_urls[job_id]}, separators=(",", ":"))
            for job_id in job_ids
        ]
        latest_versions_data.append({DATA: " ".join(datasets), RUNNER: runner})
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
    parser.add_argument(
        "-s",
        "--sample",
        action="store_true",
        help="Boolean flag to sample or not the data.",
    )
    parser.add_argument(
        "--feeds",
        choices=FEED_LISTS,
        default=ACCEPTANCE_LIST,
        help="Feeds to validate: the committed acceptance test list, or the active or"
        " non-deprecated GTFS feeds of the Mobility Database catalog.",
    )
    parser.add_argument(
        "--large-feed-threshold-mb",
        type=float,
        default=DEFAULT_LARGE_FEED_THRESHOLD_MB,
        help="Feeds above this zipped size in MB are validated on the large runner.",
    )
    parser.add_argument(
        "--per-feed-overhead-mb",
        type=float,
        default=DEFAULT_PER_FEED_OVERHEAD_MB,
        help="Fixed cost of validating a feed, expressed as an equivalent size in MB.",
    )
    parser.add_argument(
        "--small-runner",
        default=DEFAULT_SMALL_RUNNER,
        help="Runner label for jobs that only hold feeds below the threshold.",
    )
    parser.add_argument(
        "--large-runner",
        default=DEFAULT_LARGE_RUNNER,
        help="Runner label for jobs that hold feeds above the threshold.",
    )
    args = parser.parse_args()

    latest_versions_file = args.latest_versions_file
    data_path = args.data_path
    to_sample = args.sample

    if not path.isdir(data_path) and path.exists(data_path):
        raise Exception("Data path must be a directory if existing.")
    elif not path.isdir(data_path):
        os.mkdir(data_path)

    latest_versions = harvest_latest_versions(to_sample, args.feeds)
    # We save the latest versions as a JSON file because it is used later in the "compare-outputs" job of the workflow.
    save_content_to_file(
        latest_versions,
        data_path,
        latest_versions_file,
    )
    grouped_jobs = group_feeds_by_size(
        latest_versions,
        fetch_feed_sizes(latest_versions),
        args.large_feed_threshold_mb,
        args.per_feed_overhead_mb,
        args.small_runner,
        args.large_runner,
    )
    github_formatted_latest_versions = apply_github_matrix_formatting(
        latest_versions, grouped_jobs
    )
    print(json.dumps(github_formatted_latest_versions, separators=(",", ":")))
