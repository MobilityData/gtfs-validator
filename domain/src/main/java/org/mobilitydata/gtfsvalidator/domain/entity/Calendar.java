package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class Calendar {

    @NotNull
    private final String serviceId;

    private final boolean monday;
    private final boolean tuesday;
    private final boolean wednesday;
    private final boolean thursday;
    private final boolean friday;
    private final boolean saturday;
    private final boolean sunday;

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

        @NotNull
        private String serviceId;

        private boolean monday;
        private boolean tuesday;
        private boolean wednesday;
        private boolean thursday;
        private boolean friday;
        private boolean saturday;
        private boolean sunday;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        public CalendarBuilder(@NotNull String serviceId,
                               int monday,
                               int tuesday,
                               int wednesday,
                               int thursday,
                               int friday,
                               int saturday,
                               int sunday,
                               @NotNull LocalDateTime startDate,
                               @NotNull LocalDateTime endDate) {
            this.serviceId = serviceId;
            this.monday = monday == 1;
            this.tuesday = tuesday == 1;
            this.wednesday = wednesday == 1;
            this.thursday = thursday == 1;
            this.friday = friday == 1;
            this.saturday = saturday == 1;
            this.sunday = sunday == 1;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public CalendarBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public CalendarBuilder monday(final int monday) {
            this.monday = monday == 1;
            return this;
        }

        public CalendarBuilder tuesday(final int tuesday) {
            this.tuesday = tuesday == 1;
            return this;
        }

        public CalendarBuilder wednesday(final int wednesday) {
            this.wednesday = wednesday == 1;
            return this;
        }

        public CalendarBuilder thursday(final int thursday) {
            this.thursday = thursday == 1;
            return this;
        }

        public CalendarBuilder friday(final int friday) {
            this.friday = friday == 1;
            return this;
        }

        public CalendarBuilder saturday(final int saturday) {
            this.saturday = saturday == 1;
            return this;
        }

        public CalendarBuilder sunday(final int sunday) {
            this.sunday = sunday == 1;
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

        public Calendar build() {
            return new Calendar(serviceId, monday, tuesday, wednesday, thursday, friday, saturday,
                    sunday, startDate, endDate);
        }
    }
}