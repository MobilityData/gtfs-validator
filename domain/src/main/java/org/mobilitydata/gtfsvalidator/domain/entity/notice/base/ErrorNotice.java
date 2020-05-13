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

    protected static final String E_001 = "E001";
    protected static final String E_002 = "E002";
    protected static final String E_003 = "E003";
    protected static final String E_004 = "E004";
    protected static final String E_005 = "E005";
    protected static final String E_006 = "E006";
    protected static final String E_007 = "E007";
    protected static final String E_008 = "E008";
    // protected static final String E_009 = "E009"; // RESERVED - was CouldNotCleanOrCreatePathNotice (#112)
    protected static final String E_010 = "E010";
    protected static final String E_011 = "E011";
    protected static final String E_012 = "E012";
    protected static final String E_013 = "E013";
    protected static final String E_014 = "E014";
    protected static final String E_015 = "E015";
    protected static final String E_016 = "E016";
    protected static final String E_017 = "E017";
    protected static final String E_018 = "E018";
    protected static final String E_019 = "E019";
    protected static final String E_020 = "E020";
    protected static final String E_021 = "E021";
    protected static final String E_022 = "E022";
    protected static final String E_023 = "E023";

    public ErrorNotice(final String filename,
                       final String noticeId,
                       final String title,
                       final String description,
                       String entityId) {
        super(filename, noticeId, title, description, entityId);
    }
}