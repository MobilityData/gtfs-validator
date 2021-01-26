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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.DEFAULT_FROM_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.DEFAULT_MIN_TRANSFER_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.DEFAULT_TO_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.DEFAULT_TRANSFER_TYPE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsTransferTest {
  @Test
  public void shouldReturnFieldValues() {
    Builder builder = new Builder();
    GtfsTransfer underTest =
        builder
            .setFromStopId("from stop id")
            .setToStopId("to stop id")
            .setTransferType(2)
            .setMinTransferTime(20)
            .build();

    assertThat(underTest.fromStopId()).isEqualTo("from stop id");
    assertThat(underTest.toStopId()).isEqualTo("to stop id");
    assertThat(underTest.transferType()).isEqualTo(GtfsTransferType.MINIMUM_TIME);
    assertThat(underTest.minTransferTime()).isEqualTo(20);

    assertThat(underTest.hasFromStopId()).isTrue();
    assertThat(underTest.hasToStopId()).isTrue();
    assertThat(underTest.hasTransferType()).isTrue();
    assertThat(underTest.hasMinTransferTime()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    Builder builder = new Builder();
    GtfsTransfer underTest =
        builder
            .setFromStopId(null)
            .setToStopId(null)
            .setTransferType(null)
            .setMinTransferTime(null)
            .build();

    assertThat(underTest.fromStopId()).isEqualTo(DEFAULT_FROM_STOP_ID);
    assertThat(underTest.toStopId()).isEqualTo(DEFAULT_TO_STOP_ID);
    assertThat(underTest.transferType())
        .isEqualTo(GtfsTransferType.forNumber(DEFAULT_TRANSFER_TYPE));
    assertThat(underTest.minTransferTime()).isEqualTo(DEFAULT_MIN_TRANSFER_TIME);

    assertThat(underTest.hasFromStopId()).isFalse();
    assertThat(underTest.hasToStopId()).isFalse();
    assertThat(underTest.hasTransferType()).isFalse();
    assertThat(underTest.hasMinTransferTime()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    Builder builder = new Builder();
    builder
        .setFromStopId("from stop id")
        .setToStopId("to stop id")
        .setTransferType(2)
        .setMinTransferTime(20);
    builder.clear();

    GtfsTransfer underTest = builder.build();

    assertThat(underTest.fromStopId()).isEqualTo(DEFAULT_FROM_STOP_ID);
    assertThat(underTest.toStopId()).isEqualTo(DEFAULT_TO_STOP_ID);
    assertThat(underTest.transferType())
        .isEqualTo(GtfsTransferType.forNumber(DEFAULT_TRANSFER_TYPE));
    assertThat(underTest.minTransferTime()).isEqualTo(DEFAULT_MIN_TRANSFER_TIME);

    assertThat(underTest.hasFromStopId()).isFalse();
    assertThat(underTest.hasToStopId()).isFalse();
    assertThat(underTest.hasTransferType()).isFalse();
    assertThat(underTest.hasMinTransferTime()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    Builder builder = new Builder();
    GtfsTransfer underTest = builder.build();

    assertThat(underTest.fromStopId()).isNull();
    assertThat(underTest.toStopId()).isNull();
    assertThat(underTest.transferType())
        .isEqualTo(GtfsTransferType.forNumber(DEFAULT_TRANSFER_TYPE));
    assertThat(underTest.minTransferTime()).isEqualTo(DEFAULT_MIN_TRANSFER_TIME);

    assertThat(underTest.hasFromStopId()).isFalse();
    assertThat(underTest.hasToStopId()).isFalse();
    assertThat(underTest.hasTransferType()).isFalse();
    assertThat(underTest.hasMinTransferTime()).isFalse();
  }
}
