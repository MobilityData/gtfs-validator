package org.mobilitydata.gtfsvalidator.web.service.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class StorageHelperTest {

  @MockBean private Storage storage;
  @Captor ArgumentCaptor<BlobInfo> blobInfoCaptor;
  @Captor ArgumentCaptor<byte[]> byteArrayCaptor;
  @Captor ArgumentCaptor<BlobId> blobIdCaptor;

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testGetJobInfoPath() {
    assert StorageHelper.getJobInfoPath("123").equals("123/" + StorageHelper.JOB_FILENAME);
  }

  @Test
  public void testSaveJobMetadata() throws Exception {

    StorageHelper storageHelper = new StorageHelper(storage, null);

    JobMetadata jobMetadata = new JobMetadata(UUID.randomUUID().toString(), "US");

    storageHelper.saveJobMetadata(jobMetadata);

    verify(storage, times(1)).create(blobInfoCaptor.capture(), byteArrayCaptor.capture());
    BlobInfo blobInfo = blobInfoCaptor.getValue();
    byte[] bytes = byteArrayCaptor.getValue();
    String expectedPath = jobMetadata.getJobId() + "/" + StorageHelper.JOB_FILENAME;
    BlobInfo expectedBlobInfo =
        BlobInfo.newBuilder(BlobId.of(StorageHelper.JOB_INFO_BUCKET_NAME, expectedPath))
            .setContentType("application/json")
            .build();
    byte[] expectedBytes = mapper.writeValueAsString(jobMetadata).getBytes();
    assertArrayEquals(expectedBytes, bytes);
    assertEquals(expectedBlobInfo, blobInfo);
  }

  @Test
  public void testGetJobMetadata() throws Exception {

    StorageHelper storageHelper = new StorageHelper(storage, null);

    String testJobId = UUID.randomUUID().toString();
    JobMetadata expectedJobMetadata = new JobMetadata(testJobId, "US");
    String expectedJson = mapper.writeValueAsString(expectedJobMetadata);

    Blob mockBlob = mock(Blob.class);
    when(storage.get(any(BlobId.class))).thenReturn(mockBlob);
    when(mockBlob.getContent()).thenReturn(expectedJson.getBytes());

    JobMetadata actualJobMetadata = storageHelper.getJobMetadata(testJobId);

    verify(storage, times(1)).get(blobIdCaptor.capture());
    BlobId expectedBlobId =
        BlobId.of(StorageHelper.JOB_INFO_BUCKET_NAME, StorageHelper.getJobInfoPath(testJobId));
    assertEquals(expectedBlobId, blobIdCaptor.getValue());

    assertEquals(expectedJson, mapper.writeValueAsString(actualJobMetadata));
  }

  @Test
  public void testGetJobMetadataStorageExceptionShouldReturnDefault() throws Exception {

    StorageHelper storageHelper = new StorageHelper(storage, null);

    String testJobId = UUID.randomUUID().toString();

    // should return default JobMetadata with jobId only
    JobMetadata expectedJobMetadata = new JobMetadata(testJobId, "");
    String expectedJson = mapper.writeValueAsString(expectedJobMetadata);

    when(storage.get(any(BlobId.class))).thenThrow(StorageException.class);

    JobMetadata actualJobMetadata = storageHelper.getJobMetadata(testJobId);

    assertEquals(expectedJson, mapper.writeValueAsString(actualJobMetadata));
  }
}
