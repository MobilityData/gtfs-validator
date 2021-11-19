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

###############################################################################
# This script generates a text to be used as a PR comment.
# Made for Python 3.9.
###############################################################################

NEW_ERRORS = "newErrors"
AFFECTED_SOURCES = "affectedSources"
AFFECTED_SOURCES_COUNT = "affectedSourcesCount"


def load_content(data_path):
    with open(data_path, "r") as f:
        content = json.load(f)
    return content


def get_url(archive_id, path_to_urls):
    urls = load_content(path_to_urls)
    return urls[archive_id]


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script to generate comment for acceptance test using Python 3.9."
    )
    parser.add_argument(
        "-u",
        "--path_to_urls",
        action="store",
        help="Path to urls ",
    )
    parser.add_argument(
        "-c",
        "--commit_id",
        action="store",
        help="Commit id",
    )
    parser.add_argument(
        "-r", "--run_id", action="store", help="id of the run from github"
    )
    parser.add_argument(
        "-a",
        "--acceptance_test_report_path",
        action="store",
        help="Path to acceptance test report",
    )
    args = parser.parse_args()

    acceptance_test_report = load_content(args.acceptance_test_report_path)
    urls_map = load_content(args.path_to_urls)
    comment = "Thank you for this contribution."

    if len(list(acceptance_test_report[NEW_ERRORS])) != 0:
        comment = comment + " Due to changes in this pull request, the " \
                            "following validation rules trigger new errors:\n"

        for notice_sample in acceptance_test_report[NEW_ERRORS]:
            notice_code = list(notice_sample.keys())[0]
            notice_info = f"- `{notice_code}`: {notice_sample[notice_code][AFFECTED_SOURCES_COUNT]} datasets (including "
            for source_ids in notice_sample[notice_code][AFFECTED_SOURCES]:
                for source_id in list(source_ids.keys()):
                    notice_info += (
                        f"[`{source_id}`]({get_url(source_id, args.path_to_urls)}), "
                    )
            comment = comment + notice_info[:-2] + ")\n"
    else:
        comment = comment + " The changes in this pull request did not trigger any new errors on known GTFS datasets from the [MobilityDatabase](http://mobilitydatabase.org/wiki/Main_Page)."
    comment = (
        comment
        + f"\nDownload the full acceptance test report for commit {args.commit_id} [here](https://github.com/MobilityData/gtfs-validator/actions/runs/{args.run_id}) (report will disappear after 90 days)."
    )

    print(comment)
