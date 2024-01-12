package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.util.VersionResolver;
import org.mobilitydata.gtfsvalidator.web.service.util.JobMetadata;
import org.mobilitydata.gtfsvalidator.web.service.util.StorageHelper;
import org.mobilitydata.gtfsvalidator.web.service.util.ValidationHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ValidationController.class)
public class CreateJobEndpointTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private StorageHelper storageHelper;

  // must be mocked or application context fails to load correctly
  @MockBean private ValidationHandler handler;

  @Captor ArgumentCaptor<JobMetadata> jobMetadataCaptor;

  @MockBean private VersionResolver versionResolver;

  private final ObjectMapper mapper = new ObjectMapper();
  private String testJobId;
  private String testUploadUrl;

  private final String VALIDATOR_TEST_VERSION = "1.0.0";

  private void makeCreateJobRequestAndCheckResult(
      CreateJobRequest request, String expectedJobId, String expectedUploadUrl) throws Exception {
    var json = mapper.writeValueAsString(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/create-job")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.jobId").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.jobId").value(expectedJobId))
        .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(expectedUploadUrl));
  }

  @BeforeEach
  public void setUp() throws IOException {
    testJobId = "123";
    testUploadUrl = "https://gcs.io/bucket/123";
    doReturn(testJobId).when(storageHelper).createNewJobId();
    doReturn(Optional.of(VALIDATOR_TEST_VERSION)).when(versionResolver).resolveCurrentVersion();
  }

  @Test
  public void createJobWithNoUrlNoCountryCode() throws Exception {
    doReturn(new URL(testUploadUrl)).when(storageHelper).generateUniqueUploadUrl(testJobId);
    var request = new CreateJobRequest(null, null);

    makeCreateJobRequestAndCheckResult(request, testJobId, testUploadUrl);

    // should not call saveJobMetadata
    verify(storageHelper, times(0)).saveJobMetadata(any(JobMetadata.class));
    // should not call saveJobFileFromUrl
    verify(storageHelper, times(0)).saveJobFileFromUrl(anyString(), anyString(), anyString());
  }

  @Test
  public void createJobWithCountryCodeButNoUrl() throws Exception {
    doReturn(new URL(testUploadUrl)).when(storageHelper).generateUniqueUploadUrl(testJobId);

    var request = new CreateJobRequest("US", null);

    makeCreateJobRequestAndCheckResult(request, testJobId, testUploadUrl);

    // should save job metadata with country code
    verify(storageHelper, times(1)).saveJobMetadata(jobMetadataCaptor.capture());
    var jobMetadata = jobMetadataCaptor.getValue();
    assert jobMetadata.getJobId().equals(testJobId);
    assert jobMetadata.getCountryCode().equals("US");

    // should not call saveJobFileFromUrl
    verify(storageHelper, times(0)).saveJobFileFromUrl(anyString(), anyString(), anyString());
  }

  @Test
  public void createJobWithUrlButNoCountryCode() throws Exception {
    doReturn(testJobId).when(storageHelper).createNewJobId();
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest(null, url);

    makeCreateJobRequestAndCheckResult(request, testJobId, null);

    // should not call saveJobMetadata
    verify(storageHelper, times(0)).saveJobMetadata(any(JobMetadata.class));
    // should saveJobFileFromUrl
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url, VALIDATOR_TEST_VERSION);
  }

  @Test
  public void createJobWithUrlAndCountryCode() throws Exception {
    doReturn(testJobId).when(storageHelper).createNewJobId();
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest("US", url);

    makeCreateJobRequestAndCheckResult(request, testJobId, null);

    // should save job metadata with country code
    verify(storageHelper, times(1)).saveJobMetadata(jobMetadataCaptor.capture());
    var jobMetadata = jobMetadataCaptor.getValue();
    // assert jobMetadata jobId is equal to expectedJobId and countryCode is equal to "US"
    assert jobMetadata.getJobId().equals(testJobId);
    assert jobMetadata.getCountryCode().equals("US");
    // should saveJobFileFromUrl
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url, VALIDATOR_TEST_VERSION);
  }

  @Test
  public void createJobShouldReturn500ErrorIfSaveMetadataThrowsException() throws Exception {
    doReturn(testJobId).when(storageHelper).createNewJobId();
    doThrow(new RuntimeException("test exception")).when(storageHelper).saveJobMetadata(any());
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest("US", url);
    var json = mapper.writeValueAsString(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/create-job")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
  }

  @Test
  public void createJobShouldReturn500ErrorIfSaveJobFileFromUrlThrowsException() throws Exception {
    doReturn(testJobId).when(storageHelper).createNewJobId();
    doThrow(new RuntimeException("test exception"))
        .when(storageHelper)
        .saveJobFileFromUrl(anyString(), anyString(), anyString());
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest("US", url);
    var json = mapper.writeValueAsString(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/create-job")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
  }
}
