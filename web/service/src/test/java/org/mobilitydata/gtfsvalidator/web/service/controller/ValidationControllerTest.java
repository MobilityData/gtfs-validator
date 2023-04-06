package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ValidationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private StorageHelper storageHelper;
  @MockBean private ValidationHandler handler;

  @Captor ArgumentCaptor<JobMetadata> jobMetadataCaptor;
  @Captor ArgumentCaptor<File> feedFileCaptor;
  @Captor ArgumentCaptor<Path> outputPathCaptor;
  @Captor ArgumentCaptor<String> countryCodeCaptor;

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

    // should not call saveJobMetadata
    verify(storageHelper, times(0)).saveJobMetadata(any(JobMetadata.class));
    // should not call saveJobFileFromUrl
    verify(storageHelper, times(0)).saveJobFileFromUrl(anyString(), anyString());
  }

  @Test
  public void createJobWithCountryCodeButNoUrl() throws Exception {
    when(storageHelper.generateUniqueUploadUrl(testJobId)).thenReturn(new URL(testUploadUrl));

    var request = new CreateJobRequest("US", null);

    makeCreateJobRequestAndCheckResult(request, testJobId, testUploadUrl);

    // should save job metadata with country code
    verify(storageHelper, times(1)).saveJobMetadata(jobMetadataCaptor.capture());
    var jobMetadata = jobMetadataCaptor.getValue();
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

    // should not call saveJobMetadata
    verify(storageHelper, times(0)).saveJobMetadata(any(JobMetadata.class));
    // should saveJobFileFromUrl
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url);
  }

  @Test
  public void createJobWithUrlAndCountryCode() throws Exception {
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
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
    verify(storageHelper, times(1)).saveJobFileFromUrl(testJobId, url);
  }

  @Test
  public void createJobShouldReturn500ErrorIfSaveMetadataThrowsException() throws Exception {
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
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
    when(storageHelper.createNewJobId()).thenReturn(testJobId);
    doThrow(new RuntimeException("test exception"))
        .when(storageHelper)
        .saveJobFileFromUrl(any(), any());
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
  public void runValidator() throws Exception {

    var testJobId = "123";
    var filePath = testJobId + "/feedFile.zip";
    var msgText = "{\"name\": \"" + filePath + "\"}";
    var msg = new GoogleCloudPubsubMessage.Message();
    msg.setData(Base64.encodeBase64String(msgText.getBytes()));
    var gcpm = new GoogleCloudPubsubMessage();
    gcpm.setMessage(msg);

    var jobMetaData = new JobMetadata(testJobId, "US");
    File mockFeedFile = mock(File.class);
    Path mockOutputPath = mock(Path.class);
    File mockOutputPathToFile = mock(File.class);

    when(storageHelper.getJobMetadata(testJobId)).thenReturn(jobMetaData);
    when(storageHelper.downloadFeedFileFromStorage(anyString(), anyString()))
        .thenReturn(mockFeedFile);
    when(storageHelper.getOutputPathForJob(testJobId)).thenReturn(mockOutputPath);
    when(mockOutputPath.toFile()).thenReturn(mockOutputPathToFile);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/run-validator")
                .content(mapper.writeValueAsString(gcpm))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());

    // verify that the validationHandler is called with the downloaded feed file, output path, and
    // country code
    verify(handler, times(1))
        .validateFeed(mockFeedFile, mockOutputPath, jobMetaData.getCountryCode());

    // verify that the validation output files are uploaded to storage
    verify(storageHelper, times(1)).uploadFilesToStorage(testJobId, mockOutputPath);

    // verify that the temp files and directory are deleted
    verify(mockFeedFile, times(1)).delete();
    verify(mockOutputPathToFile, times(1)).delete();
  }
}
