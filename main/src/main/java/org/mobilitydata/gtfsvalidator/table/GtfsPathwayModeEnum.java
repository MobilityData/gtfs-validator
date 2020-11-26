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

package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.GtfsEnumValue;

@GtfsEnumValue(name = "WALKWAY", value = 1)
@GtfsEnumValue(name = "STAIRS", value = 2)
@GtfsEnumValue(name = "MOVING_SIDEWALK", value = 3)
@GtfsEnumValue(name = "ESCALATOR", value = 4)
@GtfsEnumValue(name = "ELEVATOR", value = 5)
@GtfsEnumValue(name = "FARE_GATE", value = 6)
@GtfsEnumValue(name = "EXIT_GATE", value = 7)
public interface GtfsPathwayModeEnum {
}
