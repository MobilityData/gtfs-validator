package org.mobilitydata.gtfsvalidator.usecase.port;

import java.util.List;

public interface GtfsSpecRepository {

    List<String> getRequiredFilenameList();


}
