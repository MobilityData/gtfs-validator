package org.mobilitydata.gtfsvalidator.db;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.File;
import java.io.IOException;
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
    public Collection<String> getActualHeadersForFile(RawFileInfo file) {
        //use jackson to open the file and see what API we have
        File csvFile = new File(file.getPath() + File.separator + file.getFilename());
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        Collection<String> toReturn = new HashSet<>();

        try {
            ((CsvSchema) (mapper.readerFor(Map.class)
                    .with(schema)
                    .readValues(csvFile).getParser().getSchema())).iterator().forEachRemaining(column -> toReturn.add(column.getName()));
        } catch (IOException e) {
            //TODO: this should go back up to use case level so it can be properly reported
            return Collections.emptySet();
        }

        return toReturn;
    }

    @Override
    public Set<String> getFilenameAll() {
        return fileInfoPerFilename.keySet();
    }
}
