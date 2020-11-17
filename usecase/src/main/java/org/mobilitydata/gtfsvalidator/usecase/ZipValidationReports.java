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
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipValidationReports {
    public static final String PB_EXTENSION = ".pb";
    public static final String JSON_EXTENSION = ".json";
    public static final String ZIP_EXTENSION = ".zip";
    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;
    private final GtfsDataRepository gtfsDataRepo;
    private final Timestamp timestamp;
    private final Logger logger;

    public ZipValidationReports(final ValidationResultRepository resultRepo,
                                final ExecParamRepository execParamRepo,
                                final GtfsDataRepository gtfsDataRepo,
                                final Timestamp timestamp,
                                final Logger logger) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
        this.gtfsDataRepo = gtfsDataRepo;
        this.timestamp = timestamp;
        this.logger = logger;
    }

    public void execute() throws IOException {
        resultRepo.tempExport();
        String reportName = gtfsDataRepo.getFeedPublisherName();

        if (gtfsDataRepo.getAgencyCount() > 0) {
            if ((reportName.isEmpty() || reportName.isBlank())) {
                reportName = gtfsDataRepo.getAgencyAll().values().iterator().next().getAgencyName();
            } else {
                reportName = reportName + "__" + gtfsDataRepo.getAgencyAll().values().iterator().next().getAgencyName();
            }
        }

        final String finalPath =
                (execParamRepo.getExecParamValue(execParamRepo.OUTPUT_KEY) +
                        File.separator + reportName + "__" +
                        timestamp + ZIP_EXTENSION
                ).replaceAll("\\s", "_").replaceAll(":", "-");

        logger.info("Computed relative path for report folder: " + finalPath);

        final FileOutputStream outputStream = new FileOutputStream(finalPath);
        final ZipOutputStream zipOut = new ZipOutputStream(outputStream);
        int length;
        FileInputStream inputStream;
        ZipEntry zipEntry;
        System.out.println(System.getProperty("user.dir"));
        final List<Path> pathCollection =
                Files.walk(Paths.get("adapter/repository/in-memory-simple/src/main/resources"))
                        .collect(Collectors.toUnmodifiableList());
        final String fileExtension =
                Boolean.parseBoolean(execParamRepo.getExecParamValue(ExecParamRepository.PROTO_KEY)) ?
                        PB_EXTENSION :
                        JSON_EXTENSION;
        for (Path path : pathCollection) {
            if (path.toString().endsWith(fileExtension) && path.toString().contains("report")) {
                final File fileToZip = new File(path.toString());
                inputStream = new FileInputStream(fileToZip);
                zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                while ((length = inputStream.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                inputStream.close();
            }
        }
        zipOut.close();
        outputStream.close();
    }
}
