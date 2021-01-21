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

package org.mobilitydata.gtfsvalidator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the first part of a composite key in tables like stop_times.txt (trip_id).
 *
 * <p>This annotation needs to be used in a combination with {@code @SequenceKey}.
 *
 * <p>Note that {@code @FirstKey} does not imply that the field is required and you need to put an
 * extra {@code @Required} annotation in this case.
 *
 * <pre>
 *   @GtfsTable("stop_times.txt")
 *   public interface GtfsStopTimeSchema extends GtfsEntity {
 *     @Required
 *     @ForeignKey(table = "trips.txt", field = "trip_id")
 *     @FirstKey
 *     String tripId();
 *
 *     @Required
 *     @NonNegative
 *     @SequenceKey
 *     int stopSequence();
 *  }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FirstKey {}
