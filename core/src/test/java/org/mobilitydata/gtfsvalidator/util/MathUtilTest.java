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

import com.google.common.truth.Expect;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MathUtilTest {
  @Rule public final Expect expect = Expect.create();

  @Test
  public void withinFractionOrMargin() {
    expect.that(MathUtil.withinFractionOrMargin(1e-20, 123456e-10, 1e-4, 1e-4)).isTrue();
    expect.that(MathUtil.withinFractionOrMargin(1e-20, -123456e-10, 1e-4, 1e-4)).isTrue();
    expect.that(MathUtil.withinFractionOrMargin(-1e-20, 12345e-10, 1e-4, 1e-4)).isTrue();
    expect.that(MathUtil.withinFractionOrMargin(-1e-20, -12345e-10, 1e-4, 1e-4)).isTrue();
    expect.that(MathUtil.withinFractionOrMargin(0.0, 123456e-10, 1e-4, 1e-4)).isTrue();
    expect.that(MathUtil.withinFractionOrMargin(0.0, -123456e-10, 1e-4, 1e-4)).isTrue();

    expect.that(MathUtil.withinFractionOrMargin(0.0, 1e-10, 1e-5, 1e-5)).isTrue();

    expect.that(MathUtil.withinFractionOrMargin(0.0, 1e-4, 1e-5, 1e-5)).isFalse();
  }
}
