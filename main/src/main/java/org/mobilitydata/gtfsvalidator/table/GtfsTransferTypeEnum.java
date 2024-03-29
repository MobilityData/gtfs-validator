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

@GtfsEnumValue(name = "RECOMMENDED", value = 0)
@GtfsEnumValue(name = "TIMED", value = 1)
@GtfsEnumValue(name = "MINIMUM_TIME", value = 2)
@GtfsEnumValue(name = "IMPOSSIBLE", value = 3)
@GtfsEnumValue(name = "IN_SEAT_TRANSFER_ALLOWED", value = 4)
@GtfsEnumValue(name = "IN_SEAT_TRANSFER_NOT_ALLOWED", value = 5)
public interface GtfsTransferTypeEnum {}
