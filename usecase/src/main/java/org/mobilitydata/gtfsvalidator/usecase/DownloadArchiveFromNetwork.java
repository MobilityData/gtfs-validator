package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.exception.DownloadException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadArchiveFromNetwork {

    private final URL sourceUrl;
    private final String targetPath;

    public DownloadArchiveFromNetwork(URL url, String targetPath) {
        this.sourceUrl = url;
        this.targetPath = targetPath;
    }

    public void execute() {
        //TODO: does using File class break clean architecture (make business logic dependant on a framework)?
        //Should the call to File happen in outside layers?
        try {
            Files.copy(
                    sourceUrl.openStream(),
                    Paths.get(targetPath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new DownloadException(e);
        }
    }
}
