import os
from utils.utils import get_migration_table

if __name__ == '__main__':
    # Read NOTICE_MIGRATION.md file
    migration_table = get_migration_table()

    # Retrieve last specified version
    previous_version = sorted(list(migration_table.columns))[-1].lower()

    # Set PREVIOUS_VERSION env variable for the GitHub action workflow
    env_file = os.getenv('GITHUB_ENV')
    with open(env_file, "a") as gh_env:
        gh_env.write(f"PREVIOUS_VERSION={previous_version}\n")

    print(previous_version)
