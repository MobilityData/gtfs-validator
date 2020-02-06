package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.exception.MissingRequiredFileException;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.util.stream.Collectors;

public class ValidateRequiredFilePresence {

    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;

    public ValidateRequiredFilePresence(final GtfsSpecRepository specRepo, final RawFileRepository rawFileRepo) {
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
    }

    public void execute() {
        if (!rawFileRepo.getFilenameAll().containsAll(specRepo.getRequiredFilenameList())) {
            throw new MissingRequiredFileException(specRepo.getRequiredFilenameList().stream()
                    .filter(req -> !rawFileRepo.getFilenameAll().contains(req)).collect(Collectors.toList()));
        }
    }
}
