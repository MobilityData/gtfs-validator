package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.web.service.util.StorageHelper;
import org.mobilitydata.gtfsvalidator.web.service.util.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ValidationController.class)
public class VersionEndpointTest {
  @Autowired private MockMvc mockMvc;
  @MockBean private StorageHelper storageHelper;
  // must be mocked or application context fails to load correctly
  @MockBean private ValidationHandler handler;
  @MockBean private VersionResolver versionResolver;

  private final String newVersion = "myVersion";

  @Test
  public void testVersionEndpoint() throws Exception {
    doReturn(Optional.of(newVersion)).when(versionResolver).resolveCurrentVersion();
    mockMvc
        .perform(MockMvcRequestBuilders.get("/version").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.version").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(newVersion));
  }

  @Test
  public void testFailedVersionEndpoint() throws Exception {
    doThrow(new IOException()).when(versionResolver).resolveCurrentVersion();
    mockMvc
        .perform(MockMvcRequestBuilders.get("/version").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
  }

  @Test
  public void testVersionNotFound() throws Exception {
    doReturn(Optional.empty()).when(versionResolver).resolveCurrentVersion();
    mockMvc
        .perform(MockMvcRequestBuilders.get("/version").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
  }
}
