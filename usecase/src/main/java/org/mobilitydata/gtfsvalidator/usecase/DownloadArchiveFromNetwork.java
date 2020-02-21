package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.usecase.notice.CannotDownloadArchiveFromNetworkNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadArchiveFromNetwork {

    private final URL sourceUrl;
    private final String targetPath;
    private final ValidationResultRepository resultRepo;


    public DownloadArchiveFromNetwork(final URL url,
                                      final String targetPath,
                                      final ValidationResultRepository resultRepo) {
        this.sourceUrl = url;
        this.targetPath = targetPath;
        this.resultRepo = resultRepo;
    }

    public void execute() {
        //TODO: does using File class break clean architecture (make business logic dependant on a framework)?
        //Should the call to File happen in outside layers?
        try {
            Files.copy(
                    sourceUrl.openStream(), // TODO: reflechir a comment enlever Files (dependane) : piste: inoutStream,
                    // stream, on pourrait utiliser un parser, file copier que les use cas eutilsient pour faire copy,
                    // lui utiliserait file dans les autres couches d'abstraction.
                    Paths.get(targetPath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            resultRepo.addNotice(new CannotDownloadArchiveFromNetworkNotice(sourceUrl));
        }
    }
}