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

//TODO: use those to track progress (no error in file xxx, took xxms) maybe also have verbose level
public abstract class InfoNotice extends Notice {
    protected static final String I_001 = "I001";

    public InfoNotice(final String filename,
                      final String noticeId,
                      final String title,
                      final String description, String entityId) {
        super(filename, noticeId, title, description, entityId);
    }

    @Override
    public Notice visit(ValidationResultRepository resultRepo) {
        return resultRepo.addNotice(this);
    }

}
