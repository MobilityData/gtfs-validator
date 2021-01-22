/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Implements support for unarchived GTFS directories. */
public class GtfsUnarchivedInput extends GtfsInput {
  private final Set<String> filenames;
  private final Path directory;

  public GtfsUnarchivedInput(Path directory) throws IOException {
    this.directory = directory;
    try (Stream<Path> stream = Files.list(directory)) {
      this.filenames =
          stream
              .filter(Files::isRegularFile)
              .map(x -> x.getFileName().toString())
              .collect(Collectors.toSet());
    }
  }

  @Override
  public Set<String> getFilenames() {
    return Collections.unmodifiableSet(filenames);
  }

  @Override
  public InputStream getFile(String filename) throws IOException {
    return Files.newInputStream(directory.resolve(filename));
  }
}
