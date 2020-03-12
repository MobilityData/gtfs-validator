package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.OptionalFileFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

public class ValidateAllOptionalFileName {

    private final GtfsSpecRepository specRepo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;
    private final List<String> filenameList;

    public ValidateAllOptionalFileName(final GtfsSpecRepository specRepo, final RawFileRepository rawFileRepo,
                                       final ValidationResultRepository resultRepo, List<String> filenameList) {
        this.specRepo = specRepo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
        this.filenameList = filenameList;
    }

    public List<String> execute() {

        specRepo.getOptionalFilenameList().stream()
                .filter(optionalFilename -> rawFileRepo.getFilenameAll().contains(optionalFilename))
                .forEach(optionalFilename -> {
                            filenameList.add(optionalFilename);
                            resultRepo.addNotice(new OptionalFileFoundNotice(optionalFilename,
                                    "I002",
                                    "Optional file found",
                                    "Optional file " + optionalFilename +
                                            " found in archive"));
                        }
                );
        return filenameList;
    }
}