package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.web.service.util.JobMetadata;
import org.mobilitydata.gtfsvalidator.web.service.util.StorageHelper;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ValidationController.class)
public class ValidationControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private StorageHelper storageHelper;

  private final ObjectMapper mapper = new ObjectMapper();
  private String testJobId;
  private String testUploadUrl;

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
  public void setUp() {
    testJobId = "123";
    testUploadUrl = "https://gcs.io/bucket/123";
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
  }

  @Test
  public void createJobWithNoUrlNoCountryCode() throws Exception {
    when(storageHelper.generateUniqueUploadUrl(testJobId)).thenReturn(new URL(testUploadUrl));
    var request = new CreateJobRequest(null, null);

    makeCreateJobRequestAndCheckResult(request, testJobId, testUploadUrl);

    // should not call saveJobMetaData
    verify(storageHelper, times(0)).saveJobMetaData(any(JobMetadata.class));
    // should not call saveJobFileFromUrl
    verify(storageHelper, times(0)).saveJobFileFromUrl(anyString(), anyString());
  }

  @Test
  public void createJobWithCountryCodeButNoUrl() throws Exception {
    when(storageHelper.generateUniqueUploadUrl(testJobId)).thenReturn(new URL(testUploadUrl));

    var request = new CreateJobRequest("US", null);

    makeCreateJobRequestAndCheckResult(request, testJobId, testUploadUrl);

    // should save job metadata with country code
    ArgumentCaptor<JobMetadata> jobMetadataArgumentCaptor =
        ArgumentCaptor.forClass(JobMetadata.class);
    verify(storageHelper, times(1)).saveJobMetaData(jobMetadataArgumentCaptor.capture());
    var jobMetadata = jobMetadataArgumentCaptor.getValue();
    assert jobMetadata.getJobId().equals(testJobId);
    assert jobMetadata.getCountryCode().equals("US");

    // should not call saveJobFileFromUrl
    verify(storageHelper, times(0)).saveJobFileFromUrl(anyString(), anyString());
  }

  @Test
  public void createJobWithUrlButNoCountryCode() throws Exception {
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest(null, url);

    makeCreateJobRequestAndCheckResult(request, testJobId, null);

    // should not call saveJobMetaData
    verify(storageHelper, times(0)).saveJobMetaData(any(JobMetadata.class));
    // should saveJobFileFromUrl
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url);
  }

  @Test
  public void createJobWithUrlAndCountryCode() throws Exception {
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
    String url = "http://myfilehost.com/myfile.zip";
    var request = new CreateJobRequest("US", url);
    ArgumentCaptor<JobMetadata> jobMetadataArgumentCaptor =
        ArgumentCaptor.forClass(JobMetadata.class);

    makeCreateJobRequestAndCheckResult(request, testJobId, null);

    // should save job metadata with country code
    verify(storageHelper, times(1)).saveJobMetaData(jobMetadataArgumentCaptor.capture());
    var jobMetadata = jobMetadataArgumentCaptor.getValue();
    // assert jobMetaData jobId is equal to expectedJobId and countryCode is equal to "US"
    assert jobMetadata.getJobId().equals(testJobId);
    assert jobMetadata.getCountryCode().equals("US");
    // should saveJobFileFromUrl
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url);
  }

  @Test
  public void createJobShouldReturn500ErrorIfSaveMetaDataThrowsException() throws Exception {
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
    doThrow(new RuntimeException("test exception")).when(storageHelper).saveJobMetaData(any());
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
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
    doThrow(new RuntimeException("test exception")).when(storageHelper).saveJobFileFromUrl(any(), any());
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
