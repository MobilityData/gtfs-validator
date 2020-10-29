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

package org.mobilitydata.gtfsvalidator.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceManagerTest {

    @Test
    void initializeConfigShouldCreateDefaultConfigViaBuilder() throws Exception {
        final ServiceManager underTest = new ServiceManager();
        final String mockExecParamAsJsonString = "{\n" +
                "  \"input\": \"mbta-dataset.zip\",\n" +
                "\"abort_on_error\": false\n" +
                "}";
        assertNull(underTest.initializeConfig(mockExecParamAsJsonString));
    }

    @Test
    void initializeConfigShouldThrowExceptionIfConfigFileNotProvided() {
        final ServiceManager underTest = new ServiceManager();
        final Exception exception = assertThrows(IOException.class,
                () -> underTest.initializeConfig(null));
        assertEquals("Configuration file not provided", exception.getMessage());
    }

    @Test
    void isConfigInitializedShouldReturnTrueIfConfigFileIsProvided() throws Exception {
        final ServiceManager underTest = new ServiceManager();
        final String mockExecParamAsJsonString = "{\n" +
                "  \"input\": \"mbta-dataset.zip\",\n" +
                "\"abort_on_error\": false\n" +
                "}";
        underTest.initializeConfig(mockExecParamAsJsonString);
        assertTrue(underTest.isConfigInitialized());
    }

    @Test
    void isConfigInitializedShouldReturnFalseIfConfigIsNotInitiated() {
        final ServiceManager underTest = new ServiceManager();
        assertFalse(underTest.isConfigInitialized());
    }

    @Test
    void isConfigInitializedShouldReturnFalseIfConfigFileIsNotProvided() {
        final ServiceManager underTest = new ServiceManager();
        assertThrows(IOException.class, () -> underTest.initializeConfig(null));
        assertFalse(underTest.isConfigInitialized());
    }

    @Test
    void validateFeedShouldThrowErrorIfDefaultConfigIsNotInitialized() {
        final ServiceManager underTest = new ServiceManager();
        assertThrows(IOException.class, underTest::runValidator);
    }
}
