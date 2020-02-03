package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.exception.UnzipException;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipInputArchive {

    private final RawFileRepository rawFileRepo;
    private final ZipFile inputZip;
    private final Path zipExtractPath;

    public UnzipInputArchive(final RawFileRepository fileRepo, final ZipFile inputZip, final Path zipExtractPath) {
        this.rawFileRepo = fileRepo;
        this.inputZip = inputZip;
        this.zipExtractPath = zipExtractPath;
    }

    public void execute() {

        Enumeration<? extends ZipEntry> zipEntries = inputZip.entries();
        zipEntries.asIterator().forEachRemaining(entry -> {
            try {
                if (entry.isDirectory()) {
                    throw new IOException("input zip must not contain any folder");
                } else {
                    Path fileToCreate = zipExtractPath.resolve(entry.getName());
                    Files.copy(inputZip.getInputStream(entry), fileToCreate);
                    rawFileRepo.create(new RawFileInfo.RawFileBuilder().filename(entry.getName()).build());
                }
            } catch (IOException e) {
                throw new UnzipException(e);
            }
        });
    }
}
