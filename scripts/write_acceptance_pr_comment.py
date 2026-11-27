"""Writes the acceptance test PR comment: the report summary, then the command for a full run.

The footer gives the command that runs the acceptance tests of the PR's branch against the latest
release on all the non-deprecated feeds. It is left out for a PR from a fork, whose branch cannot
run the workflow.

The summary is cut to fit GitHub's comment size limit, so that the footer is always kept.

Usage: write_acceptance_pr_comment.py <summary file> <output file>
Environment:
  GITHUB_REPOSITORY  owner/name of the repository
  HEAD_REPOSITORY    owner/name of the repository of the PR's branch
  HEAD_REF           the PR's branch
  LATEST_RELEASE     tag of the latest release, or empty if unknown
"""

import os
import sys

# GitHub rejects comments longer than this, in characters.
MAX_COMMENT_LENGTH = 65536
TRUNCATED = "\n\n*The summary is cut short here: see the full report in the run linked above.*\n"


def footer(repository, branch, release):
    against = f"the latest release (`{release}`)" if release else "the latest release"
    baseline = release or "<latest release>"
    return (
        "\n\n---\n"
        f"To compare this branch with {against} on all the non-deprecated feeds, run:\n"
        "```\n"
        f"gh workflow run acceptance_test.yml -R {repository} --ref {branch}"
        f" -f baseline_ref={baseline} -f feed_list=non-deprecated\n"
        "```\n"
    )


def main(summary_path, output_path):
    with open(summary_path, encoding="utf-8") as f:
        summary = f.read()
    repository = os.environ["GITHUB_REPOSITORY"]
    end = ""
    if os.environ.get("HEAD_REPOSITORY") == repository:
        end = footer(
            repository, os.environ["HEAD_REF"], os.environ.get("LATEST_RELEASE", "")
        )
    budget = MAX_COMMENT_LENGTH - len(end)
    if len(summary) > budget:
        cut = summary.rfind("\n", 0, budget - len(TRUNCATED))
        summary = summary[: max(cut, 0)] + TRUNCATED
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(summary + end)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit(f"Usage: {sys.argv[0]} <summary file> <output file>")
    main(sys.argv[1], sys.argv[2])
