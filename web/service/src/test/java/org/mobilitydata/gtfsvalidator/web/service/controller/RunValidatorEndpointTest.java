package org.mobilitydata.gtfsvalidator.web.service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import org.apache.commons.codec.binary.Base64;
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
  @MockBean private ValidationHandler handler;
  private final ObjectMapper mapper = new ObjectMapper();

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
