/*
 * Copyright 2024 MobilityData
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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;


/**
 * TODO
 *
 * <p>Generated notice: TODO
 */
@GtfsValidator
public class PickupDropOffWindowValidator
    extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    // TODO: Implement this validator
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    // No point in validating if there is no start_pickup_drop_off_window column
    // and no end_pickup_drop_off_window column
    return header.hasColumn(GtfsStopTime.START_PICKUP_DROP_OFF_WINDOW_FIELD_NAME)
        || header.hasColumn(GtfsStopTime.END_PICKUP_DROP_OFF_WINDOW_FIELD_NAME);
  }
}
