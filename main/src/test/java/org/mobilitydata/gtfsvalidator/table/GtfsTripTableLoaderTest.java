/*
 * Copyright 2026 MobilityData
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

@RunWith(JUnit4.class)
public class GtfsTripTableLoaderTest {
  private LoadingHelper helper;

  @Before
  public void setup() {
    helper = new LoadingHelper();
  }

  @Test
  public void emptyTripHeadsignGeneratesRecommendedFieldNotice() throws ValidatorLoaderException {
    helper.load(
        new GtfsTripTableDescriptor(),
        "route_id,service_id,trip_id,trip_headsign",
        "route1,service1,trip1,");

    assertThat(helper.getValidationNotices())
        .containsExactly(
            new MissingRecommendedFieldNotice(
                GtfsTrip.FILENAME, 2, GtfsTrip.TRIP_HEADSIGN_FIELD_NAME));
  }

  @Test
  public void populatedTripHeadsignGeneratesNoMissingRecommendedFieldNotice()
      throws ValidatorLoaderException {
    helper.load(
        new GtfsTripTableDescriptor(),
        "route_id,service_id,trip_id,trip_headsign",
        "route1,service1,trip1,Downtown");

    assertThat(helper.getValidationNotices()).isEmpty();
  }
}
