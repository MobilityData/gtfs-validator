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
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;

public class ExportResultAsFile {
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;

    public ExportResultAsFile(final ValidationResultRepository resultRepo,
                              final ExecParamRepository execParamRepo) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
    }

    public void execute() throws IOException {
        final String outputPath = execParamRepo.getExecParamValue("output") != null
                ? System.getProperty("user.dir") + File.separator + execParamRepo.getExecParamValue("output")
                : System.getProperty("user.dir") + File.separator
                + execParamRepo.getExecParamDefaultValue("output");

        final String asProtoAsString = (execParamRepo.getExecParamValue("proto") != null) &&
                (!execParamRepo.getExecParamValue("proto").equals("false"))
                ? execParamRepo.getExecParamValue("proto")
                : execParamRepo.getExecParamDefaultValue("proto");

        final boolean asProto = Boolean.parseBoolean(asProtoAsString);

        ValidationResultRepository.NoticeExporter exporter = resultRepo.getExporter(asProto, outputPath);

        exporter.exportBegin();

        for (Notice notice : resultRepo.getAll()) {
            notice.export(exporter);
        }
        exporter.exportEnd();
    }
}