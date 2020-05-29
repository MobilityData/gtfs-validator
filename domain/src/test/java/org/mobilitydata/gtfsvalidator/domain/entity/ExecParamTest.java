/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecParamTest {
    private static final String KEY = "key";
    private static final String[] VALUE = new String[]{"value0", "value1", "value2"};

    @Test
    public void getValueShouldReturnValue() {
        final ExecParam underTest = new ExecParam(KEY, VALUE);
        assertEquals(List.of("value0", "value1", "value2"), underTest.getValue());
    }

    @Test
    public void getKeyShouldReturnKey() {
        final ExecParam underTest = new ExecParam(KEY, VALUE);
        assertEquals(KEY, underTest.getKey());
    }
}