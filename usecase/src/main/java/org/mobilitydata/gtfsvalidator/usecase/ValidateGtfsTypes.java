package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

public class ValidateGtfsTypes {

    private final GtfsSpecRepository specRepo;
    private final ValidationResultRepository resultRepo;

    public ValidateGtfsTypes(final GtfsSpecRepository specRepo,
                             final ValidationResultRepository resultRepo) {
        this.specRepo = specRepo;
        this.resultRepo = resultRepo;
    }

    public void execute(final Collection<ParsedEntity> toValidate) {
        toValidate.forEach(parsedEntity -> specRepo.getValidatorForFile(parsedEntity.getRawFileInfo()).validate(parsedEntity).forEach(resultRepo::addNotice));
    }
}
