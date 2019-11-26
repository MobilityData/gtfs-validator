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

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

import static org.mobilitydata.gtfsvalidator.rules.ValidationRules.*;

public class ValidationUtils {

    public static @Nullable
    Float parseAndValidateFloat(@NotNull String fieldName,
                                  @Nullable String rawValue,
                                  boolean canBeNullOrEmpty,
                                  boolean canBeNegative,
                                  @NotNull  List<OccurrenceModel> outList) {

        if (Strings.isNullOrEmpty(rawValue)) {
            if (!canBeNullOrEmpty) {
                RuleUtils.addOccurrence(E002, fieldName, outList);
            }
            return null;
        }

        try {
            float value = Float.parseFloat(rawValue);

            if (Float.isNaN(value)) {
                throw new NumberFormatException();
            }

            if (!canBeNegative && value < 0) {
                RuleUtils.addOccurrence(E004, fieldName, outList);
                return null;
            }

            return value;

        } catch (NumberFormatException e) {
            RuleUtils.addOccurrence(E003, fieldName, outList);
            return null;
        }
    }

    public static @Nullable
    Integer parseAndValidateInteger(@NotNull  String fieldName,
                                  @Nullable String rawValue,
                                  boolean canBeNullOrEmpty,
                                  boolean canBeNegative,
                                  @NotNull  List<OccurrenceModel> outList) {

        if (Strings.isNullOrEmpty(rawValue)) {
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

    public static @Nullable
    String validateString(@NotNull String fieldName,
                          @Nullable String rawValue,
                          boolean canBeNullOrEmpty,
                          boolean onlyPrintableAscii,
                          @NotNull List<OccurrenceModel> outList) {

        if (Strings.isNullOrEmpty(rawValue)) {
            if (!canBeNullOrEmpty) {
                RuleUtils.addOccurrence(E002, fieldName, outList);
                return null;
            }
        }

        if (rawValue != null && onlyPrintableAscii) {
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

    public static @Nullable
    String parseAndValidateColor(@NotNull String fieldName,
                         @Nullable String rawValue,
                         @NotNull List<OccurrenceModel> outList) {

        if (Strings.isNullOrEmpty(rawValue)) {
            return null;
        }

        if (COLOR_6_DIGITS_HEXADECIMAL_PATTERN.matcher(rawValue).matches()) {
            return rawValue;
        } else {
            RuleUtils.addOccurrence(E007, fieldName, outList);
            return null;
        }
    }

    private static boolean isPrintableAscii(char ch) {
        return ch >= 32 && ch < 127;
    }
    private static final Pattern COLOR_6_DIGITS_HEXADECIMAL_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
}
