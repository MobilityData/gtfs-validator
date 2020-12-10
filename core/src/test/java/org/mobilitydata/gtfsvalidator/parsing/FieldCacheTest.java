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
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class FieldCacheTest {
    @Test
    public void hitsAndMisses() {
        FieldCache<String> cache = new FieldCache<>();

        // Test an empty cache.
        assertThat(cache.getLookupCount()).isEqualTo(0);
        assertThat(cache.getCacheSize()).isEqualTo(0);
        assertThat(cache.getCacheHits()).isEqualTo(0);
        assertThat(cache.getCacheMisses()).isEqualTo(0);
        assertThat(cache.getHitRatio()).isEqualTo(1);
        assertThat(cache.getMissRatio()).isEqualTo(0);

        // Add two different values.
        String s1 = "s1";
        String s2 = "s2";
        assertThat(cache.addIfAbsent(s1)).isSameInstanceAs(s1);
        assertThat(cache.addIfAbsent(s2)).isSameInstanceAs(s2);

        assertThat(cache.getLookupCount()).isEqualTo(2);
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getCacheHits()).isEqualTo(0);
        assertThat(cache.getCacheMisses()).isEqualTo(2);
        assertThat(cache.getHitRatio()).isEqualTo(0);
        assertThat(cache.getMissRatio()).isEqualTo(1);

        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent("s1")).isSameInstanceAs(s1);
        assertThat(cache.addIfAbsent("s2")).isSameInstanceAs(s2);

        assertThat(cache.getLookupCount()).isEqualTo(4);
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getCacheHits()).isEqualTo(2);
        assertThat(cache.getCacheMisses()).isEqualTo(2);
        assertThat(cache.getHitRatio()).isEqualTo(0.5);
        assertThat(cache.getMissRatio()).isEqualTo(0.5);
    }

    @Test
    public void lookupNull() {
        FieldCache<String> cache = new FieldCache<>();

        assertThat(cache.addIfAbsent(null)).isNull();

        assertThat(cache.getLookupCount()).isEqualTo(1);
        assertThat(cache.getCacheSize()).isEqualTo(0);
        assertThat(cache.getCacheHits()).isEqualTo(1);
        assertThat(cache.getCacheMisses()).isEqualTo(0);
        assertThat(cache.getHitRatio()).isEqualTo(1);
        assertThat(cache.getMissRatio()).isEqualTo(0);
    }

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
        assertThat(cache.getLookupCount()).isEqualTo(4);
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
        assertThat(cache.getLookupCount()).isEqualTo(4);
    }

    @Test
    public void cacheDate() {
        FieldCache<GtfsDate> cache = new FieldCache<>();
        GtfsDate d1 = GtfsDate.fromEpochDay(100);
        GtfsDate d2 = GtfsDate.fromEpochDay(200);
        assertThat(cache.addIfAbsent(d1)).isSameInstanceAs(d1);
        assertThat(cache.addIfAbsent(d2)).isSameInstanceAs(d2);
        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent(GtfsDate.fromEpochDay(100))).isSameInstanceAs(d1);
        assertThat(cache.addIfAbsent(GtfsDate.fromEpochDay(200))).isSameInstanceAs(d2);
        // Check cache size and efficiency.
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getLookupCount()).isEqualTo(4);
    }

    @Test
    public void cacheColor() {
        FieldCache<GtfsColor> cache = new FieldCache<>();
        GtfsColor c1 = GtfsColor.fromInt(0);
        GtfsColor c2 = GtfsColor.fromInt(0xff00ff);
        assertThat(cache.addIfAbsent(c1)).isSameInstanceAs(c1);
        assertThat(cache.addIfAbsent(c2)).isSameInstanceAs(c2);
        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent(GtfsColor.fromInt(0))).isSameInstanceAs(c1);
        assertThat(cache.addIfAbsent(GtfsColor.fromInt(0xff00ff))).isSameInstanceAs(c2);
        // Check cache size and efficiency.
        assertThat(cache.getCacheSize()).isEqualTo(2);
        assertThat(cache.getLookupCount()).isEqualTo(4);
    }

    @Test
    public void cacheLanguage() {
        FieldCache<Locale> cache = new FieldCache<>();
        Locale l1 = Locale.forLanguageTag("en");
        Locale l2 = Locale.forLanguageTag("fr");
        Locale l3 = Locale.forLanguageTag("fr-CA");
        assertThat(cache.addIfAbsent(l1)).isSameInstanceAs(l1);
        assertThat(cache.addIfAbsent(l2)).isSameInstanceAs(l2);
        assertThat(cache.addIfAbsent(l3)).isSameInstanceAs(l3);
        // Put the same values again as different objects.
        assertThat(cache.addIfAbsent(Locale.forLanguageTag("en"))).isSameInstanceAs(l1);
        assertThat(cache.addIfAbsent(Locale.forLanguageTag("fr"))).isSameInstanceAs(l2);

        assertThat(Locale.forLanguageTag("en")).isSameInstanceAs(l1);
        // Check cache size and efficiency.
        assertThat(cache.getCacheSize()).isEqualTo(3);
        assertThat(cache.getLookupCount()).isEqualTo(5);
    }
}
