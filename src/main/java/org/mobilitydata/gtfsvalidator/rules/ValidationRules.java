package org.mobilitydata.gtfsvalidator.rules;

/*
 * Original work Copyright (C) 2011-2017 Nipuna Gunathilake, University of South Florida.
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

import org.mobilitydata.gtfsvalidator.model.ValidationRule;

public class ValidationRules {

    /**
     * Warnings
     */
    public static final ValidationRule W001 = new ValidationRule("W001", "WARNING", "Non printable ASCII",
            "Using only printable ASCII characters is recommended",
            "contains non printable ASCII characters");

    /**
     * Errors
     */
    public static final ValidationRule E001 = new ValidationRule("E001", "ERROR", "File not found",
            "All required files must be provided",
            "could not be opened");

    public static final ValidationRule E002 = new ValidationRule("E002", "ERROR", "Invalid field value",
            "A field can't be NULL",
            "is NULL");

    public static final ValidationRule E003 = new ValidationRule("E003", "ERROR", "Invalid field value",
            "A field of type float can't be parsed",
            "is not parsable as a float");

    public static final ValidationRule E004 = new ValidationRule("E004", "ERROR", "Invalid field value",
            "A field of type non negative float can't be negative",
            "is negative");

    public static final ValidationRule E005 = new ValidationRule("E005", "ERROR", "Invalid field value",
            "A field of type integer can't be parsed",
            "is not parsable as an integer");

    public static final ValidationRule E006 = new ValidationRule("E006", "ERROR", "Invalid field value",
            "A field of type non negative integer can't be negative",
            "is negative");
}
