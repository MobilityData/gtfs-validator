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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches field values for a single table.
 * <p>
 * There are many fields that repeat for multiple rows of a single table, e.g., stop_headsign for stop_times.txt or
 * shape_id for shapes.txt. Caching of those values dramatically saves memory for large feeds, e.g., a feed with
 * 25 M stop times requires 8 GiB without caching and 3 GiB with caching.
 * <p>
 * All tables are read in parallel, that's why we create a separate set of caches for each table. It is tempting to use
 * the same cache for, e.g., trip_id in both trips.txt and stop_times.txt but that would require synchronization that
 * slows down the reading.
 *
 * @param <T> the type of the cached objects. It must be suitable as a key for hash maps.
 */
public class FieldCache<T> {
    private final Map<T, T> cache = new HashMap<>();

    private int invocationCount = 0;

    /**
     * Adds the object to the cache if it is absent. Returns a reference to the given object in cache.
     * <p>
     * Note that it is not the same as {@code Map.putIfAbsent()} which returns {@code null} if the the object was not
     * already in the map.
     *
     * @param obj object to store in cache.
     * @return reference to the object in cache.
     */
    public @Nullable
    T addIfAbsent(@Nullable T obj) {
        ++invocationCount;
        if (obj == null) {
            // Do not store null in the cache.
            return null;
        }
        // Benchmarks show that computeIfAbsent() is about 20% more expensive than calling get() and put().
        T inCache = cache.get(obj);
        if (inCache == null) {
            inCache = obj;
            cache.put(inCache, inCache);
        }
        return inCache;
    }

    /**
     * Returns amount of invocations of {@code addIfAbsent}.
     *
     * @return amount of invocations of {@code addIfAbsent}.
     */
    public int getInvocationCount() {
        return invocationCount;
    }

    /**
     * Returns cache size.
     *
     * @return cache size.
     */
    public int getCacheSize() {
        return cache.size();
    }
}
