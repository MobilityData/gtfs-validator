import pandas as pd
import argparse
import jsondiff
import json
import numpy as np
from utils.utils import get_migration_table


def read_rule_file(filepath):
    """
    Reads and parses rules.json
    :param filepath: Path containing rules.json
    :return: dictionary where the key is the rule id and the value is the severity
    """
    with open(filepath + "/rules.json", 'r') as f:
        rules = json.load(f)
    return {key: rules[key]["severityLevel"] for key in rules}


def get_severity_symbol(severity):
    """
    Links severity to symbol for the PR description formatting
    :param severity: severity value
    :return: associated symbol
    """
    if severity == "WARNING":
        return '🟡'
    if severity == "ERROR":
        return '🔴'
    return '⚪'


if __name__ == '__main__':
    # Parse arguments
    parser = argparse.ArgumentParser(description="Automatic generation of NOTICE_MIGRATION.md")
    parser.add_argument('-r', '--release', help="Release Version", required=True)
    args = parser.parse_args()
    version = args.release.upper()

    # Init the PR description output
    output = f"# Automated update of NOTICE_MIGRATION.md for release {version.lower()}\n"

    migration_table: pd.DataFrame = get_migration_table()
    migration_table.fillna('', inplace=True)

    previous_version = sorted(list(migration_table.columns))[-1]  # Retrieve previous version
    migration_table.insert(0, version, migration_table[previous_version])

    # Read rules.json
    rules_1 = read_rule_file(f"rules-{version.lower()}")
    rules_2 = read_rule_file(f"rules-{previous_version.lower()}")

    # Process added notices
    diff = jsondiff.diff(rules_1, rules_2)
    new_notices = []
    try:
        new_notices = diff[jsondiff.delete]
        i = len(migration_table.index)
        for new_notice in new_notices:
            migration_table.loc[i + 1] = [f"{rules_1[new_notice]}-{new_notice}"] \
                                         + ['' for _ in range(len(migration_table.columns) - 1)]
            i += 1
        output += "## Notices added : \n" \
                  + "\n ".join(
                        [
                            f"- `{new_notice}`: {get_severity_symbol(rules_1[new_notice])} {rules_1[new_notice]}"
                            for new_notice in new_notices
                        ]
                    ) + "\n"
    except KeyError:
        output += "*No added notice. *\n"
    output += "\n"


    # Process deleted notices
    diff = jsondiff.diff(rules_2, rules_1)
    try:
        deleted_notices = diff[jsondiff.delete]
        for deleted_notice in deleted_notices:
            migration_table.loc[
                migration_table[migration_table[version].str.endswith(deleted_notice)].index, version] = ''
        output += "## Notices deleted :\n " \
                  + ", ".join(
                        [
                            f"`{deleted_notice}`" for deleted_notice in deleted_notices
                        ]
                    ) + "\n"
    except KeyError:
        output += "*No deleted notice. *\n"
    output += "\n"

    # Process notices with severity update
    output += "## Notices change in severity level : \n"
    for notice, severity in diff.items():
        if notice in new_notices or notice == jsondiff.delete:
            continue
        migration_table.loc[migration_table[migration_table[version].str.endswith(notice)].index, version] = \
            f"{severity}-{notice}"
        output += f"- `{notice}` changed from {get_severity_symbol(rules_2[notice])} {rules_2[notice]} " \
                  f"to {get_severity_symbol(severity)} {severity} \n"
    if not len(diff):
        output += "None"

    migration_table.replace('-', ' - ', regex=True, inplace=True)
    migration_table.replace('', np.nan, regex=True, inplace=True)

    # Update NOTICE_MIGRATION.md
    notice_migration_file = "docs/NOTICE_MIGRATION.md"
    with open(notice_migration_file, 'r') as f:
        file_content = f.read()

    new_file_content = "\n".join([line for line in file_content.split('\n') if not line.startswith('|')]) \
                       + "\n" + migration_table \
                           .sort_values(by=[version, previous_version], na_position='last') \
                           .fillna('') \
                           .to_markdown(index=False)

    with open(notice_migration_file, 'w') as f:
        f.write(new_file_content)
    print(output)
