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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareLegRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareLegRuleTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsNetwork;
import org.mobilitydata.gtfsvalidator.table.GtfsNetworkTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

public class GtfsFareLegRuleNetworkIdForeignKeyValidatorTest {

  public static GtfsFareLegRule createFareLegRule(String networkId) {
    return new GtfsFareLegRule.Builder().setCsvRowNumber(1).setNetworkId(networkId).build();
  }

  private static GtfsRoute createRoute(String networkId) {
    return new GtfsRoute.Builder()
        .setCsvRowNumber(1)
        .setRouteId("testRoute")
        .setRouteType(3) // Bus, but could be other type
        .setNetworkId(networkId)
        .build();
  }

  private static GtfsNetwork createNetwork(String networkId) {
    return new GtfsNetwork.Builder().setCsvRowNumber(1).setNetworkId(networkId).build();
  }

  private static List<ValidationNotice> generateNotices(
      GtfsFareLegRule fareLegRule, GtfsRoute route, GtfsNetwork network) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new GtfsFareLegRuleNetworkIdForeignKeyValidator(
            GtfsFareLegRuleTableContainer.forEntities(
                ImmutableList.of(fareLegRule), noticeContainer),
            GtfsRouteTableContainer.forEntities(ImmutableList.of(route), noticeContainer),
            GtfsNetworkTableContainer.forEntities(ImmutableList.of(network), noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void networkIdNotInRouteOrNetworkShouldGenerateNotice() {
    List<?> notices =
        generateNotices(
            createFareLegRule("testNetworkId"),
            createRoute("otherNetworkId"),
            createNetwork("otherNetworkId"));
    assertThat(notices).isNotEmpty();
  }

  @Test
  public void networkIdInRouteButNotInNetworkShouldNotGenerateNotice() {
    List<?> notices =
        generateNotices(
            createFareLegRule("testNetworkId"),
            createRoute("testNetworkId"),
            createNetwork("otherNetworkId"));
    assertThat(notices).isEmpty();
  }

  @Test
  public void networkIdNotInRouteButInNetworkShouldNotGenerateNotice() {
    List<?> notices =
        generateNotices(
            createFareLegRule("testNetworkId"),
            createRoute("otherNetworkId"),
            createNetwork("testNetworkId"));
    assertThat(notices).isEmpty();
  }

  @Test
  public void networkIdInBothRouteAndNetworkShouldNotGenerateNotice() {
    List<?> notices =
        generateNotices(
            createFareLegRule("testNetworkId"),
            createRoute("testNetworkId"),
            createNetwork("testNetworkId"));
    assertThat(notices).isEmpty();
  }
}
