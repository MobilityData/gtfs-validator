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

import org.mobilitydata.gtfsvalidator.usecase.port.ExecParamRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Use case to create a path, if the target location is not empty, all files at target are deleted. This use case is
 * triggered after parsing the rows of a specific file. The resultant path is used in the subsequent steps to either
 * unzip the GTFS dataset to validate or to write the validation output results.
 */
public class CreatePath {

    private final ExecParamRepository execParamRepo;

    /**
     * @param execParamRepo a repository containing execution parameters
     */
    public CreatePath(final ExecParamRepository execParamRepo) {
        this.execParamRepo = execParamRepo;
    }

    /**
     * Execution method for use case: creates a path to the target location. If the target location is not empty, all
     * files at target are deleted.
     *
     * @return a path to the target location
     */
    public Path execute(String key, Boolean clearIfExists) {
        final String pathToCleanOrCreate = execParamRepo.getExecParamValue(key);
        Path toCleanOrCreate = Path.of(pathToCleanOrCreate);
        // to empty any already existing directory
        if (Files.exists(toCleanOrCreate)) {
            if (clearIfExists) {
                try {
                    //noinspection ResultOfMethodCallIgnored -- we ignore if deletion went well or not
                    Files.walk(toCleanOrCreate).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Create the directory
            try {
                Files.createDirectory(toCleanOrCreate);
            } catch (AccessDeniedException e) {
                // Wait and try again - Windows can initially block creating a directory immediately after a delete when a file lock exists (#112)
                try {
                    Thread.sleep(500);
                    Files.createDirectory(toCleanOrCreate);
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return toCleanOrCreate;
    }
}
