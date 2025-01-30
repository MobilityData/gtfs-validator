/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates GtfsFareLegJoinRule entities
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link ForeignKeyViolationNotice}
 *   <li>{@link MissingRequiredFieldNotice}
 * </ul>
 */
@GtfsValidator
public class FareLegJoinRuleValidator extends FileValidator {
  GtfsNetworkTableContainer networkTable;
  GtfsRouteTableContainer routeTable;
  GtfsFareLegJoinRuleTableContainer fareLegJoinRuleTable;

  @Inject
  public FareLegJoinRuleValidator(
      GtfsNetworkTableContainer networkTable,
      GtfsRouteTableContainer routeTable,
      GtfsFareLegJoinRuleTableContainer fareLegJoinRuleTable) {
    this.networkTable = networkTable;
    this.routeTable = routeTable;
    this.fareLegJoinRuleTable = fareLegJoinRuleTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsFareLegJoinRule entity : fareLegJoinRuleTable.getEntities()) {
      validate(entity, noticeContainer);
    }
  }

  public void validate(GtfsFareLegJoinRule entity, NoticeContainer noticeContainer) {
    // Validate foreign key reference - from_network_id references a network or route
    if (!networkTable.byNetworkId(entity.fromNetworkId()).isPresent()
        && routeTable.byNetworkId(entity.fromNetworkId()).isEmpty()) {
      noticeContainer.addValidationNotice(
          new ForeignKeyViolationNotice(
              GtfsFareLegJoinRule.FILENAME,
              GtfsFareLegJoinRule.FROM_NETWORK_ID_FIELD_NAME,
              GtfsRoute.FILENAME + " or " + GtfsNetwork.FILENAME,
              GtfsNetwork.NETWORK_ID_FIELD_NAME,
              entity.fromNetworkId(),
              entity.csvRowNumber()));
    }

    // Validate foreign key reference - to_network_id references a network or route
    if (!networkTable.byNetworkId(entity.toNetworkId()).isPresent()
        && routeTable.byNetworkId(entity.toNetworkId()).isEmpty()) {
      noticeContainer.addValidationNotice(
          new ForeignKeyViolationNotice(
              GtfsFareLegJoinRule.FILENAME,
              GtfsFareLegJoinRule.TO_NETWORK_ID_FIELD_NAME,
              GtfsRoute.FILENAME + " or " + GtfsNetwork.FILENAME,
              GtfsNetwork.NETWORK_ID_FIELD_NAME,
              entity.toNetworkId(),
              entity.csvRowNumber()));
    }

    // Validate conditionally required fields - from_stop_id and to_stop_id
    if (entity.hasFromStopId() && !entity.hasToStopId()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsFareLegJoinRule.FILENAME,
              entity.csvRowNumber(),
              GtfsFareLegJoinRule.TO_STOP_ID_FIELD_NAME));
    } else if (entity.hasToStopId() && !entity.hasFromStopId()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsFareLegJoinRule.FILENAME,
              entity.csvRowNumber(),
              GtfsFareLegJoinRule.FROM_STOP_ID_FIELD_NAME));
    }
  }
}
