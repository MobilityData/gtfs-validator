package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;

/**
 * Base class for validators that handle a single entity and do not need more information.
 */
public abstract class SingleEntityValidator<T extends GtfsEntity> {
    public abstract void validate(T entity, NoticeContainer noticeContainer);
}
