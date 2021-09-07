/*
 * Copyright 2021 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.springboot;

import static org.mobilitydata.gtfsvalidator.springboot.GtfsValidatorController.VALIDATION_REPORT_BUCKET_NAME_ENV_VAR;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringJUnit4ClassRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GtfsValidatorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Test
  public void malformedUrl_badRequest() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/?url=malformed url&country_code=us"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();
  }

  @Test
  public void noGtfsInput_badRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/?url=http://webapps.thebus.org/transitdata/Production/google_transit&country_code=us"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn();
  }

  @Test
  public void validationReportBucketNotSet_preconditionFailed() throws Exception {
    environmentVariables.clear(VALIDATION_REPORT_BUCKET_NAME_ENV_VAR);
    environmentVariables.set("TYPE", "service_account");
    environmentVariables.set("PROJECT_ID", "");
    environmentVariables.set("PRIVATE_KEY_ID", "");
    environmentVariables.set("PRIVATE_KEY", "a private key for test");
    environmentVariables.set("CLIENT_EMAIL", "");
    environmentVariables.set("CLIENT_ID", "");
    environmentVariables.set("AUTH_URI", "");
    environmentVariables.set("TOKEN_URI", "");
    environmentVariables.set("AUTH_PROVIDER_X509_CERT_URL", "");
    environmentVariables.set("CLIENT_X509_CERT_URL", "");

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/?url=http://webapps.thebus.org/transitdata/Production/google_transit.zip&country_code=us"))
        .andExpect(MockMvcResultMatchers.status().isPreconditionFailed())
        .andReturn();
  }

  @Test
  public void unauthorizedPushToGoogleCloudStorage_unauthorized() throws Exception {
    environmentVariables.set(VALIDATION_REPORT_BUCKET_NAME_ENV_VAR, "gtfs-validator-test-reports");
    environmentVariables.clear("TYPE");
    environmentVariables.clear("PROJECT_ID");
    environmentVariables.clear("PRIVATE_KEY_ID");
    environmentVariables.clear("PRIVATE_KEY");
    environmentVariables.clear("CLIENT_EMAIL");
    environmentVariables.clear("CLIENT_ID");
    environmentVariables.clear("AUTH_URI");
    environmentVariables.clear("TOKEN_URI");
    environmentVariables.clear("AUTH_PROVIDER_X509_CERT_URL");
    environmentVariables.clear("CLIENT_X509_CERT_URL");

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/?url=http://webapps.thebus.org/transitdata/Production/google_transit.zip&country_code=us"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andReturn();
  }
}
