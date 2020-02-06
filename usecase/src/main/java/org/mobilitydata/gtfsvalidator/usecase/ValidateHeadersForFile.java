package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.MissingHeadersNotice;
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
        List<String> expectedHeaderList = specRepo.getExpectedHeadersForFile(rawFileInfo);
        Collection<String> actualHeaderList = rawFileRepo.getActualHeadersForFile(rawFileInfo);

        if (!actualHeaderList.containsAll(expectedHeaderList)) {
            resultRepo.addNotice(new MissingHeadersNotice(rawFileInfo.getFilename(), expectedHeaderList, actualHeaderList));
        } else {
            //TODO: output warning notices for non standards headers
        }
    }
}
