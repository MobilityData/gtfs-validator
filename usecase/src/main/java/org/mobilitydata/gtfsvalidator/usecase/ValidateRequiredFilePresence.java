package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class ValidateRequiredFilePresence {

    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    public ValidateRequiredFilePresence(final GtfsSpecRepository specRepo,
                                        final RawFileRepository rawFileRepo,
                                        final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    public void execute() {
        if (!rawFileRepo.getFilenameAll().containsAll(specRepo.getRequiredFilenameList())) {

            specRepo.getRequiredFilenameList().stream()
                    .filter(requiredFile -> !rawFileRepo.getFilenameAll().contains(requiredFile))
                    .forEach(missingFile -> resultRepo.addNotice(new MissingRequiredFileNotice(missingFile)));
        }
    }
}
