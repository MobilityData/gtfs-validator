import os
from utils.utils import get_migration_file

if __name__ == '__main__':
    migration_table = get_migration_file()
    previous_version = sorted(list(migration_table.columns))[-1].lower()

    env_file = os.getenv('GITHUB_ENV')
    with open(env_file, "a") as gh_env:
        gh_env.write(f"PREVIOUS_VERSION={previous_version}\n")

    print(previous_version)
