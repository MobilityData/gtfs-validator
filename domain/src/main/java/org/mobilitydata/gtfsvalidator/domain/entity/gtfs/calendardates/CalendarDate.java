package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.jetbrains.annotations.NotNull;

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

        public CalendarDateBuilder exceptionType(@NotNull final ExceptionType exceptionType) {
            this.exceptionType = exceptionType;
            return this;
        }

        public CalendarDate build() {
            return new CalendarDate(serviceId, date, exceptionType);
        }
    }
}