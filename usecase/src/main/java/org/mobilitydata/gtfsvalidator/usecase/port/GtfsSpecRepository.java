package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

import java.util.Collection;
import java.util.List;

public interface GtfsSpecRepository {

    List<String> getRequiredFilenameList();

    List<String> getOptionalFilenameList();

    List<String> getRequiredHeadersForFile(final RawFileInfo fileInfo);

    List<String> getOptionalHeadersForFile(final RawFileInfo fileInfo);

    RawEntityParser getParserForFile(RawFileInfo file);

    interface RawEntityParser {
        Collection<ErrorNotice> validateNumericTypes(RawEntity toValidate);

        ParsedEntity parse(RawEntity toParse);
    }
}
