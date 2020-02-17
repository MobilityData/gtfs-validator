package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;

import java.util.List;

public interface GtfsSpecRepository {

    List<String> getRequiredFilenameList();

    List<String> getOptionalFilenameList();

    List<String> getRequiredHeadersForFile(final RawFileInfo fileInfo);

    List<String> getOptionalHeadersForFile(final RawFileInfo fileInfo);
}
