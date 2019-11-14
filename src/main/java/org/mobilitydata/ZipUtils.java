package org.mobilitydata;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipUtils {

    static void unzip(String zipInputPath, String extractTargetPath) throws IOException {

        Path unzipPath = Path.of(extractTargetPath);

        // to empty any already existing directory
        if(Files.exists(unzipPath)){
            //noinspection ResultOfMethodCallIgnored
            Files.walk(unzipPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            Files.createDirectory(unzipPath);
        }

        try (var zf = new ZipFile(zipInputPath)) {
            Enumeration<? extends ZipEntry> zipEntries = zf.entries();
            zipEntries.asIterator().forEachRemaining(entry -> {
                try {
                    if (entry.isDirectory()) {
                        var dirToCreate = unzipPath.resolve(entry.getName());
                    } else {
                        var fileToCreate = unzipPath.resolve(entry.getName());
                        Files.copy(zf.getInputStream(entry), fileToCreate);
                    }
                } catch(IOException ei) {
                    ei.printStackTrace();
                }
            });
        }
    }
}
