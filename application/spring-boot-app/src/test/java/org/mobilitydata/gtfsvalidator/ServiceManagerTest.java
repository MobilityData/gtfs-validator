/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceManagerTest {

    @Test
    void initConfigShouldCreateDefaultConfigViaBuilder() throws Exception {
        final ServiceManager underTest = new ServiceManager();
        final String mockExecParamAsJsonString = "{\n" +
                "  \"input\": \"mbta-dataset.zip\",\n" +
                "\"abort_on_error\": false\n" +
                "}";
        assertNull(underTest.initConfig(mockExecParamAsJsonString));
    }

    @Test
    void initConfigShouldThrowExceptionIfConfigFileNotProvided() {
        final ServiceManager underTest = new ServiceManager();
        final Exception exception = assertThrows(IOException.class, () -> underTest.initConfig(null));
        assertEquals("Configuration file not provided", exception.getMessage());
    }

    @Test
    void isConfigInitShouldReturnTrueIfConfigFileIsProvided() throws Exception {
        final ServiceManager underTest = new ServiceManager();
        final String mockExecParamAsJsonString = "{\n" +
                "  \"input\": \"mbta-dataset.zip\",\n" +
                "\"abort_on_error\": false\n" +
                "}";
        underTest.initConfig(mockExecParamAsJsonString);
        assertTrue(underTest.isConfigInit());
    }

    @Test
    void isConfigInitShouldReturnFalseIfConfigFileIsNotInitialized() {
        final ServiceManager underTest = new ServiceManager();
        assertFalse(underTest.isConfigInit());
    }

    @Test
    void isConfigInitShouldReturnFalseIfConfigFileIsNotProvided() {
        final ServiceManager underTest = new ServiceManager();
        assertThrows(IOException.class, () -> underTest.initConfig(null));
        assertFalse(underTest.isConfigInit());
    }

    @Test
    void validateFeedShouldThrowErrorIfDefaultConfigIsNotInitialized() {
        final ServiceManager underTest = new ServiceManager();
        assertThrows(IOException.class, underTest::runValidator);
    }
}
