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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.time.LocalDateTime;
import java.util.List;

public class CalendarDate {
    @NotNull
    private final String serviceId;
    @NotNull
    private final LocalDateTime date;
    @NotNull
    final ExceptionType exceptionType;

    private CalendarDate(@NotNull String serviceId,
                         @NotNull LocalDateTime date,
                         @NotNull ExceptionType exceptionType) {
        this.serviceId = serviceId;
        this.date = date;
        this.exceptionType = exceptionType;
    }

    @NotNull
    public String getServiceId() {
        return serviceId;
    }

    @NotNull
    public LocalDateTime getDate() {
        return date;
    }

    @NotNull
    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public static class CalendarDateBuilder {
        @Nullable
        private String serviceId;
        @Nullable
        private LocalDateTime date;
        @Nullable
        private ExceptionType exceptionType;
        private Integer originalExceptionTypeInteger;
        private final List<Notice> noticeCollection;

        public CalendarDateBuilder(final List<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
        }

        public CalendarDateBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public CalendarDateBuilder date(@NotNull final LocalDateTime date) {
            this.date = date;
            return this;
        }

        public CalendarDateBuilder exceptionType(@NotNull final Integer exceptionType) {
            this.exceptionType = ExceptionType.fromInt(exceptionType);
            this.originalExceptionTypeInteger = exceptionType;
            return this;
        }

        public EntityBuildResult<?> build() {
            noticeCollection.clear();
            if (serviceId == null || date == null || exceptionType == null) {
                if (serviceId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("calendar_dates.txt",
                            "service_id", serviceId));
                }
                if (date == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("calendar_dates.txt",
                            "date", serviceId));
                }
                if (exceptionType == null) {
                    if (originalExceptionTypeInteger == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("calendar_dates.txt",
                                "exception_type", serviceId));
                    } else if (!ExceptionType.isEnumValueValid(originalExceptionTypeInteger)) {
                        noticeCollection.add(new UnexpectedEnumValueNotice("calendar_dates.txt",
                                "exception_type", serviceId, originalExceptionTypeInteger));
                    }
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new CalendarDate(serviceId, date, exceptionType));
            }
        }
    }
}