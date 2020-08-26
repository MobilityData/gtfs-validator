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

public abstract class WarningNotice extends Notice {
    private static final String level = "WARNING";

    protected static final int W_001 = 1;
    protected static final int W_002 = 2;
    protected static final int W_003 = 3;
    protected static final int W_004 = 4;
    protected static final int W_005 = 5;
    protected static final int W_006 = 6;
    protected static final int W_007 = 7;
    protected static final int W_008 = 8;
    protected static final int W_009 = 9;
    protected static final int W_010 = 10;
    protected static final int W_011 = 11;
    protected static final int W_012 = 12;
    protected static final int W_014 = 14;
    protected static final int W_015 = 15;
    protected static final int W_016 = 16;

    public WarningNotice(final String filename,
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
                ", noticeSpecific='" + getNoticeSpecificAll() + '\'' +
                '}';
    }
}
