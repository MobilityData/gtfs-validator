package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotConstructDataProviderNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO: precondition- all filenames should have been validated
public class ParseAllRowForFile {

    private final RawFileInfo rawFileInfo;
    private final RawFileRepository rawFileRepo;
    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;

    public ParseAllRowForFile(final RawFileInfo rawFileInfo,
                              final RawFileRepository rawFileRepo,
                              final GtfsSpecRepository specRepo,
                              final ValidationResultRepository resultRepo) {
        this.rawFileInfo = rawFileInfo;
        this.rawFileRepo = rawFileRepo;
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
    }

    public Collection<ParsedEntity> execute() {

        List<ParsedEntity> toReturn = new ArrayList<>();

        rawFileRepo.getProviderForFile(rawFileInfo).ifPresentOrElse(
                provider -> {
                    GtfsSpecRepository.RawEntityParser parser = specRepo.getParserForFile(rawFileInfo);

                    while (provider.hasNext()) {
                        RawEntity rawEntity = provider.getNext();

                        parser.validateNumericTypes(rawEntity).forEach(resultRepo::addNotice);

                        toReturn.add(parser.parse(rawEntity));
                    }
                },
                () -> resultRepo.addNotice(new CannotConstructDataProviderNotice(rawFileInfo.getFilename()))
        );

        return toReturn;
    }
}
