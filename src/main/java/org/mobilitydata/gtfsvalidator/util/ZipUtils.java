package org.mobilitydata.gtfsvalidator.util;

/*
 * Copyright (c) 2019. MobilityData IO. All rights reserved
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

    public static void unzip(String zipInputPath, String extractTargetPath) throws IOException {

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
