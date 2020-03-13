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

package org.mobilitydata.gtfsvalidator.usecase.notice.base;

import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class WarningNotice extends Notice {

    protected static final String W_001 = "W001";
    protected static final String W_002 = "W002";
    protected static final String W_003 = "W003";
    protected static final String W_004 = "W004";

    public WarningNotice(final String filename,
                         final String noticeId,
                         final String title,
                         final String description) {
        super(filename, noticeId, title, description);
    }

    @Override
    public Notice visit(ValidationResultRepository resultRepo) {
        return resultRepo.addNotice(this);
    }
}
