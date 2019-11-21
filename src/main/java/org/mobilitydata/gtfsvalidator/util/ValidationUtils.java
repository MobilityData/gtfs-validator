package org.mobilitydata.gtfsvalidator.util;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
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

import java.util.List;

import static org.mobilitydata.gtfsvalidator.rules.ValidationRules.*;

public class ValidationUtils {

    public static Float parseAndValidateFloat(String fieldName,
                                              String rawValue,
                                              boolean canBeNullOrEmpty,
                                              boolean canBeNegative,
                                              List<OccurrenceModel> outList) {

        if (rawValue == null || rawValue.isEmpty()) {
            if (!canBeNullOrEmpty) {
                RuleUtils.addOccurrence(E002, fieldName, outList);
            }
            return null;
        }

        try {
            float value = Float.parseFloat(rawValue);

            if (!canBeNegative && value < 0) {
                RuleUtils.addOccurrence(E004, fieldName, outList);
                return null;
            }

            return value;

        } catch (NumberFormatException e) {
            outList.add(new OccurrenceModel(fieldName, E003));
            return null;
        }
    }

    public static Integer parseAndValidateInteger(String fieldName,
                                                  String rawValue,
                                                  boolean canBeNullOrEmpty,
                                                  boolean canBeNegative,
                                                  List<OccurrenceModel> outList) {

        if (rawValue == null || rawValue.isEmpty()) {
            if (!canBeNullOrEmpty) {
                RuleUtils.addOccurrence(E002, fieldName, outList);
            }
            return null;
        }

        try {
            int value = Integer.parseInt(rawValue);

            if (!canBeNegative && value < 0) {
                RuleUtils.addOccurrence(E006, fieldName, outList);
                return null;
            }

            return value;

        } catch (NumberFormatException e) {
            RuleUtils.addOccurrence(E005, fieldName, outList);
            return null;
        }
    }

    public static String validateString(String fieldName,
                                        String rawValue,
                                        boolean canBeNullOrEmpty,
                                        boolean onlyPrintableAscii,
                                        List<OccurrenceModel> outList) {

        if (rawValue == null || rawValue.isEmpty()) {
            if (!canBeNullOrEmpty) {
                RuleUtils.addOccurrence(E002, fieldName, outList);
            }
            return null;
        }

        if (onlyPrintableAscii) {
            int charCount = rawValue.length();
            for (int i = 0; i < charCount; ++i) {
                if (!isPrintableAscii(rawValue.charAt(i))) {
                    RuleUtils.addOccurrence(W001, fieldName, outList);
                    break;
                }
            }
        }

        return rawValue;
    }

    private static boolean isPrintableAscii(char ch) {
        return ch >= 32 && ch < 127;
    }
}
