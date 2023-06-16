package org.mobilitydata.gtfsvalidator.testing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MockGtfs {
  private final File file;

  private final Map<String, byte[]> _fileContentsByName = new HashMap<>();

  private MockGtfs(File file) {
    this.file = file;
  }

  public static MockGtfs create() throws IOException {
    File file = File.createTempFile("MockGtfs-", ".zip");
    file.deleteOnExit();
    return new MockGtfs(file);
  }

  public File getFile() {
    return file;
  }

  public Path getPath() {
    return file.toPath();
  }

  public void putFileFromString(String fileName, String content) {
    _fileContentsByName.put(fileName, content.getBytes(StandardCharsets.UTF_8));
    updateZipContents();
  }

  public void putFileFromLines(String fileName, String... lines) {
    putFileFromString(fileName, String.join("\n", lines));
  }

  private void updateZipContents() {
    try {
      if (file.exists()) {
        file.delete();
      }
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
      for (Map.Entry<String, byte[]> entry : _fileContentsByName.entrySet()) {
        String fileName = entry.getKey();
        byte[] content = entry.getValue();
        ZipEntry zipEntry = new ZipEntry(fileName);
        out.putNextEntry(zipEntry);
        out.write(content);
        out.closeEntry();
      }
      out.close();
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
