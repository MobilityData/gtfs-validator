package org.mobilitydata.gtfsvalidator.domain.entity;

public class RawFileInfo {

    private String filename;
    //...

    private RawFileInfo(final String filename) {
        this.filename = filename;
    }

    public static RawFileBuilder builder() {
        return new RawFileBuilder();
    }


    public static class RawFileBuilder {
        private String filename;

        public RawFileBuilder() {
        }

        public RawFileBuilder filename(final String filename) {
            this.filename = filename;
            return this;
        }

        public RawFileInfo build() {
            return new RawFileInfo(filename);
        }
    }

    public String getFilename() {
        return filename;
    }
}
