/*
 * Copyright 2021 Google LLC
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

package org.mobilitydata.gtfsvalidator.util;

import static java.lang.Math.max;

/**
 * This class is intended to contain a collection of useful (static) mathematical functions,
 * properly coded (by consulting numerical recipes or another authoritative source first).
 */
public class MathUtil {

  /** Default error threshold for doubles. */
  public static final double DOUBLE_STD_ERR = 1e-9d * 32;

  /**
   * Tells if two floating point numbers are within a certain fraction of their magnitude or within
   * a certain absolute margin of error.
   */
  public static boolean withinFractionOrMargin(double x, double y, double fraction, double margin) {
    if (Double.isInfinite(x) || Double.isInfinite(y)) {
      return false;
    }
    double relativeMargin = fraction * max(Math.abs(x), Math.abs(y));
    double absDiff = x > y ? x - y : y - x;
    return absDiff <= max(margin, relativeMargin);
  }

  /** Same as {@link #withinFractionOrMargin} with {@link #DOUBLE_STD_ERR} thresholds. */
  public static boolean nearByFractionOrMargin(double x, double y) {
    return withinFractionOrMargin(x, y, DOUBLE_STD_ERR, DOUBLE_STD_ERR);
  }

  private MathUtil() {}
}
