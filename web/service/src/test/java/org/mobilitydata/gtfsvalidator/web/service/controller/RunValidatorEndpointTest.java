package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.web.service.util.JobMetadata;
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
public class RunValidatorEndpointTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private StorageHelper storageHelper;
  @MockBean private ValidationHandler validationHandler;
  private final ObjectMapper mapper = new ObjectMapper();
  private String testJobId;
  JobMetadata jobMetaData;
  File mockFeedFile;
  Path mockOutputPath;
  File mockOutputPathToFile;
  GoogleCloudPubsubMessage pubSubMessage;

  @BeforeEach
  public void setUp() throws Exception {
    testJobId = "123";
    jobMetaData = new JobMetadata(testJobId, "US");
    mockFeedFile = mock(File.class);
    mockOutputPath = mock(Path.class);
    mockOutputPathToFile = mock(File.class);

    var filePath = testJobId + "/feedFile.zip";
    var msgText = "{\"name\": \"" + filePath + "\"}";
    var innerMsg = new GoogleCloudPubsubMessage.Message();
    innerMsg.setData(Base64.encodeBase64String(msgText.getBytes()));
    pubSubMessage = new GoogleCloudPubsubMessage();
    pubSubMessage.setMessage(innerMsg);

    doReturn(jobMetaData).when(storageHelper).getJobMetadata(testJobId);
    doReturn(mockOutputPath).when(storageHelper).createOutputFolderForJob(testJobId);
    doReturn(mockOutputPathToFile).when(mockOutputPath).toFile();
  }

  public boolean executionResultIs(String result) throws Exception {
    String executionResultJson = Files.readString(Paths.get("execution_result.json"));
    JSONObject executionResult = new JSONObject(executionResultJson);
    String expectedStatus = executionResult.getString("status");
    return result.equals(expectedStatus);
  }

  public boolean executionResultIsError() throws Exception {
    return executionResultIs("error");
  }

  public boolean executionResultIsSuccess() throws Exception {
    return executionResultIs("success");
  }

  @Test
  public void runValidatorSuccess() throws Exception {
    doReturn(mockFeedFile)
        .when(storageHelper)
        .downloadFeedFileFromStorage(anyString(), anyString());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/run-validator")
                .content(mapper.writeValueAsString(pubSubMessage))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());

    assertTrue(executionResultIsSuccess());

    // verify that the validationHandler is called with the downloaded feed file, output path, and
    // country code
    verify(validationHandler, times(1))
        .validateFeed(mockFeedFile, mockOutputPath, jobMetaData.getCountryCode());

    // verify that the validation output files are uploaded to storage
    verify(storageHelper, times(1)).uploadFilesToStorage(testJobId, mockOutputPath);

    // verify that the temp files and directory are deleted
    verify(mockFeedFile, times(1)).delete();
    verify(mockOutputPathToFile, times(1)).delete();
  }

  @Test
  public void runValidatorStorageDownloadFailure() throws Exception {
    doThrow(IOException.class)
        .when(storageHelper)
        .downloadFeedFileFromStorage(anyString(), anyString());

    mockMvc.perform(
        MockMvcRequestBuilders.post("/run-validator")
            .content(mapper.writeValueAsString(pubSubMessage))
            .contentType(MediaType.APPLICATION_JSON));

    assertTrue(executionResultIsError());

    // should not have attempted validation
    verify(validationHandler, times(0)).validateFeed(any(File.class), any(Path.class), anyString());

    // should not have attempted uploading to storage
    verify(storageHelper, times(0)).uploadFilesToStorage(anyString(), any(Path.class));

    // should not have attempted deleting temp files
    verify(mockFeedFile, times(0)).delete();
    verify(mockOutputPathToFile, times(0)).delete();
  }

  @Test
  public void runValidatorValidateFeedFailure() throws Exception {
    doThrow(new Exception())
        .when(validationHandler)
        .validateFeed(any(File.class), any(Path.class), anyString());

    doReturn(mockFeedFile)
        .when(storageHelper)
        .downloadFeedFileFromStorage(anyString(), anyString());

    mockMvc.perform(
        MockMvcRequestBuilders.post("/run-validator")
            .content(mapper.writeValueAsString(pubSubMessage))
            .contentType(MediaType.APPLICATION_JSON));

    assertTrue(executionResultIsError());

    // should not upload to storage
    verify(storageHelper, times(0)).uploadFilesToStorage(anyString(), any(Path.class));

    // should delete temp files when an exception is thrown
    verify(mockFeedFile, times(1)).delete();
    verify(mockOutputPathToFile, times(1)).delete();
  }
}
