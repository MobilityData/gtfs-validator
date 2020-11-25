package org.mobilitydata.gtfsvalidator.parsing;

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
     * Returns null in the following cases:
     * - the column index is out of bounds
     * - column value is not defined
     * <p>
     * If the column contains an explicit empty string, then an empty string is returned.
     * <p>
     * Example.
     * <p>
     * col0,col1,col2
     * a,,""
     * <p>
     * asString(0) == "a"
     * asString(1) == null
     * asString(2) == ""
     * asString(3) == null
     * asString(-1) == null
     *
     * @param columnIndex
     * @return value for the requested column or null
     */
    @Nullable
    public String asString(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnValues.length) {
            return null;
        }
        return columnValues[columnIndex];
    }
}
