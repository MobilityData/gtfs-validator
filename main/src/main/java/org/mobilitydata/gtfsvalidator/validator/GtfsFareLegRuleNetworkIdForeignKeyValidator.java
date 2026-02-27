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

package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that network_id field in "fare_leg_rules.txt" references a valid network_id in
 * "routes.txt" or "networks.txt".
 *
 * <p>Generated notice: {@link ForeignKeyViolationNotice}.
 */
@GtfsValidator
public class GtfsFareLegRuleNetworkIdForeignKeyValidator extends FileValidator {
  private final GtfsRouteTableContainer routeParentContainer;
  private final GtfsNetworkTableContainer networkParentContainer;

  private final GtfsFareLegRuleTableContainer fareLegRuleChildContainer;

  @Inject
  GtfsFareLegRuleNetworkIdForeignKeyValidator(
      GtfsFareLegRuleTableContainer fareLegRuleChildContainer,
      GtfsRouteTableContainer routeParentContainer,
      GtfsNetworkTableContainer networkParentContainer) {
    this.fareLegRuleChildContainer = fareLegRuleChildContainer;
    this.routeParentContainer = routeParentContainer;
    this.networkParentContainer = networkParentContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsFareLegRule fareLegRule : fareLegRuleChildContainer.getEntities()) {
      String foreignKey = fareLegRule.networkId();
      if (foreignKey.isEmpty()) {
        continue;
      }
      if (!hasReferencedKey(foreignKey, routeParentContainer, networkParentContainer)) {
        noticeContainer.addValidationNotice(
            new ForeignKeyViolationNotice(
                GtfsFareLegRule.FILENAME,
                GtfsFareLegRule.NETWORK_ID_FIELD_NAME,
                GtfsRoute.FILENAME + " or " + GtfsNetwork.FILENAME,
                GtfsFareLegRule.NETWORK_ID_FIELD_NAME,
                foreignKey,
                fareLegRule.csvRowNumber()));
      }
    }
  }

  private boolean hasReferencedKey(
      String foreignKey,
      GtfsRouteTableContainer routeContainer,
      GtfsNetworkTableContainer networkContainer) {
    return !(routeContainer.byNetworkId(foreignKey).isEmpty()
        && networkContainer.byNetworkId(foreignKey).isEmpty());
  }
}
