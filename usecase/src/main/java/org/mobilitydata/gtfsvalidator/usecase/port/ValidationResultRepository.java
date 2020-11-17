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

package org.mobilitydata.gtfsvalidator.usecase.port;

import com.google.common.io.Resources;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;

import java.io.IOException;
import java.util.Collection;

/**
 * This hold contains notices generated during the validation process.
 */
public interface ValidationResultRepository {
    int MAX_NOTICE_COUNT = 100;
    String TEMP_PATH = Resources.getResource("./in-memory-simple/src/main/resources/temp_report").toString();

    Notice addNotice(Notice newNotice) throws TooManyValidationErrorException;

    Collection<Notice> getAll();

    NoticeExporter getExporter(boolean outputAsProto, String outputPath, boolean isPretty)
            throws IOException;

    int getWarningNoticeCount();

    int getErrorNoticeCount();

    int getInfoNoticeCount();

    int getNoticeCount();

    void flushRepo();
}
