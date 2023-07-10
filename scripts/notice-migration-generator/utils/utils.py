from io import StringIO
import pandas as pd


def get_migration_table():
    notice_migration_file = "docs/NOTICE_MIGRATION.md"
    with open(notice_migration_file, 'r') as f:
        file_content = f.read()

    migration_table_string = "\n".join([line for line in file_content.split('\n') if line.startswith('|')])
    table: pd.DataFrame = pd.read_csv(
        StringIO(migration_table_string.replace(' ', '')),
        sep='|'
    ).dropna(
        axis=1,
        how='all'
    ).iloc[1:]
    return table
