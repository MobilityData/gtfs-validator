/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ExportResultAsFile {

    private final ValidationResultRepository resultRepo;
    private final String outputPath;

    // future use will also specify what kind of export as constructor parameter. Now supporting ony protobuf
    public ExportResultAsFile(final ValidationResultRepository resultRepo, final String outputPath) {
        this.resultRepo = resultRepo;
        this.outputPath = outputPath;
    }

    public void execute() throws IOException {
        ValidationResultRepository.NoticeExporter exporter = resultRepo.getExporter();

        for (Notice notice : resultRepo.getAll()) {
            OutputStream outStream = Files.newOutputStream(Paths.get(
                    outputPath + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + exporter.getExtension()));
            notice.export(exporter, outStream);
            outStream.close();
        }
    }
}