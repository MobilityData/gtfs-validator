package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;

public class InputZipContainsFolderNotice extends WarningNotice {
    public InputZipContainsFolderNotice(final String filename, final String folderName) {
        super(filename,
                "W009",
                "Non empty folder",
                "File: " + filename + "contains folder named: " + folderName);
    }
}