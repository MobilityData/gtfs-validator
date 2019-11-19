/*
 * Original work Copyright (C) 2011 Nipuna Gunathilake.
 * Modified work Copyright (C) 2019 MobilityData IO
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.model;

import java.io.Serializable;

public class OccurrenceModel implements Serializable {

    public OccurrenceModel(String prefix, ValidationRule rule) {
        this.prefix = prefix;
        this.rule = rule;
    }

    public OccurrenceModel() {
    }

    private String prefix;
    private ValidationRule rule;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ValidationRule getRule() {
        return rule;
    }

    public void setRule(ValidationRule rule) {
        this.rule = rule;
    }

}
