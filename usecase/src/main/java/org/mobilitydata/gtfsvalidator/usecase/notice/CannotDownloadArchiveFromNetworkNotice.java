package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

import java.net.URL;

public class CannotDownloadArchiveFromNetworkNotice extends ErrorNotice {
    public CannotDownloadArchiveFromNetworkNotice(final URL url) {
        super(String.valueOf(url),
                "E007",
                "Downloading error",
                "An error occurred while trying to download archive from network: " + url);
    }
}