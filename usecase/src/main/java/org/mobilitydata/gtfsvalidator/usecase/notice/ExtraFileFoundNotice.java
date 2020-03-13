package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.WarningNotice;

public class ExtraFileFoundNotice extends WarningNotice {

    public ExtraFileFoundNotice(String filename) {
        super(filename, W_004, "Non standard file found", "Extra file " + filename + " found in archive");
    }
}