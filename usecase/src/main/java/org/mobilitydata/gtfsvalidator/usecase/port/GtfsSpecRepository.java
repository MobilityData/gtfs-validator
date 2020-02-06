package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;

import java.util.List;

public interface GtfsSpecRepository {

    List<String> getRequiredFilenameList();

    List<String> getExpectedHeadersForFile(final RawFileInfo fileInfo);


}
