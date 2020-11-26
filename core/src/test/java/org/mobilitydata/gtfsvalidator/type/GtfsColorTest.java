/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
