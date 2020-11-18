/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class FatalInternalErrorNotice extends ErrorNotice {
    public FatalInternalErrorNotice(final String exceptionMessage,
                                    final String exceptionStackTrace) {
        super(null,
                E_060,
                "Fatal internal error -- please report",
                String.format("An exception occurred and the validator could not complete the validation process." +
                        " Please report this error. See detailed message for more information: %s -- stack trace: %s",
                        exceptionMessage,
                        exceptionStackTrace),
                null);
        putNoticeSpecific(KEY_EXCEPTION_MESSAGE, exceptionMessage);
        putNoticeSpecific(KEY_EXCEPTION_STACK_TRACE, exceptionStackTrace);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
