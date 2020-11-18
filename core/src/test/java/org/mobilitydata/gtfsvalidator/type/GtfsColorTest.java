package org.mobilitydata.gtfsvalidator.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsColorTest {
    @Test
    public void fromString() {
        assertThat(GtfsColor.fromString("00ff00").getRgb()).isEqualTo(0x00ff00);
        assertThat(GtfsColor.fromString("cc0012").getRgb()).isEqualTo(0xcc0012);

        assertThrows(IllegalArgumentException.class, () -> GtfsColor.fromString("0"));
        assertThrows(IllegalArgumentException.class, () -> GtfsColor.fromString("qwerty"));
        assertThrows(IllegalArgumentException.class, () -> GtfsColor.fromString("green"));
    }

    @Test
    public void toHtmlColor() {
        assertThat(GtfsColor.fromInt(0x11ff00).toHtmlColor()).isEqualTo("#11FF00");
    }
}
