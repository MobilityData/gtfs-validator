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

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;

import java.util.List;

/**
 * Generic class to handle values generated at building stage of entities.
 * This class contains either a list of {@code Notice} or a GTFS entity.
 *
 * @param <T> list of {@link Notice} or GTFS entity.
 */
public class EntityBuildResult<T> {
    private final T data;
    private final Status status;

    /**
     * Takes a value of type list and sets it to the field data. Sets field status to Failure.
     * of gtfs entity building
     */
    public EntityBuildResult(final List<Notice> data) {
        //noinspection unchecked
        this.data = (T) data;
        this.status = Status.FAILURE;
    }

    /**
     * Takes a value of type Agency and sets it to the field data. Sets field status to Success.
     * of gtfs entity building
     */
    public EntityBuildResult(final Agency agency) {
        //noinspection unchecked
        this.data = (T) agency;
        this.status = Status.SUCCESS;
    }

    /**
     * Takes a value of type Route and sets it to the field data. Sets field status to Success.
     * of gtfs entity building
     */
    public EntityBuildResult(final Route route) {
        //noinspection unchecked
        this.data = (T) route;
        this.status = Status.SUCCESS;
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
     * Returns true if entity building succeeded, otherwise returns false
     *
     * @return true if entity building succeeded, otherwise returns false
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /**
     * Enum item to clearly define the building status of a gtfs entity.
     */
    private enum Status {
        FAILURE,
        SUCCESS
    }
}