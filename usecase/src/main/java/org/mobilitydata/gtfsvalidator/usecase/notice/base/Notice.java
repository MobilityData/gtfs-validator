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

public abstract class Notice {
    protected String filename;
    protected String noticeId;
    protected String title;
    protected String description;

    protected Notice(final String filename,
                     final String noticeId,
                     final String title,
                     final String description) {
        this.filename = filename;
        this.noticeId = noticeId;
        this.title = title;
        this.description = description;
    }

    public abstract Notice visit(ValidationResultRepository resultRepo);

    public String getFilename() {
        return filename;
    }

    public String getId() {
        return noticeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "\nNotice{" +
                "filename='" + filename + '\'' +
                ", noticeId='" + noticeId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
