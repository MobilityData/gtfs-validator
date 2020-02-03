package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.exception.PathCleaningOrCreationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class CleanOrCreatePath {

    private final String pathToCleanOrCreate;

    public CleanOrCreatePath(final String toCleanOrCreate) {
        this.pathToCleanOrCreate = toCleanOrCreate;
    }

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
            throw new PathCleaningOrCreationException(e);
        }

        return toCleanOrCreate;
    }
}
