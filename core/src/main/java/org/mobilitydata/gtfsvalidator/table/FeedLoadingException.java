/*
 * Copyright 2026 MobilityData
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

package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Thrown when one or more files present in the feed could not be loaded, for example because the
 * loader ran out of memory. Validation cannot continue, since the multi-file validators depending
 * on those files would run without their tables.
 *
 * <p>It is unchecked so that the loader's public signatures stay unchanged; {@code
 * ValidationRunner} catches it explicitly.
 *
 * <p>Files absent from the feed and files whose loader threw a {@link RuntimeException} are not
 * covered: they are treated as missing files and validation continues.
 */
public class FeedLoadingException extends RuntimeException {

  private final ImmutableList<String> failedFiles;

  public FeedLoadingException(List<String> failedFiles, Throwable cause) {
    super("Could not load " + String.join(", ", failedFiles), cause);
    this.failedFiles = ImmutableList.copyOf(failedFiles);
  }

  /** The names of the files that could not be loaded. */
  public ImmutableList<String> getFailedFiles() {
    return failedFiles;
  }
}
