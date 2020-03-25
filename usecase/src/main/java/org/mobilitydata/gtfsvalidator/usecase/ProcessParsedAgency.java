package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.usecase.entity.Agency;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class ProcessParsedAgency {
    private final GtfsSpecRepository gtfsSpecRepository;
    private final ValidationResultRepository resultRepository;
    private GtfsDataRepository gtfsDataRepository;

    public ProcessParsedAgency(GtfsSpecRepository gtfsSpecRepository, ValidationResultRepository resultRepository,
                               GtfsDataRepository gtfsDataRepository) {
        this.gtfsSpecRepository = gtfsSpecRepository;
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
    }

    public Agency.AgencyBuilder getBuilder(String agencyName, String agencyUrl, String agencyTimezone) {
        return new Agency.AgencyBuilder(agencyName, agencyUrl, agencyTimezone);
    }

    public void execute(final ParsedEntity validatedAgencyEntity) {

        String agencyId = (String) validatedAgencyEntity.get("agency_id");
        String agencyName = (String) validatedAgencyEntity.get("agency_name");
        String agencyUrl = (String) validatedAgencyEntity.get("agency_url");
        String agencyTimezone = (String) validatedAgencyEntity.get("agency_timezone");
        String agencyLang = (String) validatedAgencyEntity.get("agency_lang");
        String agencyPhone = (String) validatedAgencyEntity.get("agency_phone");
        String agencyFareUrl = (String) validatedAgencyEntity.get("agency_fare_url");
        String agencyEmail = (String) validatedAgencyEntity.get("agency_email");

        Agency.AgencyBuilder builder = getBuilder(agencyName, agencyUrl, agencyTimezone);

        builder.agencyId(agencyId)
                .agencyLang(agencyLang)
                .agencyPhone(agencyPhone)
                .agencyFareUrl(agencyFareUrl)
                .agencyEmail(agencyEmail);

        Agency agency = builder.build();

        gtfsDataRepository.addEntity(agency);
    }
}