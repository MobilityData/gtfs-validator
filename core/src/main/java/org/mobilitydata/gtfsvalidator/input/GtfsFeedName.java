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

package org.mobilitydata.gtfsvalidator.input;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a name of a GTFS feed, such as "nl-openov".
 */
public class GtfsFeedName {

    private static final Pattern FEED_NAME_PATTERN = Pattern.compile("[a-z]{2}-[a-z0-9]+(-[a-z0-9]+)*");

    private static final Set<String> ISO_COUNTRIES = ImmutableSet.copyOf(Locale.getISOCountries());

    private static final String INVALID_FEED_NAME_MESSAGE = "Feed name must follow format `cc-abc-...', where cc is an ISO Alpha 2 country code";

    private final String countryFirstName;

    private GtfsFeedName(String countryFirstName) {
        this.countryFirstName = countryFirstName;
    }

    /**
     * Checks that the given string is a valid name for a GTFS feed that starts from ISO Alpha 2 country code.
     * <p>
     * Examples of valid names: "nl-openov", "au-sydney-buses".
     * Examples of invalid names: "openov-nl", "zz-buses".
     */
    public static boolean isValidCountryFirstFeedName(String s) {
        return FEED_NAME_PATTERN.matcher(s).matches() && isValidISOAlpha2(s.substring(0, 2));
    }

    /**
     * Checks that the given string is a valid ISO Alpha 2 country code (case insensitive).
     */
    public static boolean isValidISOAlpha2(String s) {
        return s.length() == 2 && ISO_COUNTRIES.contains(s.toUpperCase());
    }

    /**
     * Parses a string as a feed name in country-first format ("nl-openov"). If that fails, then tries to interpret it
     * as country-last format ("openov-nl").
     *
     * @param feedName GTFS feed name in country-first or country-last format
     * @return a valid GtfsFeedName object
     * @throws IllegalArgumentException
     */
    public static GtfsFeedName parseString(String feedName) throws IllegalArgumentException {
        String[] separated = feedName.split("-");
        if (separated.length < 2) {
            throw new IllegalArgumentException(INVALID_FEED_NAME_MESSAGE);
        }

        if (isValidCountryFirstFeedName(feedName)) {
            return new GtfsFeedName(feedName);
        } else if (isValidISOAlpha2(separated[separated.length - 1])) {
            List<String> reversedComponents = Arrays.asList(separated);
            Collections.reverse(reversedComponents);
            String reversedName = String.join("-", reversedComponents);
            if (isValidCountryFirstFeedName(reversedName)) {
                return new GtfsFeedName(reversedName);
            }
        }
        throw new IllegalArgumentException(INVALID_FEED_NAME_MESSAGE);
    }

    /**
     * Returns feed name in country-first format, e.g., "nl-openov".
     *
     * @return feed name in country-first format
     */
    public String getCountryFirstName() {
        return countryFirstName;
    }

    /**
     * Returns feed name in country-last format, e.g., "openov-nl".
     *
     * @return feed name in country-last format
     */
    public String getCountryLastName() {
        List<String> reversedComponents = Arrays.asList(countryFirstName.split("-"));
        Collections.reverse(reversedComponents);
        return String.join("-", reversedComponents);
    }

    /**
     * Returns the uppercase ISO Alpha country code component, e.g., "NL" or "AU".
     *
     * @return uppercase ISO Alpha country code
     */
    public String getISOAlpha2CountryCode() {
        return countryFirstName.substring(0, 2).toUpperCase();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof GtfsFeedName) {
            return this.countryFirstName.equals(((GtfsFeedName) other).countryFirstName);
        }
        return false;
    }
}
