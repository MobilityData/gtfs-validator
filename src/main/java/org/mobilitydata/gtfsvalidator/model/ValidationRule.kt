package org.mobilitydata.gtfsvalidator.model;

/*
 * Original work Copyright (C) 2011-2017 Nipuna Gunathilake, University of South Florida.
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

import java.io.Serializable;

public class ValidationRule implements Serializable {

    private String errorId;
    private String severity;
    private String title;
    private String errorDescription;
    private String occurrenceSuffix;

    public ValidationRule() {
    }

    public ValidationRule(String errorId, String severity, String title, String errorDescription, String occurrenceSuffix) {
        this.errorId = errorId;
        this.severity = severity;
        this.title = title;
        this.errorDescription = errorDescription;
        this.occurrenceSuffix = occurrenceSuffix;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String errorType) {
        this.severity = errorType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOccurrenceSuffix() {
        return this.occurrenceSuffix;
    }

    public void setOccurrenceSuffix(String occurrenceSuffix) {
        this.occurrenceSuffix = occurrenceSuffix;
    }
}
