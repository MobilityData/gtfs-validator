package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.CouldNotCleanOrCreatePathNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class CleanOrCreatePath {

    private final String pathToCleanOrCreate;
    private final ValidationResultRepository resultRepo;

    public CleanOrCreatePath(final String toCleanOrCreate,
                             final ValidationResultRepository resultRepo) {
        this.pathToCleanOrCreate = toCleanOrCreate;
        this.resultRepo = resultRepo;
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
            resultRepo.addNotice(new CouldNotCleanOrCreatePathNotice(pathToCleanOrCreate));
        }

        return toCleanOrCreate;
    }
}
