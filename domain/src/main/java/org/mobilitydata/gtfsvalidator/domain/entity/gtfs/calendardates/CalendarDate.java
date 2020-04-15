package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

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
            return this;
        }

        public CalendarDate build() {
            if (serviceId == null) {
                throw new IllegalArgumentException("field service_id in calendar_dates.txt can not be null");
            }
            if (date == null) {
                throw new IllegalArgumentException("field date in calendar_dates.txt can not be null");
            }
            if (exceptionType == null) {
                throw new IllegalArgumentException("unexpected value found for field exception_type of " +
                        "calendar_dates.txt");
            }
            return new CalendarDate(serviceId, date, exceptionType);
        }
    }
}