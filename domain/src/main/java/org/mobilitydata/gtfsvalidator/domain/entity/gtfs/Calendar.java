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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in calendar.txt. Can not be directly instantiated: user must use
 * {@link CalendarBuilder} to create this.
 */
public class Calendar extends GtfsEntity {
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
    private final LocalDate startDate;
    @NotNull
    private final LocalDate endDate;

    /**
     * Class for all entities defined in calendar.txt
     *
     * @param serviceId uniquely identifies a set of dates when service is available for one or more routes
     * @param monday    indicates whether the service operates on all mondays in the date range specified
     *                  by {@param startDate} and {@param endDate} fields
     * @param tuesday   functions in the same way as monday except applies to tuesdays
     * @param wednesday functions in the same way as monday except applies to wednesday
     * @param thursday  functions in the same way as monday except applies to thursday
     * @param friday    functions in the same way as monday except applies to friday
     * @param saturday  functions in the same way as monday except applies to saturday
     * @param sunday    functions in the same way as monday except applies to sunday
     * @param startDate start service day for the service interval
     * @param endDate   end service day for the service interval
     */
    private Calendar(@NotNull final String serviceId,
                     @NotNull final Boolean monday,
                     @NotNull final Boolean tuesday,
                     @NotNull final Boolean wednesday,
                     @NotNull final Boolean thursday,
                     @NotNull final Boolean friday,
                     @NotNull final Boolean saturday,
                     @NotNull final Boolean sunday,
                     @NotNull final LocalDate startDate,
                     @NotNull final LocalDate endDate) {
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

    @NotNull
    public Boolean isMonday() {
        return monday;
    }

    @NotNull
    public Boolean isTuesday() {
        return tuesday;
    }

    @NotNull
    public Boolean isWednesday() {
        return wednesday;
    }

    @NotNull
    public Boolean isThursday() {
        return thursday;
    }

    @NotNull
    public Boolean isFriday() {
        return friday;
    }

    @NotNull
    public Boolean isSaturday() {
        return saturday;
    }

    @NotNull
    public Boolean isSunday() {
        return sunday;
    }

    @NotNull
    public LocalDate getStartDate() {
        return startDate;
    }

    @NotNull
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Builder class to create {@link Calendar} objects. Allows an unordered definition of the different attributes of
     * {@link Calendar}.
     */
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
        private LocalDate startDate;
        private LocalDate endDate;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field serviceId value and returns this
         *
         * @param serviceId uniquely identifies a set of dates when service is available for one or more routes
         * @return builder for future object creation
         */
        public CalendarBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        /**
         * Sets fields monday and originalMondayInteger value and returns this
         *
         * @param monday indicates whether the service operates on all mondays in the date range specified
         *               by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder monday(@NotNull final Integer monday) {
            // to avoid lint regarding nullability of parameter monday. It is safe to remove this warning since
            // parameter monday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field tuesday and originalTuesdayInteger value and returns this
         *
         * @param tuesday indicates whether the service operates on all tuesday in the date range specified
         *                by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder tuesday(@NotNull final Integer tuesday) {
            // to avoid lint regarding nullability of parameter tuesday. It is safe to remove this warning since
            // parameter tuesday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field wednesday and originalWednesdayInteger value and returns this
         *
         * @param wednesday indicates whether the service operates on all wednesday in the date range specified
         *                  by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder wednesday(@NotNull final Integer wednesday) {
            // to avoid lint regarding nullability of parameter wednesday. It is safe to remove this warning since
            // parameter wednesday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field thursday and originalThursdayInteger value and returns this
         *
         * @param thursday indicates whether the service operates on all thursday in the date range specified
         *                 by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder thursday(@NotNull final Integer thursday) {
            // to avoid lint regarding nullability of parameter thursday. It is safe to remove this warning since
            // parameter thursday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field friday and originalFridayInteger value and returns this
         *
         * @param friday indicates whether the service operates on all friday in the date range specified
         *               by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder friday(@NotNull final Integer friday) {
            // to avoid lint regarding nullability of parameter friday. It is safe to remove this warning since
            // parameter friday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field saturday and originalSaturdayInteger value and returns this
         *
         * @param saturday indicates whether the service operates on friday saturday in the date range specified
         *                 by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder saturday(@NotNull final Integer saturday) {
            // to avoid lint regarding nullability of parameter saturday. It is safe to remove this warning since
            // parameter saturday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field saturday and originalSundayInteger value and returns this
         *
         * @param sunday indicates whether the service operates on sunday saturday in the date range specified
         *               by {startDate} and {endDate} fields
         * @return builder for future object creation
         */
        public CalendarBuilder sunday(@NotNull final Integer sunday) {
            // to avoid lint regarding nullability of parameter sunday. It is safe to remove this warning since
            // parameter sunday can be null even though it should not be.
            //noinspection ConstantConditions
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

        /**
         * Sets field startDate value and returns this
         *
         * @param startDate start service day for the service interval
         * @return builder for future object creation
         */
        public CalendarBuilder startDate(@NotNull final LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Sets field endDate value and returns this
         *
         * @param endDate start service day for the service interval
         * @return builder for future object creation
         */
        // suppressed warning regarding unused return value of method to avoid lint. This method is usually called last
        // in the process of creating an entity. Thereby its return value is not used.
        @SuppressWarnings("UnusedReturnValue")
        public CalendarBuilder endDate(@NotNull final LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Entity representing a row from calendar.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns list of {@link Notice}.
         *
         * @return Entity representing a row from calendar.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns list of {@link Notice}.
         */
        public EntityBuildResult<?> build() {
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
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Calendar(serviceId, monday, tuesday, wednesday, thursday, friday,
                        saturday, sunday, startDate, endDate));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null;
         */
        public CalendarBuilder clear() {
            serviceId = null;
            monday = null;
            originalMondayInteger = null;
            tuesday = null;
            originalTuesdayInteger = null;
            wednesday = null;
            originalWednesdayInteger = null;
            thursday = null;
            originalThursdayInteger = null;
            friday = null;
            originalFridayInteger = null;
            saturday = null;
            originalSaturdayInteger = null;
            sunday = null;
            originalSundayInteger = null;
            startDate = null;
            endDate = null;
            noticeCollection.clear();
            return this;
        }
    }

    /**
     * Utility method to determine if date ranges of {@link Calendar} overlap
     *
     * @param otherCalendar {@link Calendar} to compare
     * @return true if calendars date ranges overlap, else false
     */
    public boolean areCalendarOverlapping(final Calendar otherCalendar) {
        return
                // calendars have a common date:
                (startDate.isEqual(otherCalendar.getStartDate()) || startDate.isEqual(otherCalendar.getEndDate()) ||
                        endDate.isEqual(otherCalendar.getStartDate()) || endDate.isEqual(otherCalendar.getEndDate()))
                        || // calendars overlap
                        startDate.isBefore(otherCalendar.getEndDate()) && endDate.isAfter(otherCalendar.getStartDate());
    }

    /**
     * Utility method to determine the common days of week of operation for two {@link Calendar}
     *
     * @param otherCalendar {@link Calendar} to compare
     * @return the list of days both calendar are active as a list of {@link String}
     */
    public List<String> getCalendarsCommonOperationDayCollection(final Calendar otherCalendar) {
        final List<String> toReturn = new ArrayList<>();
        if (isMonday() && otherCalendar.isMonday()) {
            toReturn.add("monday");
        }
        if (isTuesday() && otherCalendar.isTuesday()) {
            toReturn.add("tuesday");
        }
        if (isWednesday() && otherCalendar.isWednesday()) {
            toReturn.add("wednesday");
        }
        if (isThursday() && otherCalendar.isThursday()) {
            toReturn.add("thursday");
        }
        if (isFriday() && otherCalendar.isFriday()) {
            toReturn.add("friday");
        }
        if (isSaturday() && otherCalendar.isSaturday()) {
            toReturn.add("saturday");
        }
        if (isSunday() && otherCalendar.isSunday()) {
            toReturn.add("sunday");
        }
        return toReturn;
    }
}