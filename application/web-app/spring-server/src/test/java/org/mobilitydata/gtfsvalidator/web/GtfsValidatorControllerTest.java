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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class GtfsValidatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceManager mockServiceManager;

    // FIXME: Update method test postmapping
//    @Test
//    void initConfigShouldCallServiceManagerInitConfigMethod() throws Exception {
//        when(mockServiceManager.initializeConfig("jsonAsString")).thenReturn(null);
//        mockMvc.perform(MockMvcRequestBuilders.post("/actions/initialize/")
//                .contentType(MediaType.ALL))
//                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
////
//        verify(mockServiceManager, times(1))
//                .initializeConfig(ArgumentMatchers.eq("jsonAsString"));
//        verifyNoMoreInteractions(mockServiceManager);
//    }

    @Test
    void validateFeedShouldCallServiceManagerValidateFeedMethod() throws Exception {
        when(mockServiceManager.runValidator()).thenReturn("validation status");

        mockMvc.perform(MockMvcRequestBuilders.get("/actions/runvalidator/"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        verify(mockServiceManager, times(1)).runValidator();
        verifyNoMoreInteractions(mockServiceManager);
    }

    @Test
    void openReportShouldCallServiceManageOpenReportMethod() throws Exception {
        when(mockServiceManager.displayReport()).thenReturn("no content");

        mockMvc.perform(MockMvcRequestBuilders.get("/actions/displayreport/"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        verify(mockServiceManager, times(1)).displayReport();
        verifyNoMoreInteractions(mockServiceManager);
    }
}
