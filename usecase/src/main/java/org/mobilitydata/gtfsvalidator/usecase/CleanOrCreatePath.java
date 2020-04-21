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

import org.mobilitydata.gtfsvalidator.usecase.notice.error.CouldNotCleanOrCreatePathNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Use case to create a path, if the target location is not empty, all files at target are deleted. This use case is
 * triggered after parsing the rows of a specific file. The resultant path is used in the subsequent steps to either
 * unzip the GTFS dataset to validate or to write the validation output results.
 */
public class CleanOrCreatePath {

    private final ValidationResultRepository resultRepo;
    private final ExecParamRepository execParamRepo;

    /**
     * @param resultRepo    a repository storing information about the validation
     * @param execParamRepo a repository containing execution parameters
     */
    public CleanOrCreatePath(final ValidationResultRepository resultRepo,
                             final ExecParamRepository execParamRepo) {
        this.resultRepo = resultRepo;
        this.execParamRepo = execParamRepo;
    }

    /**
     * Execution method for use case: creates a path to the target location. If the target location is not
     * * empty, all files at target are deleted. If the process fails, a {@link CouldNotCleanOrCreatePathNotice} is
     * * generated and added to the {@link ValidationResultRepository} provided in the constructor.
     *
     * @return a path to the target location
     */
    public Path execute(String key) {
        final String pathToCleanOrCreate = execParamRepo.getExecParamValue(key);
        Path toCleanOrCreate = Path.of(pathToCleanOrCreate);
        try {
            // to empty any already existing directory
            if (Files.exists(toCleanOrCreate)) {
                //noinspection ResultOfMethodCallIgnored
                Files.walk(toCleanOrCreate).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            Files.createDirectory(toCleanOrCreate);
        } catch (IOException e) {
            resultRepo.addNotice(new CouldNotCleanOrCreatePathNotice(pathToCleanOrCreate));
        }

        return toCleanOrCreate;
    }
}
