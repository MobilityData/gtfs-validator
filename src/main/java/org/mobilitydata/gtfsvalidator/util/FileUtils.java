package org.mobilitydata.gtfsvalidator.util;

/*
 * Copyright (c) 2019. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    public static void copyZipFromNetwork(String url, String zipInputPath) throws IOException {

        Files.copy(
                new URL(url).openStream(),
                Paths.get(zipInputPath),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public static Path cleanOrCreatePath(String pathToCleanOrCreate) throws IOException {

        Path toCleanOrCreate = Path.of(pathToCleanOrCreate);

        // to empty any already existing directory
        if (Files.exists(toCleanOrCreate)) {
            //noinspection ResultOfMethodCallIgnored
            Files.walk(toCleanOrCreate).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }

        Files.createDirectory(toCleanOrCreate);

        return toCleanOrCreate;
    }

    public static void unzip(String zipInputPath, String extractTargetPath) throws IOException {

        Path unzipPath = cleanOrCreatePath(extractTargetPath);

        try (var zf = new ZipFile(zipInputPath)) {
            Enumeration<? extends ZipEntry> zipEntries = zf.entries();
            zipEntries.asIterator().forEachRemaining(entry -> {
                try {
                    if (entry.isDirectory()) {
                        throw new IOException("input zip must not contain any folder");
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
