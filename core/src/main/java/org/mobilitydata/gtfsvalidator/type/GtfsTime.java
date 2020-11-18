package org.mobilitydata.gtfsvalidator.type;

/**
 * Represents GTFS time.
 * <p>
 * The time is measured from "noon minus 12h" of the service day (effectively midnight except for days on which daylight
 * savings time changes occur).
 */
public class GtfsTime implements Comparable<GtfsTime> {
    private final int secondsSinceMidnight;

    private GtfsTime(int secondsSinceMidnight) {
        this.secondsSinceMidnight = secondsSinceMidnight;
    }

    public static GtfsTime fromHourMinuteSecond(int hour, int minute, int second) {
        if (hour < 0) {
            throw new IllegalArgumentException("Negative hour: " + hour);
        }
        if (minute < 0 || minute >= 60) {
            throw new IllegalArgumentException("Invalid minute: " + minute);
        }
        if (second < 0 || second >= 60) {
            throw new IllegalArgumentException("Invalid second: " + second);
        }
        return new GtfsTime(hour * 3600 + minute * 60 + second);
    }

    public static GtfsTime fromSecondsSinceMidnight(int secondsSinceMidnight) {
        return new GtfsTime(secondsSinceMidnight);
    }

    public static GtfsTime fromString(String hhmmss) {
        int length = hhmmss.length();
        if (length != 8 && length != 7) {
            throw new IllegalArgumentException("Time must have HH:MM:SS or H:MM:SS format: " + hhmmss);
        }
        int hour, minute, second;
        try {
            hour = Integer.parseInt(hhmmss.substring(0, length - 6));
            minute = Integer.parseInt(hhmmss.substring(length - 5, length - 3));
            second = Integer.parseInt(hhmmss.substring(length - 2));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Time must have HH:MM:SS or H:MM:SS format: " + hhmmss);
        }
        return fromHourMinuteSecond(hour, minute, second);
    }

    public int getSecondsSinceMidnight() {
        return secondsSinceMidnight;
    }

    public int getHour() {
        return secondsSinceMidnight / 3600;
    }

    public int getMinute() {
        return (secondsSinceMidnight / 60) % 60;
    }

    public int getSecond() {
        return secondsSinceMidnight % 60;
    }

    public String toHHMMSS() {
        return String.format("%02d:%02d:%02d", getHour(), getMinute(), getSecond());
    }

    @Override
    public int compareTo(GtfsTime other) {
        return Integer.compare(secondsSinceMidnight, other.secondsSinceMidnight);
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof GtfsTime) {
            return compareTo((GtfsTime) anObject) == 0;
        }
        return false;
    }

    public boolean isAfter(GtfsTime other) {
        return secondsSinceMidnight > other.secondsSinceMidnight;
    }

    public boolean isBefore(GtfsTime other) {
        return secondsSinceMidnight < other.secondsSinceMidnight;
    }
}
