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
ACCEPTANCE_TEST_STATUS = "status"
NOTICE_CODE = "noticeCode"
CORRUPTED_SOURCES_COUNT = "corruptedSourcesCount"
NEW_ERRORS = "newErrors"
AFFECTED_SOURCES = "affectedSources"
AFFECTED_SOURCES_COUNT = "affectedSourcesCount"
SOURCE_ID_KEY = "sourceId"


def load_content(data_path):
    with open(data_path, "r") as f:
        content = json.load(f)
    return content


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script to generate comment for acceptance test using Python 3.9."
    )
    parser.add_argument(
        "-a",
        "--acceptance_test_report_path",
        action="store",
        help="Path to acceptance test report",
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
        "-u",
        "--path_to_urls",
        action="store",
        help="Path to urls ",
    )
    parser.add_argument(
        "-x",
        "--corrupted_sources_report_path",
        action="store",
        help="Path to corrupted sources report",
    )
    args = parser.parse_args()

    acceptance_test_report = load_content(args.acceptance_test_report_path)
    corrupted_sources_report = load_content(args.corrupted_sources_report_path)
    urls_map = load_content(args.path_to_urls)
    comment = (
        "Thank you for this contribution! 🍰✨🦄 \n\n"
        "### Information about source "
        "corruption \n\n"
        f"{corrupted_sources_report['corruptedSourcesCount']} out of "
        f"{corrupted_sources_report['sourceIdCount']}"
        f" sources are corrupted."
    )
    if corrupted_sources_report[ACCEPTANCE_TEST_STATUS] == "invalid":
        comment = (
            comment + " Hence the results of this acceptance test "
            "execution are not reliable."
        )
    if corrupted_sources_report[CORRUPTED_SOURCES_COUNT] != 0:
        comment = comment + "\nThe following sources are corrupted: \n"
    for source_id in corrupted_sources_report["corruptedSources"]:
        comment = comment + f"- [`{source_id}`]({urls_map.get(source_id)})"
    comment = comment + "\n\n### Acceptance test details\n"
    if len(list(acceptance_test_report[NEW_ERRORS])) != 0:
        comment = (
            comment + "\nDue to changes in this pull request, the "
            "following validation rules trigger new errors:\n"
        )

        for notice_sample in acceptance_test_report[NEW_ERRORS]:
            notice_code = notice_sample[NOTICE_CODE]
            notice_info = f"- `{notice_code}`: {notice_sample[AFFECTED_SOURCES_COUNT]} datasets (including "
            for source_infos in notice_sample[AFFECTED_SOURCES]:
                source_id = source_infos[SOURCE_ID_KEY]
                notice_info += f"[`{source_id}`]({urls_map.get(source_id)}), "
            comment = comment + notice_info[:-2] + ")\n"
    else:
        comment = (
            comment
            + " The changes in this pull request did not trigger any new errors on known GTFS datasets from the [MobilityDatabase](http://old.mobilitydatabase.org/wiki/Main_Page)."
        )
    comment = (
        comment
        + f"\nDownload the full acceptance test report for commit {args.commit_id} [here](https://github.com/MobilityData/gtfs-validator/actions/runs/{args.run_id}) (report will disappear after 90 days)."
    )

    print(comment)
