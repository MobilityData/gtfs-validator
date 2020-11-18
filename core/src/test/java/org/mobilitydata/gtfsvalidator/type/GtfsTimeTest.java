package org.mobilitydata.gtfsvalidator.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsTimeTest {
    @Test
    public void fromString() {
        assertThat(GtfsTime.fromString("12:20:30").getSecondsSinceMidnight()).isEqualTo(12 * 3600 + 20 * 60 + 30);
        assertThat(GtfsTime.fromString("2:34:12").getSecondsSinceMidnight()).isEqualTo(2 * 3600 + 34 * 60 + 12);

        assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("0"));
        assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("qwerty"));
        assertThrows(IllegalArgumentException.class, () -> GtfsTime.fromString("midnight"));
    }
}
