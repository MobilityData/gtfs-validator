/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * Rows from `fare_rules.txt` must be unique based on `fare_rules.route_id`, `fare_rules.origin_id`,
 * `fare_rules.contains_id` and `fare_rules.destination_id`.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class DuplicateFareRuleZoneIdFieldsNotice extends ValidationNotice {
  public DuplicateFareRuleZoneIdFieldsNotice(
      long csvRowNumber, String fareId, long previousCsvRowNumber, String previousFareId) {
    super(
        ImmutableMap.of(
            "csvRowNumber", csvRowNumber,
            "fareId", fareId,
            "previousCsvRowNumber", previousCsvRowNumber,
            "previousFareId", previousFareId),
        SeverityLevel.ERROR);
  }

  @Override
  public String getCode() {
    return "duplicate_fare_rule_zone_id_fields";
  }
}
