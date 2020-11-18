package org.mobilitydata.gtfsvalidator.type;

import java.time.LocalDate;

/**
 * Represents GTFS date.
 */
public class GtfsDate implements Comparable<GtfsDate> {
    private final LocalDate localDate;

    private GtfsDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public static GtfsDate fromLocalDate(LocalDate localDate) {
        return new GtfsDate(localDate);
    }

    public static GtfsDate fromEpochDay(long epochDay) {
        return new GtfsDate(LocalDate.ofEpochDay(epochDay));
    }

    public static GtfsDate fromString(String yyyymmdd) {
        if (yyyymmdd.length() != 8) {
            throw new IllegalArgumentException("Date must have YYYYMMDD format: " + yyyymmdd);
        }
        int year, month, day;
        try {
            year = Integer.parseInt(yyyymmdd.substring(0, 4));
            month = Integer.parseInt(yyyymmdd.substring(4, 6));
            day = Integer.parseInt(yyyymmdd.substring(6));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Date must have YYYYMMDD format: " + yyyymmdd);
        }
        return new GtfsDate(LocalDate.of(year, month, day));
    }

    public int getYear() {
        return localDate.getYear();
    }

    public int getMonth() {
        return localDate.getMonthValue();
    }

    public int getDay() {
        return localDate.getDayOfMonth();
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public long toEpochDay() {
        return localDate.toEpochDay();
    }

    public String toYYYYMMDD() {
        return String.format("%04d%02d%02d", getYear(), getMonth(), getDay());
    }

    @Override
    public int compareTo(GtfsDate other) {
        return localDate.compareTo(other.localDate);
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof GtfsDate) {
            return compareTo((GtfsDate) anObject) == 0;
        }
        return false;
    }

    public boolean isAfter(GtfsDate other) {
        return localDate.isAfter(other.localDate);
    }

    public boolean isBefore(GtfsDate other) {
        return localDate.isBefore(other.localDate);
    }

}
