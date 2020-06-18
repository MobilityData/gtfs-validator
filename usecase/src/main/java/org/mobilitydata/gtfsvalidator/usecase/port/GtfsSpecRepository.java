/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase.port;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.relationship_descriptor.RelationshipDescriptor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * This holds information about a GTFS specification and provides methods to execute the validation process.
 */
public interface GtfsSpecRepository {
    List<String> getRequiredFilenameList();

    List<String> getOptionalFilenameList();

    List<String> getRequiredHeadersForFile(final RawFileInfo fileInfo);

    List<String> getOptionalHeadersForFile(final RawFileInfo fileInfo);

    RawEntityParser getParserForFile(final RawFileInfo file);

    ParsedEntityTypeValidator getValidatorForFile(final RawFileInfo file);

    RelationshipDescriptor getGtfsRelationshipDescriptor();

    interface RawEntityParser {
        Collection<ErrorNotice> validateNonStringTypes(final RawEntity toValidate);

        ParsedEntity parse(final RawEntity toParse);
    }

    interface ParsedEntityTypeValidator {
        // TODO: explore if more abstractions should be introduced
        // ie: abstract schema definition through domain entities (column, field)
        // have use case inspect abstracted schema and call validateColor, validateLatitude, validateUrl, ...
        // on GtfsTypeValidator interface
        Collection<Notice> validate(final ParsedEntity toValidate);
    }

    interface RelationshipDescriptorParser {
        RelationshipDescriptor parse(final String gtfsSchemaAsString) throws IOException;
    }
}