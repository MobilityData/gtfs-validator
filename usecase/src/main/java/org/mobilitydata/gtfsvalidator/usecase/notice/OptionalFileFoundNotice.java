package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.InfoNotice;

public class OptionalFileFoundNotice extends InfoNotice {

    public OptionalFileFoundNotice(String filename, String noticeId, String title, String description) {
        super(filename, noticeId, title, description);
    }
}