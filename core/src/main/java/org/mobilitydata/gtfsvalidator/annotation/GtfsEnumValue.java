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

import java.lang.annotation.*;

/**
 * Specifies a value for a GTFS enum. This information will be used to generate the actual Java class for the enum.
 * <p>
 * This annotation should be applied to an interface called ${TypeName}Enum and the annotation processor creates a
 * ${TypeName} Java enum for it.
 * <p>
 * Example. Annotation processor creates {@code GtfsLocationType} for the given schema.
 *
 * <pre>
 *   @GtfsEnumValue(name = "STOP", value = 0)
 *   @GtfsEnumValue(name = "STATION", value = 1)
 *   @GtfsEnumValue(name = "ENTRANCE", value = 2)
 *   @GtfsEnumValue(name = "GENERIC_NODE", value = 3)
 *   @GtfsEnumValue(name = "BOARDING_AREA", value = 4)
 *   public interface GtfsLocationTypeEnum {
 *   }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(GtfsEnumValues.class)
public @interface GtfsEnumValue {
    String name();

    int value();
}
