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

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CsvRowTest {

  private static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void testCsvRowAllMethods() throws IOException {
    InputStream inputStream =
        toInputStream(
            "stop_id,stop_name,stop_lat\n" + "s1,First stop,3.21\n" + "s2,Second stop,1.31\n");
    CsvFile csvFile = new CsvFile(inputStream, "stops.txt");
    inputStream.close();

    String[] columnValues = {
      "stop_id",
      "stop_code",
      "stop_name",
      "tts_stop_name",
      "stop_desc",
      "stop_lat",
      "stop_lon",
      "location_type",
      "parent_station",
      "stop_timezone",
      "wheelchair_boarding",
      "level_id",
      "platform_code"
    };

    CsvRow underTest = new CsvRow(csvFile, 3, columnValues);

    assertThat(underTest.getRowNumber()).isEqualTo(3);
    assertThat(underTest.getColumnIndex("stop_name")).isEqualTo(1);
    assertThat(underTest.getColumnIndex("location_type")).isEqualTo(-1);
    assertThat(underTest.getColumnCount()).isEqualTo(13);
    assertThat(underTest.getColumnName(0)).isEqualTo("stop_id");
    assertThat(underTest.getColumnName(1)).isEqualTo("stop_name");
    assertThat(underTest.getColumnName(2)).isEqualTo("stop_lat");
    assertThat(underTest.getFileName()).isEqualTo("stops.txt");
    assertThat(underTest.asString(0)).isEqualTo("stop_id");
    assertThat(underTest.asString(1)).isEqualTo("stop_code");
    assertThat(underTest.asString(2)).isEqualTo("stop_name");
  }
}
