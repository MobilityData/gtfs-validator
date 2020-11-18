package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * Interface for validators that handle one as a whole or several files.
 */
public abstract class FileValidator {
    public abstract void validate(NoticeContainer noticeContainer);
}
