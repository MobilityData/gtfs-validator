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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.base;

//TODO: use those to track progress (no error in file xxx, took xxms) maybe also have verbose level
public abstract class InfoNotice extends Notice {
    private static final String level = "INFO";
    protected static final int I_001 = 1;
    protected static final int I_002 = 2;
    protected static final int I_003 = 3;

    public InfoNotice(final String filename,
                      final int code,
                      final String title,
                      final String description,
                      final String entityId) {
        super(filename, code, title, description, entityId);
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "\nNotice{" +
                "filename='" + getFilename() + '\'' +
                ", level='" + getLevel() + '\'' +
                ", code='" + getCode() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", extra='" + getNoticeSpecificAll() + '\'' +
                '}';
    }
}
