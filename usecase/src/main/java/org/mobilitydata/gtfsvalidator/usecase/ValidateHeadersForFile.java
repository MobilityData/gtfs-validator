package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.MissingHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.NonStandardHeaderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;
import java.util.List;

//TODO: precondition- all filenames should have been validated
public class ValidateHeadersForFile {

    private final GtfsSpecRepository specRepo;
    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    public ValidateHeadersForFile(final GtfsSpecRepository specRepo,
                                  final RawFileInfo rawFileInfo,
                                  final RawFileRepository rawFileRepo,
                                  final ValidationResultRepository resultRepo
    ) {
        this.specRepo = specRepo;
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    public void execute() {
        List<String> expectedRequiredHeaderList = specRepo.getRequiredHeadersForFile(rawFileInfo);
        List<String> expectedOptionalHeaderList = specRepo.getOptionalHeadersForFile(rawFileInfo);
        Collection<String> actualHeaderList = rawFileRepo.getActualHeadersForFile(rawFileInfo);

        //Missing headers
        expectedRequiredHeaderList.stream()
                .filter(expectedHeader -> !(actualHeaderList.contains(expectedHeader)))
                .forEach(missingHeader -> resultRepo.addNotice(new MissingHeaderNotice(rawFileInfo.getFilename(), missingHeader)));

        //Extra headers
        actualHeaderList.stream()
                .filter(header -> !expectedOptionalHeaderList.contains(header) && !expectedRequiredHeaderList.contains(header))
                .forEach(extraHeader -> resultRepo.addNotice(new NonStandardHeaderNotice(rawFileInfo.getFilename(), extraHeader)));

    }
}