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

package org.mobilitydata.gtfsvalidator.parsing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class FieldCacheTest {
    @Test
    public void cacheString() {
        FieldCache<String> cache = new FieldCache<>();
        String s1 = "s1";
        String s2 = "s2";
        assertThat(cache.addIfAbsent(s1)).isSameInstanceAs(s1);
        assertThat(cache.addIfAbsent(s2)).isSameInstanceAs(s2);
        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent("s1")).isSameInstanceAs(s1);
        assertThat(cache.addIfAbsent("s2")).isSameInstanceAs(s2);
        // Check cache size and efficiency.
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getInvocationCount()).isEqualTo(4);
    }

    @Test
    public void cacheTime() {
        FieldCache<GtfsTime> cache = new FieldCache<>();
        GtfsTime t1 = GtfsTime.fromSecondsSinceMidnight(1);
        GtfsTime t2 = GtfsTime.fromSecondsSinceMidnight(2);
        assertThat(cache.addIfAbsent(t1)).isSameInstanceAs(t1);
        assertThat(cache.addIfAbsent(t2)).isSameInstanceAs(t2);
        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent(GtfsTime.fromSecondsSinceMidnight(1))).isSameInstanceAs(t1);
        assertThat(cache.addIfAbsent(GtfsTime.fromSecondsSinceMidnight(2))).isSameInstanceAs(t2);
        // Check cache size and efficiency.
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getInvocationCount()).isEqualTo(4);
    }
}
