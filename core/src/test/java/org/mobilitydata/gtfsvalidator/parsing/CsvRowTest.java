/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import java.io.Reader;
import java.io.StringReader;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class CsvRowTest {

    @Test
    public void shouldReturnRowNumber() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{});
        assertThat(underTest.getRowNumber()).isEqualTo(4);
    }

    @Test
    public void shouldReturnColumnIndex() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{});
        assertThat(underTest.getColumnIndex("stop_name")).isEqualTo(1);
    }

    @Test
    public void shouldReturnColumnCount() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{"s2","Second stop","1.31"});
        assertThat(underTest.getColumnCount()).isEqualTo(3);
    }

    @Test
    public void shouldReturnColumnName() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{});
        assertThat(underTest.getColumnName(2)).isEqualTo("stop_lat");
    }

    @Test
    public void shouldReturnFileName() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{});
        assertThat(underTest.getFileName()).isEqualTo("stops.txt");
    }

    @Test
    public void shouldReturnColumnValuesAsString() {
        Reader reader = new StringReader("stop_id,stop_name,stop_lat" + System.lineSeparator() +
                "s1,First stop,3.21" + System.lineSeparator() + "s2,Second stop,1.31" + System.lineSeparator());
        CsvFile csvFile = new CsvFile(reader, "stops.txt");

        CsvRow underTest = new CsvRow(csvFile, 4, new String[]{"s2","Second stop","1.31"});
        assertThat(underTest.asString(0)).isEqualTo("s2");
        assertThat(underTest.asString(1)).isEqualTo("Second stop");
        assertThat(underTest.asString(2)).isEqualTo("1.31");
    }
}
