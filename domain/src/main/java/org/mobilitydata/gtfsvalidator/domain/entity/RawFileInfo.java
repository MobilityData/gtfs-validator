package org.mobilitydata.gtfsvalidator.domain.entity;

public class RawFileInfo {

    private final String filename;
    private final String path;

    private RawFileInfo(final String filename, final String path) {
        this.filename = filename;
        this.path = path;
    }

    public static RawFileInfoBuilder builder() {
        return new RawFileInfoBuilder();
    }

    public static class RawFileInfoBuilder {
        private String filename;
        private String path;

        public RawFileInfoBuilder() {
        }

        public RawFileInfoBuilder filename(final String filename) {
            this.filename = filename;
            return this;
        }

        public RawFileInfoBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public RawFileInfo build() {
            return new RawFileInfo(filename, path);
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }
}
