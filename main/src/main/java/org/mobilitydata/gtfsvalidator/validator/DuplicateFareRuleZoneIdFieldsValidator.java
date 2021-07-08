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

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.NoticeExport;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;

/**
 * Validates: unique combination of "fare_rules.route_id", "fare_rules.origin_id",
 * "fare_rules.contains_id" and "fare_rules.destination_id" fields in GTFS file "fare_rules.txt"
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link DuplicateFareRuleZoneIdFieldsNotice}
 * </ul>
 */
@GtfsValidator
public class DuplicateFareRuleZoneIdFieldsValidator extends FileValidator {
  private static final HashFunction HASH_FUNCTION = Hashing.farmHashFingerprint64();
  private final GtfsFareRuleTableContainer fareRuleTable;

  @Inject
  DuplicateFareRuleZoneIdFieldsValidator(GtfsFareRuleTableContainer fareRuleTable) {
    this.fareRuleTable = fareRuleTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Long, GtfsFareRule> fareRuleByZoneIdFieldsCombination =
        new HashMap<>(fareRuleTable.entityCount());
    fareRuleTable
        .getEntities()
        .forEach(
            fareRule -> {
              GtfsFareRule otherFareRule =
                  fareRuleByZoneIdFieldsCombination.putIfAbsent(getHash(fareRule), fareRule);
              if (otherFareRule != null) {
                noticeContainer.addValidationNotice(
                    new DuplicateFareRuleZoneIdFieldsNotice(
                        fareRule.csvRowNumber(),
                        fareRule.fareId(),
                        otherFareRule.csvRowNumber(),
                        otherFareRule.fareId()));
              }
            });
  }

  /**
   * Returns the hashcode associated to the combination of "fare_rules.origin_id",
   * "fare_rules.contains_id" and "fare_rules.destination_id".
   *
   * @param fareRule the {@code GtfsFareRule} to generate the hash from
   * @return the hashcode associated to the combination of this {@code GtfsFareRule} "route_id",
   *     "origin_id", "contains_id" and "destination_id".
   */
  private static long getHash(GtfsFareRule fareRule) {
    return HASH_FUNCTION
        .newHasher()
        .putUnencodedChars(fareRule.routeId())
        .putChar('\0')
        .putUnencodedChars(fareRule.originId())
        .putChar('\0')
        .putUnencodedChars(fareRule.containsId())
        .putChar('\0')
        .putUnencodedChars(fareRule.destinationId())
        .hash()
        .asLong();
  }

  /**
   * Rows from "fare_rules.txt" must be unique based on "fare_rules.route_id",
   * "fare_rules.origin_id", "fare_rules.contains_id" and "fare_rules.destination_id".
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class DuplicateFareRuleZoneIdFieldsNotice extends ValidationNotice {

    @NoticeExport
    DuplicateFareRuleZoneIdFieldsNotice(
        long csvRowNumber, String fareId, long previousCsvRowNumber, String previousFareId) {
      super(
          ImmutableMap.of(
              "csvRowNumber", csvRowNumber,
              "fareId", fareId,
              "previousCsvRowNumber", previousCsvRowNumber,
              "previousFareId", previousFareId),
          SeverityLevel.ERROR);
    }
  }
}
