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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for all entities defined in calendar_dates.txt. Can not be directly instantiated: user must use the
 * {@link CalendarDate.CalendarDateBuilder} to create this.
 */
public class CalendarDate extends GtfsEntity {
    @NotNull
    private final String serviceId;
    @NotNull
    private final LocalDate date;
    @NotNull
    final ExceptionType exceptionType;

    /**
     * @param serviceId     identifies a set of dates when a service exception occurs for one or more routes
     * @param date          date when service exception occurs
     * @param exceptionType indicates whether service is available on the date specified in the date field
     */
    private CalendarDate(@NotNull String serviceId,
                         @NotNull LocalDate date,
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
    public LocalDate getDate() {
        return date;
    }

    @NotNull
    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    /**
     * Builder class to create {@link CalendarDate} objects. Allows an unordered definition of the different attributes
     * of {@link CalendarDate}.
     */
    public static class CalendarDateBuilder {
        private String serviceId;
        private LocalDate date;
        private ExceptionType exceptionType;
        private Integer originalExceptionTypeInteger;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field serviceId value and returns this
         *
         * @param serviceId identifies a set of dates when a service exception occurs for one or more routes
         * @return builder for future object creation
         */
        public CalendarDateBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        /**
         * Sets field date value and returns this
         *
         * @param date date when service exception occurs
         * @return builder for future object creation
         */
        public CalendarDateBuilder date(@NotNull final LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * Sets field exceptionType value and returns this
         *
         * @param exceptionType indicates whether service is available on the date specified in the date field
         * @return builder for future object creation
         */
        public CalendarDateBuilder exceptionType(@NotNull final Integer exceptionType) {
            this.exceptionType = ExceptionType.fromInt(exceptionType);
            this.originalExceptionTypeInteger = exceptionType;
            return this;
        }

        /**
         * Returns an {@code EntityBuildResult} representing a row from calendar_dates.txt if the requirements from the
         * official GTFS specification are met. Otherwise, method returns an entity representing a list of notices.
         *
         * @return an {@link EntityBuildResult} representing a row from calendar_dates.txt if the requirements from the
         * official GTFS specification are met. Otherwise, method returns a collection of notices describing the issues.
         */
        public EntityBuildResult<?> build() {
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

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null;
         */
        public CalendarDateBuilder clear() {
            serviceId = null;
            date = null;
            exceptionType = null;
            originalExceptionTypeInteger = null;
            noticeCollection.clear();
            return this;
        }
    }

    /**
     * Generates an hash code for this {@link CalendarDate} based on service_id and date fields
     *
     * @return an hash code for this {@link CalendarDate} based on service_id and date fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(serviceId.hashCode(), date.hashCode());
    }

    /**
     * Determines if two {@link CalendarDate} are equal based on their hash codes
     *
     * @param object other {@link CalendarDate} to compare
     * @return true if the two {@link CalendarDate} are equals (i.e {@link CalendarDate have same hash codes}),
     * otherwise returns false.
     */
    @Override
    public boolean equals(final Object object) {
        if (getClass() != object.getClass()) {
            return false;
        }
        return hashCode() == object.hashCode();
    }
}