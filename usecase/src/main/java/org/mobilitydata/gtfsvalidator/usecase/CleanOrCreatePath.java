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

import org.mobilitydata.gtfsvalidator.usecase.notice.CouldNotCleanOrCreatePathNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Use case to create a path
 */
public class CleanOrCreatePath {

    private final String pathToCleanOrCreate;
    private final ValidationResultRepository resultRepo;

    /**
     * @param toCleanOrCreate a string object specifying the target location
     * @param resultRepo      an instance of {@link ValidationResultRepository} storing information about the validation
     */
    public CleanOrCreatePath(final String toCleanOrCreate,
                             final ValidationResultRepository resultRepo) {
        this.pathToCleanOrCreate = toCleanOrCreate;
        this.resultRepo = resultRepo;
    }

    /**
     * Execution method for use case: creates a {@link Path} to the target location. If the target location is not
     * empty, all files at target are deleted.
     *
     * @return a {@code Path} instance
     */
    public Path execute() {
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
