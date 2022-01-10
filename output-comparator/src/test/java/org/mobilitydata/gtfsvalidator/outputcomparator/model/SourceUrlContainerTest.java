/*
 * Copyright 2021 Google LLC, MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.model;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SourceUrlContainerTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private SourceUrlContainer createUrlFile(String jsonString) throws IOException {
    if (!Files.exists(tmpDir.getRoot().toPath().resolve("metadata"))) {
      tmpDir.newFolder("metadata");
    }
    File urlFile = tmpDir.newFile("gtfs_latest_versions.json");
    Files.write(urlFile.toPath(), jsonString.getBytes(StandardCharsets.UTF_8));
    return new SourceUrlContainer(urlFile.toPath());
  }

  @Test
  public void urlContainerShouldReflectFileContent() throws IOException {
    SourceUrlContainer urlContainer =
        createUrlFile(
            "{\"source-id-1\":\"url to source id 1\",\"source-id-2\":\"url to source id 2\"}");
    assertThat(urlContainer.getUrlForSourceId("source-id-1").matches("url to source id 1"));
    assertThat(urlContainer.getUrlForSourceId("source-id-1").matches("url to source id 2"));
  }
}
