package org.mobilitydata.gtfsvalidator.db;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FromFileRawEntityProvider implements RawFileRepository.RawEntityProvider {
    private final MappingIterator<Map<String, String>> dataSource;
    private final int headerCount;

    public FromFileRawEntityProvider(RawFileInfo file) throws IOException {
        File csvFile = new File(file.getPath() + File.separator + file.getFilename());
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        dataSource = mapper.readerFor(Map.class)
                .with(schema)
                .readValues(csvFile);

        final int[] headerCountArray = {0};

        ((CsvSchema) (dataSource.getParser().getSchema())).iterator().forEachRemaining(column -> ++headerCountArray[0]);

        headerCount = headerCountArray[0];
    }

    @Override
    public boolean hasNext() {
        return dataSource.hasNext();
    }

    @Override
    public RawEntity getNext() {
        return new RawEntity(dataSource.next(), dataSource.getCurrentLocation().getLineNr());
    }

    @Override
    public int getHeaderCount() {
        return headerCount;
    }
}
