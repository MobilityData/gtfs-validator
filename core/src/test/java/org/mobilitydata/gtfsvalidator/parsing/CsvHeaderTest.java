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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CsvHeaderTest {
  @Test
  public void nonEmpty() {
    CsvHeader header = new CsvHeader(new String[] {"stop_id", "stop_name", "stop_code"});
    assertThat(header.getColumnCount()).isEqualTo(3);
    assertThat(header.getColumnIndex("stop_id")).isEqualTo(0);
    assertThat(header.getColumnIndex("stop_name")).isEqualTo(1);
    assertThat(header.getColumnIndex("stop_code")).isEqualTo(2);
    assertThat(header.getColumnIndex("agency_id")).isEqualTo(-1);
    assertThat(header.hasColumn("stop_id")).isTrue();
    assertThat(header.hasColumn("agency_id")).isFalse();
    assertThat(header.getColumnName(1)).isEqualTo("stop_name");
    assertThat(header.getColumnNames())
        .asList()
        .containsExactly("stop_id", "stop_name", "stop_code");
  }

  @Test
  public void empty() {
    CsvHeader header = new CsvHeader(new String[] {});
    assertThat(header.getColumnCount()).isEqualTo(0);
    assertThat(header.getColumnIndex("stop_id")).isEqualTo(-1);
    assertThat(header.hasColumn("agency_id")).isFalse();
    assertThat(header.getColumnNames()).isEmpty();
  }

  @Test
  public void nullColumn() {
    CsvHeader header = new CsvHeader(new String[] {null, ""});
    assertThat(header.getColumnCount()).isEqualTo(2);
    assertThat(header.getColumnIndex("")).isEqualTo(-1);
    assertThat(header.getColumnNames()).asList().containsExactly("", "");
  }
}
