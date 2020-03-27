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

public class ExportResultAsFile {

    private final ValidationResultRepository resultRepo;
    private final String outputPath;
    private final boolean asProto;

    public ExportResultAsFile(final ValidationResultRepository resultRepo,
                              final String outputPath,
                              final boolean asProto) {
        this.resultRepo = resultRepo;
        this.outputPath = outputPath;
        this.asProto = asProto;
    }

    public void execute() throws IOException {
        ValidationResultRepository.NoticeExporter exporter = resultRepo.getExporter(asProto, outputPath);

        exporter.exportBegin();

        for (Notice notice : resultRepo.getAll()) {
            notice.export(exporter);
        }

        exporter.exportEnd();
    }
}