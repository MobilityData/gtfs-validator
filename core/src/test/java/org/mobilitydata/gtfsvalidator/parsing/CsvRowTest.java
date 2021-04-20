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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CsvRowTest {
  @Test
  public void testCsvRowAllMethods() {
    CsvRow underTest = new CsvRow(3, new String[] {"stop_id", "stop_code", ""});

    assertThat(underTest.getRowNumber()).isEqualTo(3);
    assertThat(underTest.asString(0)).isEqualTo("stop_id");
    assertThat(underTest.asString(1)).isEqualTo("stop_code");
    assertThat(underTest.asString(2)).isNull();
  }
}
