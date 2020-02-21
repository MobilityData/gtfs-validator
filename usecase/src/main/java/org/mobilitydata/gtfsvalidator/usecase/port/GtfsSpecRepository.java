package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;

import java.util.Collection;
import java.util.List;

public interface GtfsSpecRepository {

    List<String> getRequiredFilenameList();

    List<String> getOptionalFilenameList();

    List<String> getRequiredHeadersForFile(final RawFileInfo fileInfo);

    List<String> getOptionalHeadersForFile(final RawFileInfo fileInfo);

    RawEntityParser getParserForFile(RawFileInfo file);

    ParsedEntityTypeValidator getValidatorForFile(RawFileInfo file);

    interface RawEntityParser {
        Collection<ErrorNotice> validateNumericTypes(RawEntity toValidate);

        ParsedEntity parse(RawEntity toParse);
    }

    interface ParsedEntityTypeValidator {
        //TODO: explore if more abstractions should be introduced
        //ie: abstract schema definition through domain entities (column, field)
        //have use case inspect abstracted schema and call validateColor, validateLatitude, validateUrl, ...
        //on GtfsTypeValidator interface
        Collection<Notice> validate(ParsedEntity toValidate);
    }
}
