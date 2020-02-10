package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

//TODO: precondition- all filenames should have been validated
public class ValidateAllRowLengthForFile {

    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final ValidationResultRepository resultRepo;

    public ValidateAllRowLengthForFile(final RawFileInfo rawFileInfo,
                                       final RawFileRepository rawFileRepo,
                                       final ValidationResultRepository resultRepo) {
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.resultRepo = resultRepo;
    }

    public void execute() {
        rawFileRepo.getProviderForFile(rawFileInfo).ifPresentOrElse(
                provider -> {
                    while (provider.hasNext()) {
                        RawEntity rawEntity = provider.getNext();
                        if (rawEntity.size() != provider.getHeaderCount()) {
                            resultRepo.addNotice(new InvalidRowLengthNotice(
                                    rawFileInfo.getFilename(),
                                    rawEntity.getIndex(),
                                    provider.getHeaderCount(),
                                    rawEntity.size())
                            );
                        }
                    }
                },
                () -> resultRepo.addNotice(new CannotConstructDataProviderNotice(rawFileInfo.getFilename()))
        );
    }
}
