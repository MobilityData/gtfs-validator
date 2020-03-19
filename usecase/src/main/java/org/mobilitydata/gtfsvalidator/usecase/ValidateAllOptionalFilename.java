package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.ExtraFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case to validate the presence of optional files. This use case is triggered after validating the presence of
 * all required files. In the validation process if a file is not marked as required or optional the validation
 * process fails.
 */
public class ValidateAllOptionalFilename {

    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    /**
     * @param specRepo    a repository holding information about the GTFS specification
     * @param rawFileRepo a repository containing information about a GTFS dataset
     * @param resultRepo  a repository containing information about the validation process
     */
    public ValidateAllOptionalFilename(final GtfsSpecRepository specRepo, final RawFileRepository rawFileRepo,
                                       final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    /**
     * Returns the list of files marked as "optional" in the GTFS specification that were found in the GTFS
     * dataset to validate.
     *
     * @return the filename list of encountered optional files
     */
    public List<String> execute() {

        List<String> toReturn = new ArrayList<>();

        List<String> specOptionalFileList = specRepo.getOptionalFilenameList();
        List<String> specRequiredFileList = specRepo.getRequiredFilenameList();

        rawFileRepo.getFilenameAll().stream()
                .filter(filename -> !specOptionalFileList.contains(filename) && !specRequiredFileList.contains(filename))
                .forEach(extraFilename -> resultRepo.addNotice(new ExtraFileFoundNotice(extraFilename)));

        rawFileRepo.getFilenameAll().stream()
                .filter(specOptionalFileList::contains)
                .forEach(toReturn::add);

        return toReturn;
    }
}