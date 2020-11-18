package org.mobilitydata.gtfsvalidator.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsDateTest {
    @Test
    public void fromString() {
        assertThat(GtfsDate.fromString("20200901").getLocalDate()).isEqualTo(LocalDate.of(2020, 9, 1));
        assertThat(GtfsDate.fromString("19970103").getLocalDate()).isEqualTo(LocalDate.of(1997, 1, 3));

        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("0"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("qwerty"));
        assertThrows(IllegalArgumentException.class, () -> GtfsDate.fromString("today"));
    }
}
