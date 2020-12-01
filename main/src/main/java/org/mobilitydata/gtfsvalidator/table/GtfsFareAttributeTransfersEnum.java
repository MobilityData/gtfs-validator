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

// GTFS spec treats empty value as "unlimited" and 0 as "no transfer", so we add a magical -1 constant here for
// "unlimited".
@GtfsEnumValue(name = "UNLIMITED", value = -1)
@GtfsEnumValue(name = "NO_TRANSFER", value = 0)
@GtfsEnumValue(name = "ONE_TRANSFER", value = 1)
@GtfsEnumValue(name = "TWO_TRANSFERS", value = 2)
public interface GtfsFareAttributeTransfersEnum {
}

