/*
 * Copyright 2020 Google LLC
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

import javax.annotation.Nullable;

/**
 * Read access to a data row in a CSV file.
 */
public class CsvRow {
    private final CsvFile csvFile;
    private final long rowNumber;
    private final String[] columnValues;

    public CsvRow(CsvFile csvFile, long rowNumber, String[] columnValues) {
        this.csvFile = csvFile;
        this.rowNumber = rowNumber;
        this.columnValues = columnValues;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public int getColumnIndex(String columnName) {
        return csvFile.getColumnIndex(columnName);
    }

    public int getColumnCount() {
        return columnValues.length;
    }

    public String getColumnName(int columnIndex) {
        return csvFile.getColumnName(columnIndex);
    }

    /**
     * Returns base name of the file that contains this row, e.g., "stops.txt".
     *
     * @return file name
     */
    public String getFileName() {
        return csvFile.getFileName();
    }

    /**
     * Returns a value in this row for the requested column as a string.
     * <p>
     * Returns {@code null} in the following cases:
     * - the column index is out of bounds
     * - column value is not defined
     * - column value is an empty string ""
     * <p>
     * Example.
     * <p>
     * col0,col1,col2
     * a,,""
     * <p>
     * asString(0) == "a"
     * asString(1) == null  (no value specified)
     * asString(2) == null  (explicit empty string)
     * asString(3) == null  (index out of bounds)
     * asString(-1) == null (index out of bounds)
     *
     * @param columnIndex
     * @return value for the requested column or null
     */
    @Nullable
    public String asString(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnValues.length) {
            return null;
        }
        String s = columnValues[columnIndex];
        // Univocity CSV parser already returns null for no explicit value and for an explicit empty string "".
        // Here we just want to be sure that we always return null and never "".
        if (Strings.isNullOrEmpty(s)) {
            return null;
        }
        return s;
    }
}

