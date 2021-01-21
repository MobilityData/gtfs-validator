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

/**
 * Specifies a default value for a particular GTFS field.
 *
 * <p>The value needs to be given as a string in the same form as it would appear in a GTFS file.
 *
 * <p>Example.
 *
 * <pre>
 *   @GtfsTable("routes.txt")
 *   public interface GtfsRouteSchema extends GtfsEntity {
 *       @DefaultValue("FFFFFF")
 *       GtfsColor routeColor();
 *   }
 * </pre>
 */
public @interface DefaultValue {
  String value();
}
