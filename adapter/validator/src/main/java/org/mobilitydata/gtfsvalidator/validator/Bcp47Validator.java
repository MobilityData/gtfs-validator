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

package org.mobilitydata.gtfsvalidator.validator;

import java.util.IllformedLocaleException;
import java.util.Locale;

/**
 * Provides methods to validate an IETF BCP 47 language code.
 * Language codes used in GTFS feeds should be in IETF BCP 47 format.
 */
public class Bcp47Validator {
    private final Locale.Builder langTagValidator;

    /**
     * Bcp47Validation constructor.
     * Creates a new Locale.Builder, which will be used to validate a language tag.
     */
    public Bcp47Validator() {
        this.langTagValidator = new Locale.Builder();
    }

    /**
     * Validates a language tag according to the IETF BCP 47
     *
     * @param languageTag the language tag to validate
     * @return true if the language tag is valid, false if not.
     */
    public Boolean isValid(String languageTag) {
        try {
            langTagValidator.setLanguageTag(languageTag).build();
            return true;
        } catch (IllformedLocaleException e) {
            return false;
        }
    }
}
