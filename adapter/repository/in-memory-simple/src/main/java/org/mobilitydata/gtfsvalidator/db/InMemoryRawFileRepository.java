package org.mobilitydata.gtfsvalidator.db;

import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.util.*;

public class InMemoryRawFileRepository implements RawFileRepository {

    private final Map<String, RawFileInfo> fileInfoPerFilename = new HashMap<>();

    @Override
    public RawFileInfo create(RawFileInfo fileInfo) {
        fileInfoPerFilename.put(fileInfo.getFilename(), fileInfo);
        return fileInfo;
    }

    @Override
    public Optional<RawFileInfo> findByName(String filename) {
        return Optional.ofNullable(fileInfoPerFilename.get(filename));
    }

    @Override
    public Set<String> getFilenameAll() {
        return fileInfoPerFilename.keySet();
    }
}
