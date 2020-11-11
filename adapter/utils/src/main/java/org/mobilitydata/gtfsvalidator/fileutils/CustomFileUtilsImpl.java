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

package org.mobilitydata.gtfsvalidator.fileutils;

import org.apache.commons.io.FileUtils;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;

import java.io.File;
import java.nio.file.Path;

public class CustomFileUtilsImpl implements CustomFileUtils {
    private static CustomFileUtilsImpl CUSTOM_FILE_UTILS = null;

    private CustomFileUtilsImpl() {
    }

    /**
     * Implement singleton pattern
     *
     * @return a unique instance of {@code CustomFileUtilsImpl}
     */
    public static CustomFileUtilsImpl getInstance() {
        if (CUSTOM_FILE_UTILS == null) {
            return new CustomFileUtilsImpl();
        }
        return CUSTOM_FILE_UTILS;
    }

    /**
     * Returns the size of a file given its path
     * @param pathToFile  the path to the file as String
     * @return the size of a file given its path
     */
    @Override
    public long sizeOf(final Path pathToFile) {
        return FileUtils.sizeOf(new File(pathToFile.toString()));
    }

    @Override
    public long sizeOf(final Path pathToFile, final String unit) {
        switch (unit) {
            case MEGABYTES: {
                return FileUtils.sizeOf(new File(pathToFile.toString()))/FileUtils.ONE_MB;
            }
            default: {
                return sizeOf(pathToFile);
            }
        }
    }

    /**
     * Returns the size of a directory given its path
     * @param pathToDirectory  the path to the directory as String
     * @return the size of a directory given its path
     */
    @Override
    public long sizeOfDirectory(final Path pathToDirectory) {
        return FileUtils.sizeOfDirectory(new File(pathToDirectory.toString()));
    }
}
