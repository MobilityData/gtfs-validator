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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth8.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;
import org.mobilitydata.gtfsvalidator.testgtfs.GtfsStopTableContainer;

public class GtfsFeedContainerTest {

  @Test
  public void getTableForFilename() {
    GtfsStopTableContainer stopTable =
        new GtfsStopTableContainer(TableStatus.EMPTY_FILE, CsvHeader.EMPTY);
    GtfsFeedContainer feedContainer = new GtfsFeedContainer(ImmutableList.of(stopTable));

    assertThat(feedContainer.getTableForFilename("stops.txt")).hasValue(stopTable);
    assertThat(feedContainer.getTableForFilename("STOPS.TXT")).hasValue(stopTable);
    assertThat(feedContainer.getTableForFilename("STOPS")).isEmpty();
  }
}
