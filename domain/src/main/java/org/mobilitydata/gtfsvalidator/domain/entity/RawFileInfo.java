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

package org.mobilitydata.gtfsvalidator.domain.entity;

/**
 * Contains information regarding a file location and expected content (file name)
 */
public class RawFileInfo {

    private final String filename;
    private final String path;

    /**
     * @param filename the name of the file
     * @param path     a relative path to access a file
     */
    private RawFileInfo(final String filename, final String path) {
        this.filename = filename;
        this.path = path;
    }

    /**
     * Returns a builder for this. This can be built either from the filename or from
     * the path.
     *
     * @return a builder for this
     */
    public static RawFileInfoBuilder builder() {
        return new RawFileInfoBuilder();
    }

    public static class RawFileInfoBuilder {
        private String filename;
        private String path;

        public RawFileInfoBuilder() {
        }

        public RawFileInfoBuilder filename(final String filename) {
            this.filename = filename;
            return this;
        }

        public RawFileInfoBuilder path(final String path) {
            this.path = path;
            return this;
        }

        /**
         * Build and return a new instance
         *
         * @return a new instance
         */
        public RawFileInfo build() {
            return new RawFileInfo(filename, path);
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }
}
