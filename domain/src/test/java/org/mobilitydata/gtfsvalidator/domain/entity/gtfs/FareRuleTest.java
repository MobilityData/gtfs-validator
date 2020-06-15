/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;

class FareRuleTest {

    @Test
    void createFareRuleWithNullFareIdShouldGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        // parameter of method .fareId() is annotated as not null. Warning is disabled here for the purpose of this test
        //noinspection ConstantConditions
        underTest.fareId(null);
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof List);
        // Cast check disabled since this test is designed so that method getData returns a list of notices.
        //noinspection unchecked to avoid lint
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_rules.txt", notice.getFilename());
        assertEquals("fare_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareRuleWithValidNonNullValuesShouldNotGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        underTest.fareId("fare id")
                .routeId("route id")
                .originId("origin id")
                .destinationId("destination id")
                .containsId("contains id");
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof FareRule);
    }

    @Test
    void createFareRuleWithValidNullValuesShouldNotGenerateNotice() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        underTest.fareId("fare id");
        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof FareRule);
    }

    @Test
    void getFareRuleMappingKeyShouldReturnStringOfConcatenatedFieldValues() {
        final FareRule.FareRuleBuilder underTest = new FareRule.FareRuleBuilder();
        final FareRule fareRule = (FareRule) underTest.fareId("fare id")
                .routeId("route id")
                .originId("origin id")
                .destinationId("destination id")
                .containsId("contains id")
                .build()
                .getData();

        assertEquals("fare id" + "route id" + "origin id" + "destination id" + "contains id",
                fareRule.getFareRuleMappingKey());
    }
}