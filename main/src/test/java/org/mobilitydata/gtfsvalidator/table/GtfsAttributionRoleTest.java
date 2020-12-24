package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class GtfsAttributionRoleTest {

    @Test
    public void shouldReturnEnumValue() {
        assertThat(GtfsAttributionRole.forNumber(0)).isEqualTo(GtfsAttributionRole.NOT_ASSIGNED);
        assertThat(GtfsAttributionRole.forNumber(1)).isEqualTo(GtfsAttributionRole.ASSIGNED);
    }

    @Test
    public void shouldBeInterpretedAsNull() {
        assertThat(GtfsAttributionRole.forNumber(-1)).isNull();
        assertThat(GtfsAttributionRole.forNumber(3)).isNull();
    }
}
