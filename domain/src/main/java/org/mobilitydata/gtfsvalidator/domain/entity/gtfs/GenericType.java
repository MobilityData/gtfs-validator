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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;

/**
 * Generic class to handle values generated at building stage of entities.
 * This class contains either a list of {@code Notice} or a GTFS entity.
 *
 * @param <T> list of {@link Notice} or GTFS entity.
 */
public class GenericType<T> {
    private final T data;
    private final boolean state;

    /**
     * Takes a value of "some type" and set it to the field
     */
    public GenericType(T data, boolean state) {
        this.data = data;
        this.state = state;
    }

    /**
     * Returns a value of "some type"
     *
     * @return a value of "some type"
     */
    public T getData() {
        return data;
    }

    /**
     * Returns true if success, otherwise false
     *
     * @return if success, otherwise false
     */
    public boolean getState() {
        return state;
    }
}