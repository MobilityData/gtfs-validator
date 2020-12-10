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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class CsvFileTest {

    @Test
    public void emptyFile() throws IOException {
        Reader reader = new StringReader("");
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        assertThat(csvFile.isEmpty()).isEqualTo(true);
        assertThat(csvFile.getColumnCount()).isEqualTo(0);
        assertThat(csvFile.getFileName()).isEqualTo("stops.txt");
        assertThat(csvFile.iterator().hasNext()).isEqualTo(false);

        reader.close();
    }

    @Test
    public void headersOnlyFile() throws IOException {
        Reader reader = new StringReader("stop_id,stop_name");
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        assertThat(csvFile.isEmpty()).isEqualTo(false);
        assertThat(csvFile.getColumnCount()).isEqualTo(2);
        assertThat(csvFile.getColumnName(0)).isEqualTo("stop_id");
        assertThat(csvFile.getColumnIndex("stop_name")).isEqualTo(1);
        assertThat(csvFile.getFileName()).isEqualTo("stops.txt");
        assertThat(csvFile.iterator().hasNext()).isEqualTo(false);

        reader.close();
    }

    @Test
    public void fileWithEntities() throws IOException {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat\n" +
                "s1,First stop,3.21\n" + "s2,Second stop,1.31\n");
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        assertThat(csvFile.isEmpty()).isEqualTo(false);
        assertThat(csvFile.getColumnCount()).isEqualTo(3);
        assertThat(csvFile.getColumnName(0)).isEqualTo("stop_id");
        assertThat(csvFile.getColumnIndex("stop_name")).isEqualTo(1);
        assertThat(csvFile.getFileName()).isEqualTo("stops.txt");

        Iterator<CsvRow> iterator = csvFile.iterator();
        assertThat(iterator.hasNext()).isEqualTo(true);
        CsvRow row = iterator.next();
        assertThat(row.getFileName()).isEqualTo("stops.txt");
        assertThat(row.asString(0)).isEqualTo("s1");
        assertThat(row.asString(2)).isEqualTo("3.21");
        assertThat(row.asString(200)).isNull();

        assertThat(iterator.hasNext()).isEqualTo(true);
        row = iterator.next();
        assertThat(row.asString(0)).isEqualTo("s2");
        assertThat(row.asString(1)).isEqualTo("Second stop");
        assertThat(row.asString(-1)).isNull();

        assertThat(iterator.hasNext()).isEqualTo(false);

        reader.close();
    }

    @Test
    public void fileWithEntitiesWindows() throws IOException {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat\r\n" +
                "s1,First stop,3.21\r\n" + "s2,Second stop,1.31\r\n");
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        assertThat(csvFile.isEmpty()).isEqualTo(false);
        assertThat(csvFile.getColumnCount()).isEqualTo(3);
        assertThat(csvFile.getColumnName(0)).isEqualTo("stop_id");
        assertThat(csvFile.getColumnIndex("stop_name")).isEqualTo(1);
        assertThat(csvFile.getFileName()).isEqualTo("stops.txt");

        Iterator<CsvRow> iterator = csvFile.iterator();
        assertThat(iterator.hasNext()).isEqualTo(true);
        CsvRow row = iterator.next();
        assertThat(row.getFileName()).isEqualTo("stops.txt");
        assertThat(row.asString(0)).isEqualTo("s1");
        assertThat(row.asString(2)).isEqualTo("3.21");
        assertThat(row.asString(200)).isNull();

        assertThat(iterator.hasNext()).isEqualTo(true);
        row = iterator.next();
        assertThat(row.asString(0)).isEqualTo("s2");
        assertThat(row.asString(1)).isEqualTo("Second stop");
        assertThat(row.asString(-1)).isNull();

        assertThat(iterator.hasNext()).isEqualTo(false);

        reader.close();
    }

    @Test
    public void emptyValues() throws IOException {
        Reader reader = new StringReader("col0,col1,col2\n" +
                "a,,\"\",b\n");
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        Iterator<CsvRow> iterator = csvFile.iterator();
        assertThat(iterator.hasNext()).isEqualTo(true);
        CsvRow row = iterator.next();
        assertThat(row.asString(0)).isEqualTo("a");
        // There is no way to tell between a missing value and an explicit empty string "". Both are returned as null.
        assertThat(row.asString(1)).isNull();
        assertThat(row.asString(2)).isNull();
        assertThat(row.asString(3)).isEqualTo("b");
    }
}
