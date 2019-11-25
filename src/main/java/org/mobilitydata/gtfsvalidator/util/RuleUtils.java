package org.mobilitydata.gtfsvalidator.util;

/* Original work Copyright (C) 2017 University of South Florida.
 * Modified work Copyright (c) 2019. MobilityData IO. All rights reserved
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

import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;
import org.mobilitydata.gtfsvalidator.model.ValidationRule;

import java.util.List;

/**
 * Utilities related to rules
 */
public class RuleUtils {

    /**
     * Adds occurrence for rule
     *
     * @param rule             rule to add occurrence for
     * @param occurrencePrefix prefix to use for the OccurrenceModel constructor
     * @param list             list to add occurrence for the rule to
     * @param log              logger to use to output occurrence info
     */
    public static void addOccurrence(ValidationRule rule, String occurrencePrefix, List<OccurrenceModel> list/*, org.slf4j.Logger log*/) {
        OccurrenceModel om = new OccurrenceModel(occurrencePrefix, rule);
        list.add(om);
        //log.debug(om.getPrefix() + " " + rule.getOccurrenceSuffix());
    }
}
