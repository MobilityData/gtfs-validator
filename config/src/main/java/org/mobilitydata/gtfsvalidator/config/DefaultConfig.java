package org.mobilitydata.gtfsvalidator.config;

import org.mobilitydata.gtfsvalidator.db.InMemoryGtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.db.InMemoryRawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.CleanOrCreatePath;
import org.mobilitydata.gtfsvalidator.usecase.DownloadArchiveFromNetwork;
import org.mobilitydata.gtfsvalidator.usecase.UnzipInputArchive;
import org.mobilitydata.gtfsvalidator.usecase.ValidateFileName;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class DefaultConfig {
    private final GtfsSpecRepository specRepo = new InMemoryGtfsSpecRepository("gtfs_spec.asciipb");
    private final RawFileRepository rawFileRepo = new InMemoryRawFileRepository();

    public DownloadArchiveFromNetwork downloadArchiveFromNetwork(final String url, final String targetPath) throws MalformedURLException {
        return new DownloadArchiveFromNetwork(new URL(url), targetPath);
    }

    public CleanOrCreatePath cleanOrCreatePath(final String toCleanOrCreate) {
        return new CleanOrCreatePath(toCleanOrCreate);
    }

    public UnzipInputArchive unzipInputArchive(final String zipInputPath, final Path zipExtractPath) throws IOException {
        return new UnzipInputArchive(rawFileRepo, new ZipFile(zipInputPath), zipExtractPath);
    }

    public ValidateFileName validateFileName() {
        return new ValidateFileName(specRepo, rawFileRepo);
    }

    public DefaultConfig() throws IOException {
    }
}
