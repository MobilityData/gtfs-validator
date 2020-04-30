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

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.time.LocalDateTime;
import java.util.List;

public class Calendar {

    @NotNull
    private final String serviceId;

    @NotNull
    private final Boolean monday;

    @NotNull
    private final Boolean tuesday;

    @NotNull
    private final Boolean wednesday;

    @NotNull
    private final Boolean thursday;

    @NotNull
    private final Boolean friday;

    @NotNull
    private final Boolean saturday;

    @NotNull
    private final Boolean sunday;

    @NotNull
    private final LocalDateTime startDate;

    @NotNull
    private final LocalDateTime endDate;

    private Calendar(@NotNull final String serviceId,
                     final boolean monday,
                     final boolean tuesday,
                     final boolean wednesday,
                     final boolean thursday,
                     final boolean friday,
                     final boolean saturday,
                     final boolean sunday,
                     @NotNull final LocalDateTime startDate,
                     @NotNull final LocalDateTime endDate) {
        this.serviceId = serviceId;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NotNull
    public String getServiceId() {
        return serviceId;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    @NotNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @NotNull
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public static class CalendarBuilder {
        private String serviceId;
        private Boolean monday;
        private Integer originalMondayInteger;
        private Boolean tuesday;
        private Integer originalTuesdayInteger;
        private Boolean wednesday;
        private Integer originalWednesdayInteger;
        private Boolean thursday;
        private Integer originalThursdayInteger;
        private Boolean friday;
        private Integer originalFridayInteger;
        private Boolean saturday;
        private Integer originalSaturdayInteger;
        private Boolean sunday;
        private Integer originalSundayInteger;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private final List<Notice> noticeCollection;

        public CalendarBuilder(final List<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
        }

        public CalendarBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public CalendarBuilder monday(final Integer monday) {
            if (monday == null) {
                this.monday = null;
            } else if (monday.equals(1)) {
                this.monday = true;
            } else if (monday.equals(0)) {
                this.monday = false;
            } else {
                this.monday = null;
            }
            this.originalMondayInteger = monday;
            return this;
        }

        public CalendarBuilder tuesday(final Integer tuesday) {
            if (tuesday == null) {
                this.tuesday = null;
            } else if (tuesday.equals(1)) {
                this.tuesday = true;
            } else if (tuesday.equals(0)) {
                this.tuesday = false;
            } else {
                this.tuesday = null;
            }
            this.originalTuesdayInteger = tuesday;
            return this;
        }

        public CalendarBuilder wednesday(final Integer wednesday) {
            if (wednesday == null) {
                this.wednesday = null;
            } else if (wednesday.equals(1)) {
                this.wednesday = true;
            } else if (wednesday.equals(0)) {
                this.wednesday = false;
            } else {
                this.wednesday = null;
            }
            this.originalWednesdayInteger = wednesday;
            return this;
        }

        public CalendarBuilder thursday(final Integer thursday) {
            if (thursday == null) {
                this.thursday = null;
            } else if (thursday.equals(1)) {
                this.thursday = true;
            } else if (thursday.equals(0)) {
                this.thursday = false;
            } else {
                this.thursday = null;
            }
            this.originalThursdayInteger = thursday;
            return this;
        }

        public CalendarBuilder friday(final Integer friday) {
            if (friday == null) {
                this.friday = null;
            } else if (friday.equals(1)) {
                this.friday = true;
            } else if (friday.equals(0)) {
                this.friday = false;
            } else {
                this.friday = null;
            }
            this.originalFridayInteger = friday;
            return this;
        }

        public CalendarBuilder saturday(final Integer saturday) {
            if (saturday == null) {
                this.saturday = null;
            } else if (saturday.equals(1)) {
                this.saturday = true;
            } else if (saturday.equals(0)) {
                this.saturday = false;
            } else {
                this.saturday = null;
            }
            this.originalSaturdayInteger = saturday;
            return this;
        }

        public CalendarBuilder sunday(final Integer sunday) {
            if (sunday == null) {
                this.sunday = null;
            } else if (sunday.equals(1)) {
                this.sunday = true;
            } else if (sunday.equals(0)) {
                this.sunday = false;
            } else {
                this.sunday = null;
            }
            this.originalSundayInteger = sunday;
            return this;
        }

        public CalendarBuilder startDate(@NotNull final LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public CalendarBuilder endDate(@NotNull final LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public EntityBuildResult<?> build() {
            noticeCollection.clear();
            if (monday == null || tuesday == null || wednesday == null || thursday == null || friday == null
                    || saturday == null || sunday == null || startDate == null || endDate == null || serviceId == null) {
                if (originalMondayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "monday", serviceId));
                } else if (originalMondayInteger < 0 || originalMondayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "monday", serviceId,
                                    0, 1, originalMondayInteger));
                }
                if (originalTuesdayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "tuesday", serviceId));
                } else if (originalTuesdayInteger < 0 || originalTuesdayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "tuesday",
                                    serviceId, 0, 1, originalTuesdayInteger));
                }
                if (originalWednesdayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "wednesday", serviceId));
                } else if (originalWednesdayInteger < 0 || originalWednesdayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "wednesday",
                                    serviceId, 0, 1, originalWednesdayInteger));
                }
                if (originalThursdayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "thursday", serviceId));
                } else if (originalThursdayInteger < 0 || originalThursdayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "thursday",
                                    serviceId, 0, 1, originalThursdayInteger));
                }
                if (originalFridayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "friday",
                                    serviceId));
                } else if (originalFridayInteger < 0 || originalFridayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "friday", serviceId,
                                    0, 1, originalFridayInteger));
                }
                if (originalSaturdayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "saturday", serviceId));
                } else if (originalSaturdayInteger < 0 || originalSaturdayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "saturday",
                                    serviceId, 0, 1, originalSaturdayInteger));
                }
                if (originalSundayInteger == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "sunday", serviceId));
                } else if (originalSundayInteger < 0 || originalSundayInteger > 1) {
                    noticeCollection.add(
                            new IntegerFieldValueOutOfRangeNotice("calendar.txt", "sunday",
                                    serviceId, 0, 1, originalSundayInteger));
                }
                if (serviceId == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "service_id", serviceId));
                }
                if (startDate == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "start_date", serviceId));
                }
                if (endDate == null) {
                    noticeCollection.add(
                            new MissingRequiredValueNotice("calendar.txt", "end_date", serviceId));
                }
                return new EntityBuildResult<>(noticeCollection, EntityBuildResult.Status.FAILURE);
            } else {
                return new EntityBuildResult<>(new Calendar(serviceId, monday, tuesday, wednesday, thursday, friday,
                        saturday, sunday, startDate, endDate),
                        EntityBuildResult.Status.SUCCESS);
            }
        }
    }
}