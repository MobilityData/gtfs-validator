package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public class Frequency {

    @NotNull
    private final String tripId;

    @NotNull
    private final LocalDateTime startTime;

    @NotNull
    private final LocalDateTime endTime;

    private final int headwaySecs;
    private final boolean exactTimes;

    private Frequency(@NotNull String tripId,
                      @NotNull LocalDateTime startTime,
                      @NotNull LocalDateTime endTime,
                      int headwaySecs,
                      boolean exactTimes) {
        this.tripId = tripId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headwaySecs = headwaySecs;
        this.exactTimes = exactTimes;
    }

    @NotNull
    public String getTripId() {
        return tripId;
    }

    @NotNull
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @NotNull
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getHeadwaySecs() {
        return headwaySecs;
    }

    public boolean isExactTimes() {
        return exactTimes;
    }

    public static class FrequencyBuilder {

        @NotNull
        private String tripId;

        @NotNull
        private LocalDateTime startTime;

        @NotNull
        private LocalDateTime endTime;

        private int headwaySecs;
        private boolean exactTimes;


        public FrequencyBuilder(@NotNull String tripId,
                                @NotNull LocalDateTime startTime,
                                @NotNull LocalDateTime endTime,
                                int headwaySecs) {
            this.tripId = tripId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.headwaySecs = headwaySecs;
        }

        public FrequencyBuilder tripId(@NotNull String tripId) {
            this.tripId = tripId;
            return this;
        }

        public FrequencyBuilder startTime(@NotNull LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public FrequencyBuilder endTime(@NotNull LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public FrequencyBuilder headwaySecs(int headwaySecs) {
            this.headwaySecs = headwaySecs;
            return this;
        }

        public FrequencyBuilder tripId(@Nullable Integer exactTimes) {
            this.exactTimes = Objects.equals(exactTimes, 1);
            return this;
            // TODO: implement solution to handle unexpected int value here, i.e 2.
        }

        public Frequency build() {
            return new Frequency(tripId, startTime, endTime, headwaySecs, exactTimes);
        }


    }
}
