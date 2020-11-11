/*
 * Copyright (c) 2019. MobilityData IO.
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

package org.mobilitydata.gtfsvalidator.usecase.utils;

import java.nio.file.Path;

public interface CustomFileUtils {
    String MEGABYTES = "mb";

    /**
     * Returns the size of a file given its path in bytes
     * @param pathToFile  the path to the file as String
     * @return the size of a file given its path in bytes
     */
    long sizeOf(final Path pathToFile);

    /**
     * Returns the size of a file given its path in the specified unit. If no unit is specified, will return the result
     * in bytes
     * @param pathToFile  the path to the file as String
     * @param unit        the unit in which to express the result
     * @return  the size of a file given its path in the specified unit. If no unit is specified, will return the result
     * in bytes
     */
    long sizeOf(final Path pathToFile, final String unit);

    /**
     * Returns the size of a directory given its path in bytes
     * @param pathToDirectory  the path to the directory as String
     * @return the size of a directory given its path in bytes
     */
    long sizeOfDirectory(final Path pathToDirectory);
}
