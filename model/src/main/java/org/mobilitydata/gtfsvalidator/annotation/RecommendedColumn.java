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
 * Adds a validation that it's recommended that a column be present. A value for the field may be
 * optional.
 *
 * <p>Example.
 *
 * <pre>
 *   {@literal @}GtfsTable("stop_times.txt")
 *    public interface GtfsStopTimeSchema extends GtfsEntity {
 *
 *     {@literal @}DefaultValue("1")
 *     {@literal @}RecommendedColumn
 *      GtfsStopTimeTimepoint timepoint();
 *   }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RecommendedColumn {}
