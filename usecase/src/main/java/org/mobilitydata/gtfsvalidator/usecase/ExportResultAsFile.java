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

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;

public class ExportResultAsFile {
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final GtfsDataRepository gtfsDataRepo;
    private final Logger logger;

    public ExportResultAsFile(final ValidationResultRepository resultRepo,
                              final ExecParamRepository execParamRepo,
                              final GtfsDataRepository gtfsDataRepo,
                              final Logger logger) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.gtfsDataRepo = gtfsDataRepo;
        this.logger = logger;
    }

    public void execute() throws IOException {

        if (Boolean.parseBoolean(execParamRepo.getExecParamValue(execParamRepo.PROTO_KEY))) {
            logger.info("-p provided, exporting results as proto");
        } else {
            logger.info("Results are exported as JSON by default");
        }

        logger.info("Exporting validation repo content:" + resultRepo.getAll());

        final String outputPath = execParamRepo.getExecParamValue(execParamRepo.OUTPUT_KEY);
        final boolean asProto = Boolean.parseBoolean(execParamRepo.getExecParamValue(execParamRepo.PROTO_KEY));

        NoticeExporter exporter = resultRepo.getExporter(
                asProto,
                outputPath,
                gtfsDataRepo.getFeedPublisherName());

        exporter.exportBegin();

        for (Notice notice : resultRepo.getAll()) {
            notice.export(exporter);
        }
        exporter.exportEnd();
    }
}
