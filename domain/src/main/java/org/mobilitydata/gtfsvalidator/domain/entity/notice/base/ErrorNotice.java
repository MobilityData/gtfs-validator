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

public abstract class ErrorNotice extends Notice {
    private static final String level = "ERROR";

    protected static final int E_001 = 1;
    // protected static final int E_002 = 2; // RESERVED - was CannotConstructDataProviderNotice
    protected static final int E_003 = 3;
    protected static final int E_004 = 4;
    protected static final int E_005 = 5;
    protected static final int E_006 = 6;
    protected static final int E_007 = 7;
    protected static final int E_008 = 8;
    // protected static final int E_009 = 9; // RESERVED - was CouldNotCleanOrCreatePathNotice (#112)
    protected static final int E_010 = 10;
    protected static final int E_011 = 11;
    protected static final int E_012 = 12;
    protected static final int E_013 = 13;
    protected static final int E_014 = 14;
    protected static final int E_015 = 15;
    protected static final int E_016 = 16;
    protected static final int E_017 = 17;
    protected static final int E_018 = 18;
    protected static final int E_019 = 19;
    protected static final int E_020 = 20;
    protected static final int E_021 = 21;
    protected static final int E_022 = 22;
    protected static final int E_023 = 23;
    protected static final int E_024 = 24;
    protected static final int E_025 = 25;
    protected static final int E_026 = 26;
    protected static final int E_027 = 27;
    protected static final int E_028 = 28;
    protected static final int E_029 = 29;
    protected static final int E_030 = 30;
    protected static final int E_031 = 31;
    protected static final int E_032 = 32;
    protected static final int E_033 = 33;
    protected static final int E_034 = 34;
    protected static final int E_035 = 35;
    protected static final int E_036 = 36;
    protected static final int E_037 = 37;
    protected static final int E_038 = 38;
    protected static final int E_039 = 39;
    protected static final int E_040 = 40;
    protected static final int E_041 = 41;
    protected static final int E_042 = 42;
    protected static final int E_043 = 43;
    protected static final int E_044 = 44;
    protected static final int E_045 = 45;
    protected static final int E_046 = 46;
    protected static final int E_047 = 47;
    protected static final int E_048 = 48;
    protected static final int E_049 = 49;
    protected static final int E_050 = 50;
    protected static final int E_051 = 51;
    protected static final int E_053 = 53;
    protected static final int E_055 = 55;
    protected static final int E_056 = 56;

    public ErrorNotice(final String filename,
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