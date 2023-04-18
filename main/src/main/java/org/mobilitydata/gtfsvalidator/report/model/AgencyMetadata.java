package org.mobilitydata.gtfsvalidator.report.model;

import org.mobilitydata.gtfsvalidator.table.GtfsAgency;

public class AgencyMetadata {
    public final String name;
    public final String url;
    public final String phone;
    public final String email;

    public AgencyMetadata(String name, String url, String phone, String email) {

        this.name = name;
        this.url = url;
        this.phone = phone.isEmpty() ? "N/A" : phone;
        this.email = email.isEmpty() ? "N/A" : email;
    }

    public static AgencyMetadata from(GtfsAgency agency) {
        return new AgencyMetadata(agency.agencyName(), agency.agencyUrl(), agency.agencyPhone(), agency.agencyEmail());
    }
}
