/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.parsing;

import com.google.common.base.Strings;
import java.util.HashMap;
import javax.annotation.Nullable;

/** Read access to a header row in a CSV file. */
public class CsvHeader {
  private final HashMap<String, Integer> columnIndices = new HashMap<>();
  private final String[] columnNames;

  public static final CsvHeader EMPTY = new CsvHeader(null);

  /**
   * Creates a header for the given column names.
   *
   * <p>Empty or null columns names are treated as {@code ""}.
   */
  public CsvHeader(@Nullable String[] columnNames) {
    this.columnNames = columnNames == null ? new String[] {} : columnNames;
    for (int i = 0; i < this.columnNames.length; ++i) {
      if (Strings.isNullOrEmpty(columnNames[i])) {
        this.columnNames[i] = "";
      } else {
        columnIndices.putIfAbsent(columnNames[i], i);
      }
    }
  }

  /** Returns all columns in the table. */
  public String[] getColumnNames() {
    return columnNames;
  }

  /** Returns column name at given index, or null if the index is out of bounds. */
  @Nullable
  public String getColumnName(int columnIndex) {
    if (columnIndex < 0 || columnIndex >= columnNames.length) {
      return null;
    }
    return columnNames[columnIndex];
  }

  /** Returns the amount of columns. */
  public int getColumnCount() {
    return columnNames.length;
  }

  /** Returns index of a column with the given name, or -1 if there is no such column. */
  public int getColumnIndex(String columnName) {
    return columnIndices.getOrDefault(columnName, -1);
  }

  /** Tells if a column with the given name exists. */
  public boolean hasColumn(String columnName) {
    return columnIndices.containsKey(columnName);
  }
}
