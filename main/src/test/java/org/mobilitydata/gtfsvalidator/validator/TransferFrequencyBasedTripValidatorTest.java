/*
 * Copyright 2023 Google LLC
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

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.validator.TransferFrequencyBasedTripValidator.TransferForFrequencyBasedTripNotice;

@RunWith(JUnit4.class)
public class TransferFrequencyBasedTripValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsTransfer> transfers, List<GtfsFrequency> frequencies) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TransferFrequencyBasedTripValidator(
        GtfsTransferTableContainer.forEntities(transfers, noticeContainer),
        GtfsFrequencyTableContainer.forEntities(frequencies, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  static GtfsFrequency createFrequency(String tripId) {
    return new GtfsFrequency.Builder().setCsvRowNumber(tripId.hashCode()).setTripId(tripId).build();
  }

  @Test
  public void frequencyToFrequency_yieldsTwoNotices() {
    GtfsTransfer transfer = new GtfsTransfer.Builder()
                                .setCsvRowNumber(2)
                                .setFromTripId("trip1")
                                .setToTripId("trip2")
                                .build();
    ImmutableList<GtfsFrequency> frequencies =
        ImmutableList.of(createFrequency("trip1"), createFrequency("trip2"));
    assertThat(generateNotices(ImmutableList.of(transfer), frequencies))
        .containsExactly(
            new TransferForFrequencyBasedTripNotice(transfer, "from_trip_id", frequencies.get(0)),
            new TransferForFrequencyBasedTripNotice(transfer, "to_trip_id", frequencies.get(1)));
  }

  @Test
  public void nonFrequencyBased_yieldsNoNotices() {
    GtfsTransfer transfer = new GtfsTransfer.Builder()
                                .setCsvRowNumber(2)
                                .setFromTripId("trip1")
                                .setToTripId("trip2")
                                .build();
    assertThat(generateNotices(ImmutableList.of(transfer), ImmutableList.of())).isEmpty();
  }
}
