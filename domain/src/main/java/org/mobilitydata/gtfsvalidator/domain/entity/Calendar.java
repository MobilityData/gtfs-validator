package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

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
        private Boolean tuesday;
        private Boolean wednesday;
        private Boolean thursday;
        private Boolean friday;
        private Boolean saturday;
        private Boolean sunday;
        private LocalDateTime startDate;
        private LocalDateTime endDate;


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
            }
            return this;
        }

        public CalendarBuilder tuesday(final Integer tuesday) {
            if (tuesday == null) {
                this.tuesday = null;
            } else if (tuesday.equals(1)) {
                this.tuesday = true;
            } else if (tuesday.equals(0)) {
                this.tuesday = false;
            }
            return this;
        }

        public CalendarBuilder wednesday(final Integer wednesday) {
            if (wednesday == null) {
                this.wednesday = null;
            } else if (wednesday.equals(1)) {
                this.wednesday = true;
            } else if (wednesday.equals(0)) {
                this.wednesday = false;
            }
            return this;
        }

        public CalendarBuilder thursday(final Integer thursday) {
            if (thursday == null) {
                this.thursday = null;
            } else if (thursday.equals(1)) {
                this.thursday = true;
            } else if (thursday.equals(0)) {
                this.thursday = false;
            }
            return this;
        }

        public CalendarBuilder friday(final Integer friday) {
            if (friday == null) {
                this.friday = null;
            } else if (friday.equals(1)) {
                this.friday = true;
            } else if (friday.equals(0)) {
                this.friday = false;
            }
            return this;
        }

        public CalendarBuilder saturday(final Integer saturday) {
            if (saturday == null) {
                this.saturday = null;
            } else if (saturday.equals(1)) {
                this.saturday = true;
            } else if (saturday.equals(0)) {
                this.saturday = false;
            }
            return this;
        }

        public CalendarBuilder sunday(final Integer sunday) {
            if (sunday == null) {
                this.sunday = null;
            } else if (sunday.equals(1)) {
                this.sunday = true;
            } else if (sunday.equals(0)) {
                this.sunday = false;
            }
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
            if (monday == null) {
                throw new IllegalArgumentException("invalid value found for field monday");
            }
            if (tuesday == null) {
                throw new IllegalArgumentException("invalid value found for field tuesday");
            }
            if (wednesday == null) {
                throw new IllegalArgumentException("invalid value found for field wednesday");
            }
            if (thursday == null) {
                throw new IllegalArgumentException("invalid value found for field thursday");
            }
            if (friday == null) {
                throw new IllegalArgumentException("invalid value found for field friday");
            }
            if (saturday == null) {
                throw new IllegalArgumentException("invalid value found for field saturday");
            }
            if (sunday == null) {
                throw new IllegalArgumentException("invalid value found for field sunday");
            }
            if (serviceId == null) {
                throw new IllegalArgumentException("field service_id can not be null");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("field start_date can not be null");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("field end_date can not be null");
            }
            return new Calendar(serviceId, monday, tuesday, wednesday, thursday, friday, saturday,
                    sunday, startDate, endDate);
        }
    }
}