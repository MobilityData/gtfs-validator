package org.mobilitydata.gtfsvalidator.type;

/**
 * Represents a GTFS color - a hexadecimal integer from 000000 to FFFFFF.
 */
public class GtfsColor {
    private final int rgb;

    private GtfsColor(int rgb) {
        this.rgb = rgb;
    }

    public static GtfsColor fromString(String s) {
        if (s.length() != 6) {
            throw new IllegalArgumentException("Color must have 6 digits: " + s);
        }
        return fromInt(Integer.parseInt(s, 16));
    }

    public static GtfsColor fromInt(int i) {
        return new GtfsColor(i);
    }

    public int getRgb() {
        return rgb;
    }

    @Override
    public String toString() {
        return toHtmlColor();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof GtfsColor) {
            return this.getRgb() == ((GtfsColor) anObject).getRgb();
        }
        return false;
    }

    public String toHtmlColor() {
        return String.format("#%06X", rgb);
    }

    /**
     * Returns the luma [0, 255] of the color based on Rec 601 standard.
     * It corresponds to a human's perception of the color's brightness.
     * See https://en.wikipedia.org/wiki/Luma_(video).
     */
    public int rec601Luma() {
        int r = (rgb & 0xff0000) >> 16;
        int g = (rgb & 0x00ff00) >> 8;
        int b = (rgb & 0x0000ff);
        return (int) (0.30 * r + 0.59 * g + 0.11 * b);
    }
}
