from io import StringIO
import pandas as pd
import argparse

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Automatic generation of NOTICE_MIGRATION.md")
    parser.add_argument('-r', '--ref_name', help="GitHub Ref Name", default="refs/tags/v1.0.0")
    args = parser.parse_args()
    ref_name = args.ref_name

    print(f"Ref name = {ref_name}")

    notice_migration_file = "docs/NOTICE_MIGRATION.md"
    with open(notice_migration_file, 'r') as f:
        file_content = f.read()

    migration_table_string = "\n".join([line for line in file_content.split('\n') if line.startswith('|')])
    migration_table = pd.read_csv(
        StringIO(migration_table_string.replace(' ', '')),  # Get rid of whitespaces
        sep='|'
    ).dropna(
        axis=1,
        how='all'
    ).iloc[1:]
    print(migration_table)
