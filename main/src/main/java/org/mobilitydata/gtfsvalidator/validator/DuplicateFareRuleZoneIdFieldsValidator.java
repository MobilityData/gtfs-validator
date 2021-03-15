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

package org.mobilitydata.gtfsvalidator.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.DuplicateFareRuleZoneIdFieldsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;

/**
 * Validates: unique combination of `fare_rules.route_id`, `fare_rules.origin_id`,
 * `fare_rules.contains_id` and `fare_rules.destination_id` fields in GTFS file `fare_rules.txt`
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link DuplicateFareRuleZoneIdFieldsNotice}
 * </ul>
 */
@GtfsValidator
public class DuplicateFareRuleZoneIdFieldsValidator extends FileValidator {
  @Inject GtfsFareRuleTableContainer fareRuleTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Integer, GtfsFareRule> fareRuleByZoneIdFieldsCombination =
        new HashMap<>(fareRuleTable.entityCount());
    fareRuleTable
        .getEntities()
        .forEach(
            fareRule -> {
              int fieldsCombinationHash = getZoneFieldsCombinationHash(fareRule);
              if (fareRuleByZoneIdFieldsCombination.containsKey(fieldsCombinationHash)) {
                noticeContainer.addValidationNotice(
                    new DuplicateFareRuleZoneIdFieldsNotice(
                        fareRule.csvRowNumber(),
                        fareRule.fareId(),
                        fareRuleByZoneIdFieldsCombination.get(fieldsCombinationHash).csvRowNumber(),
                        fareRuleByZoneIdFieldsCombination.get(fieldsCombinationHash).fareId()));
              } else {
                fareRuleByZoneIdFieldsCombination.put(fieldsCombinationHash, fareRule);
              }
            });
  }

  /**
   * Returns the hashcode associated to the combination of `fare_rules.route_id`,
   * `fare_rules.contains_id` and `fare_rules.destination_id`.
   *
   * @param fareRule
   * @return the hashcode associated to the combination of this {@code GtfsFareRule} `route_id`,
   *     `contains_id` and `destination_id`.
   */
  private int getZoneFieldsCombinationHash(GtfsFareRule fareRule) {
    return Objects.hash(fareRule.originId(), fareRule.containsId(), fareRule.destinationId());
  }
}
